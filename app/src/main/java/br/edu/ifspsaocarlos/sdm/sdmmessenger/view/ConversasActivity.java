package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Conversa;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.service.BuscaMensagemService;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.ConversaArrayAdapter;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.SQLiteHelper;

public class ConversasActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private MeuPerfil meuPerfil;
    private TextView tvConversas;
    private Intent intent;
    private ConversaArrayAdapter adapter;
    ContatoDAO contatoDAO;
    private ListView lvConversas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);

        // Lança o serviço
        startService(new Intent(this, BuscaMensagemService.class));

        meuPerfil = MeuPerfil.getInstance();
        contatoDAO = new ContatoDAO(this);

        tvConversas = (TextView) findViewById(R.id.tvConversas);
        lvConversas = (ListView) findViewById(R.id.lvConversas);
        lvConversas.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildListView();
        Log.i(getString(R.string.app_name), getLocalClassName()+".onResume()");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Conversa conversa = adapter.getItem(position);
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("contato", new Contato(conversa.getId(), conversa.getNome(), conversa.getApelido()));
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_novaMensagem) {
            intent = new Intent(this, ContatosActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_contatos) {
            intent = new Intent(this, ContatosActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_meuPerfil) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Apagando perfil");
            progressDialog.setMessage("apagando contatos e mensagens, por favor aguarde...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {
                SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
                sqLiteHelper.reset();
                Toast.makeText(this, "Perfil apagado com sucesso", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Erro ao apagar o seu perfil", Toast.LENGTH_SHORT).show();
            } finally {
                progressDialog.dismiss();
            }

            // Para o serviço de verificação de msgs
            stopService(new Intent(this, BuscaMensagemService.class));
            // Cancela notificacoes
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildListView() {
        contatoDAO.open();
        List<Conversa> conversas = contatoDAO.ultimasMensagensDosContatos();

        if (adapter == null) {
            adapter = new ConversaArrayAdapter(this, conversas);
        } else {
            adapter.clear();
            adapter.addAll(conversas);
        }

        if (conversas.size() > 0) {
            tvConversas.setVisibility(TextView.INVISIBLE);
        } else {
            tvConversas.setVisibility(TextView.VISIBLE);
        }

        lvConversas.setAdapter(adapter);
        contatoDAO.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
