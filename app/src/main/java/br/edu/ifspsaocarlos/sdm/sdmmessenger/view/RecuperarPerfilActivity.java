package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;

public class RecuperarPerfilActivity extends ActionBarActivity {

    private Intent intent;
    private MeuPerfil meuPerfil;
    private EditText etId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_perfil);

        etId = (EditText) findViewById(R.id.etId);
        progressDialog = new ProgressDialog(this);
        meuPerfil = MeuPerfil.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recuperar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_recuperarPerfil) {
            buscarContato();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buscarContato() {

        final Contato contato = new Contato();
        String strId = etId.getText().toString().trim();
        int intId;
        String url;

        if (strId.length() < 1) {
            Toast.makeText(this, "Por favor, insira o ID do perfil.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            intId = Integer.parseInt(strId);
            if (intId < 1) throw new Exception("O número digitado não é um inteiro positivo.");
        } catch (Exception ex) {
            Toast.makeText(this, "Por favor, insira um número inteiro positivo.", Toast.LENGTH_SHORT).show();
            Log.e(getString(R.string.app_name), ex.getMessage());
            return;
        }

        url = getString(R.string.app_ws_urlContato) + "/" + intId;
        progressDialog.setTitle("Recuperando perfil");
        progressDialog.setMessage("por favor, aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            RequestQueue queue = Volley.newRequestQueue(RecuperarPerfilActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(
                    JsonObjectRequest.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if (Contato.isContatoDesteApp(jsonObject)) {
                                contato.populate(jsonObject);
                                if (contato.getId() > 0) {
                                    meuPerfil.populate(jsonObject);
                                    meuPerfil.salvarPerfil(RecuperarPerfilActivity.this);
                                    intent = new Intent(RecuperarPerfilActivity.this, ConversasActivity.class);
                                    startActivity(intent);
                                    setResult(CriarPerfilActivity.ACTIVITY_RESULT_FINISH);
                                    finish();

                                } else
                                    Toast.makeText(RecuperarPerfilActivity.this, "Perfil inválido.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RecuperarPerfilActivity.this, "Este perfil não pertence ao app SDMessenger.", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(RecuperarPerfilActivity.this, "Perfil inexistente.", Toast.LENGTH_SHORT).show();
                            Log.e(getString(R.string.app_name), volleyError.getMessage());
                            progressDialog.dismiss();
                        }
                    }
            );
            queue.add(request);
        } catch (Exception ex) {
            Toast.makeText(this, "Falha ao recuperar perfil. Tente novamente!", Toast.LENGTH_SHORT).show();
            Log.e(getString(R.string.app_name), ex.getMessage());
            progressDialog.dismiss();
        }

    }
}
