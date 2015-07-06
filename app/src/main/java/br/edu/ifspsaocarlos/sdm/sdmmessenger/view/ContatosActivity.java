package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.ContatoArrayAdapter;

public class ContatosActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private MeuPerfil meuPerfil;
    private ProgressDialog progressDialog;
    private ContatoArrayAdapter adapter;
    private ContatoDAO contatoDAO;
    private ListView lvContatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        meuPerfil = MeuPerfil.getInstance();

        contatoDAO = new ContatoDAO(this);
        contatoDAO.open();

        lvContatos = (ListView) findViewById(R.id.lvContatos);
        lvContatos.setOnItemClickListener(this);

        progressDialog = new ProgressDialog(this);

        buildListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contatoDAO != null) {
            contatoDAO.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contatos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_atualizarContatos) {
            atualizarContatos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contato contato = adapter.getItem(position);
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("contato", contato);
        startActivity(i);
        finish();
    }

    private void buildListView() {
        List<Contato> contatos = contatoDAO.selectAll();

        if (adapter == null)
            adapter = new ContatoArrayAdapter(this, contatos);

        if (contatos.size() == 0)
            atualizarContatos();

        lvContatos.setAdapter(adapter);
    }

    private void atualizarContatos() {
        progressDialog.setTitle("Atualizando lista de contatos");
        progressDialog.setMessage("baixando contatos, por favor aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            RequestQueue queue = Volley.newRequestQueue(ContatosActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(
                    JsonObjectRequest.Method.GET,
                    getString(R.string.app_ws_urlContato),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int inseridos = 0;
                            int atualizados = 0;
                            Contato contato = new Contato();
                            ContatoDAO dao = new ContatoDAO(ContatosActivity.this);

                            try {
                                dao.open();
                                JSONArray arrayJsonContatos = jsonObject.getJSONArray("contatos");
                                for (int i = 0; i < arrayJsonContatos.length(); i++) {
                                    JSONObject jsonContato = arrayJsonContatos.getJSONObject(i);
                                    if (Contato.isContatoDesteApp(jsonContato)) {
                                        contato.populate(jsonContato);
                                        if (contato.getId() == meuPerfil.getId())
                                            continue;
                                        else if (dao.findById(contato.getId()) == null) {
                                            dao.insert(contato);
                                            inseridos++;
                                        } else {
                                            if (dao.update(contato));
                                            atualizados++;
                                        }
                                    }
                                }

                                adapter.clear();
                                adapter.addAll(dao.selectAll());

                                String toastMsg;
                                toastMsg  = (inseridos == 0) ? "Nenhum novo contato" : (inseridos == 1) ? "Um novo contato inserido" : inseridos + " novos contatos inseridos";
                                //toastMsg  = (inseridos == 0) ? "Nenhum novo contato e " : (inseridos == 1) ? "Um novo contato inserido e " : inseridos + " novos contatos inseridos e ";
                                //toastMsg += (atualizados == 0) ? "nenhum contato atualizado." : (atualizados == 1) ? "um contato atualizado." : atualizados + " contatos atualizados.";

                                Toast.makeText(ContatosActivity.this, toastMsg, Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(ContatosActivity.this, "Falha ao atualizar lista de contatos. Tente novamente!", Toast.LENGTH_LONG).show();
                                Log.e(getString(R.string.app_name), ex.getMessage());
                            } finally {
                                dao.close();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(ContatosActivity.this, "Falha ao recuperar lista de contatos. Tente novamente!", Toast.LENGTH_LONG).show();
                            Log.e(getString(R.string.app_name), volleyError.getMessage());
                            progressDialog.dismiss();
                        }
                    }
            );
            queue.add(request);
        } catch (Exception ex) {
            Toast.makeText(this, "Falha ao atualizar lista de contatos. Tente novamente!", Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.app_name), ex.getMessage());
            progressDialog.dismiss();
        }
    }
}
