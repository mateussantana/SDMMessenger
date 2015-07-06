package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mateus on 26/06/15.
 */
public class Mensagem implements JsonBean {

    private int id, idOrigem, idDestino;
    private String assunto, corpo;

    private static final String JSON_ID = "id";
    private static final String JSON_ID_ORIGEM = "origem_id";
    private static final String JSON_ID_DESTINO = "destino_id";
    private static final String JSON_ASSUNTO = "assunto";
    private static final String JSON_CORPO = "corpo";

    public static String WS_MENSAGEM_PREFIXO;

    public Mensagem() {
        this.id =  0;
    }

    public Mensagem(int idOrigem, int idDestino, String assunto, String corpo) {
        this();
        this.idOrigem = idOrigem;
        this.idDestino = idDestino;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public Mensagem(int id, int idOrigem, int idDestino, String assunto, String corpo) {
        this.id = id;
        this.idOrigem = idOrigem;
        this.idDestino = idDestino;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(int idOrigem) {
        this.idOrigem = idOrigem;
    }

    public int getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    @Override
    public JSONObject getJsonObject() {
        String prefixo = this.WS_MENSAGEM_PREFIXO;
        String assunto = prefixo + getAssunto();

        try {
            JSONObject jsonObject = new JSONObject();
            if (id != 0) jsonObject.put(JSON_ID, getId());
            jsonObject.put(JSON_ID_ORIGEM, getIdOrigem())
                    .put(JSON_ID_DESTINO, getIdDestino())
                    .put(JSON_ASSUNTO, assunto)
                    .put(JSON_CORPO, getCorpo());
            return jsonObject;
        } catch (JSONException ex) {
            return null;
        }
    }

    @Override
    public void populate(JSONObject jsonObject) {
        try {
            String assunto = jsonObject.getString(JSON_ASSUNTO);
            if (assunto.contains(WS_MENSAGEM_PREFIXO))
                assunto = assunto.replaceFirst(WS_MENSAGEM_PREFIXO, "");
            setId( jsonObject.getInt(JSON_ID) );
            setIdOrigem( jsonObject.getInt(JSON_ID_ORIGEM) );
            setIdDestino(jsonObject.getInt(JSON_ID_DESTINO));
            setAssunto(assunto);
            setCorpo(jsonObject.getString(JSON_CORPO));
        } catch (Exception ex) {
            return;
        }
    }

    public static boolean isMensagemDesteApp(JSONObject jsonObject) {
        try {
            String assunto = jsonObject.getString(Mensagem.JSON_ASSUNTO);
            return assunto.contains(Mensagem.WS_MENSAGEM_PREFIXO);
        } catch (Exception ex) {
            return false;
        }
    }
}
