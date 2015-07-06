package br.edu.ifspsaocarlos.sdm.sdmmessenger.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;

public class MainActivity extends Activity {

    private Intent intent;
    private MeuPerfil perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Seta o prefixo para identificar os contatos e mensagens desse app
        Contato.WS_CONTATO_PREFIXO = getString(R.string.app_ws_contatoPrefixo);
        Mensagem.WS_MENSAGEM_PREFIXO = getString(R.string.app_ws_mensagemPrefixo);

        if (carregarPerfil()) {
            if (perfil.getId() == 0) {
                intent = new Intent(this, CriarPerfilActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(this, ConversasActivity.class);
                startActivity(intent);
            }
        }

        finish();
    }

    private boolean carregarPerfil() {
        perfil = MeuPerfil.getInstance();

        try {
            perfil.carregarPerfil(this);
            return true;
        } catch (Exception ex) {
            Toast.makeText(
                    this,
                    "Erro ao carregar seu perfil de usuário :(",
                    Toast.LENGTH_LONG
            ).show();
        }

        return false;
    }
}
