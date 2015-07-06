package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;

public class CriarPerfilActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int ACTIVITY_RESULT_FINISH = 78289;
    public static final int ACTIVITY_REQUEST_RECUPERAR_PERFIL = 12456;

    private MeuPerfil perfil;
    private TextView tvRecuperarPerfil;
    private EditText etNome, etApelido;
    private ProgressDialog progressDialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_perfil);

        perfil = MeuPerfil.getInstance();

        tvRecuperarPerfil = (TextView) findViewById(R.id.tvPossuiPerfil);
        tvRecuperarPerfil.setOnClickListener(this);

        etNome = (EditText) findViewById(R.id.etNome);
        etApelido = (EditText) findViewById(R.id.etApelido);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_criar_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cadastrarPerfil) {
            cadastrarNovoPerfil();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == tvRecuperarPerfil) {
            Intent intent = new Intent(this, RecuperarPerfilActivity.class);
            startActivityForResult(intent, this.ACTIVITY_REQUEST_RECUPERAR_PERFIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.ACTIVITY_REQUEST_RECUPERAR_PERFIL && resultCode == this.ACTIVITY_RESULT_FINISH)
            finish();
    }

    private void cadastrarNovoPerfil() {
        String nome = etNome.getText().toString().trim();
        String apelido = etApelido.getText().toString().trim();

        if (nome.length() < 5) {
            Toast.makeText(this, "Por favor, insira o seu nome completo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apelido.length() < 3) {
            Toast.makeText(this, "Por favor, insira um apelido.", Toast.LENGTH_SHORT).show();
            return;
        }

        perfil.setNome(nome);
        perfil.setApelido(apelido);

        progressDialog.setTitle("Criando perfil");
        progressDialog.setMessage("por favor, aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            RequestQueue queue = Volley.newRequestQueue(CriarPerfilActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(
                    JsonObjectRequest.Method.POST,
                    getString(R.string.app_ws_urlContato),
                    perfil.getJsonObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            perfil.populate(jsonObject);
                            if (perfil.getId() > 0) {
                                perfil.salvarPerfil(CriarPerfilActivity.this);
                                intent = new Intent(CriarPerfilActivity.this, ConversasActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Erro: o ws não retornou um id válido
                                Toast.makeText(CriarPerfilActivity.this, "Erro ao cadastrar perfil.", Toast.LENGTH_SHORT).show();
                                Log.e(getString(R.string.app_name), "ID retornado: " + perfil.getId());
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(CriarPerfilActivity.this, "Erro ao cadastrar perfil.", Toast.LENGTH_SHORT).show();
                            Log.e(getString(R.string.app_name), volleyError.getMessage());
                            progressDialog.dismiss();
                        }
                    }
            );
            queue.add(request);

        } catch (Exception ex) {
            Toast.makeText(this, "Falha ao cadastrar novo perfil. Tente novamente!", Toast.LENGTH_SHORT).show();
            Log.e(getString(R.string.app_name), ex.getMessage());
            progressDialog.dismiss();
        }
    }
}
