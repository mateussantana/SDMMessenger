package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.NotificationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.MensagemArrayAdapter;

public class ChatActivity extends ActionBarActivity implements View.OnClickListener {

    private MeuPerfil meuPerfil;
    private Contato contato;

    private EditText etMensagem;
    private ImageView btnEnviar;
    private ListView lvMensagens;

    private MensagemDAO mensagemDAO;
    private MensagemArrayAdapter adapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!getIntent().hasExtra("contato")) {
            finish();
            return;
        }

        meuPerfil = MeuPerfil.getInstance();
        contato = (Contato) getIntent().getSerializableExtra("contato");
        setTitle(contato.getNome());

        lvMensagens = (ListView) findViewById(R.id.lvMensagens);
        lvMensagens.setDivider(null);

        mensagemDAO = new MensagemDAO(this);
        requestQueue = Volley.newRequestQueue(this);

        etMensagem = (EditText) findViewById(R.id.etMensagem);
        btnEnviar = (ImageView) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);

        buildListView();
    }

    @Override
    public void onClick(View v) {
        String txtMsg = etMensagem.getText().toString();
        Mensagem mensagem = new Mensagem(meuPerfil.getId(), contato.getId(), "", txtMsg);
        enviarMensagem(mensagem);
        adapter.add(mensagem);
        etMensagem.setText("");
    }

    private void buildListView() {
        MensagemDAO dao = new MensagemDAO(this);
        List<Mensagem> mensagens;
        try {
            dao.open();
            mensagens = dao.selectMensagens(contato);
            if (adapter == null) adapter = new MensagemArrayAdapter(this, mensagens);
            lvMensagens.setAdapter(adapter);
        } catch (Exception ex) {
            Log.e(getString(R.string.app_name), ex.getMessage());
            Toast.makeText(this, "Falha ao recuperar mensagens do banco de dados", Toast.LENGTH_SHORT).show();
        } finally {
            dao.close();
        }
    }

    private void enviarMensagem(final Mensagem mensagem) {
        String url = getString(R.string.app_ws_urlMensagem);
        JSONObject msgJsonObject = mensagem.getJsonObject();

        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    JsonObjectRequest.Method.POST,
                    url,
                    msgJsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                mensagem.populate(jsonObject);
                                mensagemDAO.open();
                                mensagemDAO.insert(mensagem);
                            } catch (Exception ex) {
                                Log.e(getString(R.string.app_name), ex.getMessage());
                            } finally {
                                mensagemDAO.close();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(ChatActivity.this, "Falha ao enviar mensagem", Toast.LENGTH_LONG).show();
                            Log.e(getString(R.string.app_name), "Falha ao enviar mensagem" + volleyError.getMessage());
                        }
                    }
            );
            requestQueue.add(request);
        } catch (Exception ex) {
            Toast.makeText(this, "Falha ao enviar mensagem", Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.app_name), "Falha ao enviar mensagem" + ex.getMessage());
        }
    }
}
