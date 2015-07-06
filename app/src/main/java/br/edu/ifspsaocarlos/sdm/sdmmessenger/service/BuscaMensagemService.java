package br.edu.ifspsaocarlos.sdm.sdmmessenger.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.MensagemDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.view.ChatActivity;

/**
 * Created by mateus on 02/07/15.
 */
public class BuscaMensagemService extends Service implements Runnable {

    /**
     * A ideia aqui é a cada X verificações de novas mensagens, verificar também se um novo contato
     * foi cadastrado no app. Resolvendo parte do problema de que, se algum novo contato se cadastrar
     * e te enviar uma mensagem, você possa receber a notificação.
     */
    private int contExecucoes = 0;
    private final int limiteParaAtualizarContatos = 3;

    private static int ERROR_COUNT = 0;
    private static int EXECUTIONS_COUNT = 0;

    private boolean appAberto;
    private MeuPerfil meuPerfil;

    /**
     * Um mapa para lcoalizar de forma rápiida os Contatos com o último id da mensagem verificada
     * Map< idContato, idUltimaMsg>
     */
    private Map<Integer, Integer> hmMensagens;
    private List<Contato> contatos;

    private ContatoDAO contatoDao;
    private MensagemDAO mensagemDAO;
    private RequestQueue requestQueue;


    @Override
    public void onCreate() {
        super.onCreate();

        appAberto = true;
        contatoDao = new ContatoDAO(this);
        mensagemDAO = new MensagemDAO(this);
        requestQueue = Volley.newRequestQueue(this);
        meuPerfil = MeuPerfil.getInstance();
        hmMensagens = new HashMap<>();
        atualizarListaContatos();

        Log.i(getString(R.string.app_name), "+++++++++++ Serviço de Buscar Mensagens lançado...");
        new Thread(this).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Não implementado");
    }

    @Override
    public void run() {
        while (appAberto) {
            // Verifica se precisa atualizar a lista de contatos
            if (++contExecucoes > limiteParaAtualizarContatos) {
                contExecucoes = 0;
                atualizarListaContatos();
            }

            try {
                if (EXECUTIONS_COUNT > 0)
                    Log.i(getString(R.string.app_name), "Terminou a execução do serviço de mensagem (" + EXECUTIONS_COUNT + ")");
                Thread.sleep(getResources().getInteger(R.integer.app_serviceBuscaMensagem_sleep));
                if (++EXECUTIONS_COUNT >= Integer.MAX_VALUE) EXECUTIONS_COUNT = 1; // vai que.... hahahahaha (pouco provável)
                Log.i(getString(R.string.app_name), "Executando o serviço de mensagens pela " + EXECUTIONS_COUNT + "a. vez.");
                // Verifica mensagem para cada contato da lista
                for (Contato contato : contatos)
                    verificarMensagem(contato.getId());
            } catch (InterruptedException ex) {
                Log.e(getString(R.string.app_name), "Erro ao executar thread do serviço de verificação de mensagens");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getString(R.string.app_name), "++++++++++++++++ Parando o serviço de busca de mensagens...");
        appAberto = false;
        stopSelf();
    }

    private void atualizarListaContatos() {
        // Atualiza a lista de contatos
        try {
            contatoDao.open();
            contatos = contatoDao.selectAll();
            contatoDao.close();
        } catch (Exception ex) {
            Toast.makeText(
                    this,
                    "Erro ao recuperar contatos no serviço de recuperação de mensagens. Parando...",
                    Toast.LENGTH_LONG
            ).show();
            Log.e(getString(R.string.app_name), ex.getMessage());
            contatoDao.close();
            stopSelf();
        }

        if (contatos.isEmpty()) return;

        // Atualiza a HashMap
        try {
            mensagemDAO.open();
            for (Contato contato : contatos) {
                if (!hmMensagens.containsKey(contato.getId()))
                    hmMensagens.put(contato.getId(), mensagemDAO.idUltimaMensagem(contato.getId()));
            }
            mensagemDAO.close();
        } catch (Exception ex) {
            Log.e(getString(R.string.app_name), ex.getMessage());
            mensagemDAO.close();
        }
    }

    private void verificarMensagem(final int idRemetente) {
        int idUltimaMsg = hmMensagens.get(idRemetente) + 1;
        int idDestinatario = meuPerfil.getId();
        String url = getString(R.string.app_ws_urlMensagem) + "/" + idUltimaMsg + "/" + idRemetente + "/" + idDestinatario;

        try {
            JsonObjectRequest request = new JsonObjectRequest(
                    JsonObjectRequest.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            int totalMensagens = 0;
                            int msgsRepetidas = 0;
                            Mensagem mensagem = new Mensagem();
                            Contato remetente = new Contato();

                            try {
                                mensagemDAO.open();
                                JSONArray arrayJsonMensagens = jsonObject.getJSONArray("mensagens");
                                JSONObject jsonMensagem = null;
                                totalMensagens = arrayJsonMensagens.length();
                                if (totalMensagens < 1) return;
                                for (int i = 0; i < totalMensagens; i++) {
                                    jsonMensagem = arrayJsonMensagens.getJSONObject(i);
                                    if (!Mensagem.isMensagemDesteApp(jsonMensagem)) continue;
                                    mensagem.populate(jsonMensagem);
                                    // inserir no BD as novas mensagens
                                    if (mensagem.getId() > hmMensagens.get(mensagem.getIdOrigem()))
                                        mensagemDAO.insert(mensagem);
                                    else
                                        msgsRepetidas++;
                                }
                                // atualizar hashmap
                                hmMensagens.put(mensagem.getIdOrigem(), mensagem.getId());
                                totalMensagens -= msgsRepetidas;
                                // lança notificação
                                remetente.populate(jsonMensagem.getJSONObject("origem"));
                                if (totalMensagens > 0) lancarNotificacao(remetente, mensagem);
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
                            // Exibe apenas na primeira ocorrência (para não ficar pipocando Toast infinitamente)
                            if (ERROR_COUNT == 0)
                                Toast.makeText(BuscaMensagemService.this, "Falha ao verificar mensagens...", Toast.LENGTH_LONG).show();
                            Log.e(getString(R.string.app_name), "Falha ao verificar mensagens ("+ ++ERROR_COUNT +"): " + volleyError.getMessage());
                        }
                    }
            );
            requestQueue.add(request);
        } catch (Exception ex) {
            // Exibe apenas na primeira ocorrência (para não ficar pipocando Toast infinitamente)
            if (ERROR_COUNT == 0)
                Toast.makeText(this, "Falha ao verificar mensagens...", Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.app_name), "Falha ao verificar mensagens ("+ ++ERROR_COUNT +"): " + ex.getMessage());
        }
    }

    private void lancarNotificacao(Contato remetente, Mensagem mensagem) {
        int idNotificacao = R.mipmap.sdm_launcher - remetente.getId();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("contato", remetente);
        intent.putExtra("notificacao", idNotificacao);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder
                .setSmallIcon(R.drawable.sdm_small_icon32)
                .setTicker("Nova mensagem de " + remetente.getNome())
                .setContentTitle(remetente.getNome())
                .setContentText(mensagem.getCorpo())
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.sdm_launcher));

        Notification notification = builder.build();
        notification.vibrate = new long[]{100, 250};
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(idNotificacao, notification);

        Log.i(getString(R.string.app_name), "Nova notificação ["+ idNotificacao +"] do contato ("+ remetente.getId() +") lançada...");
    }
}
