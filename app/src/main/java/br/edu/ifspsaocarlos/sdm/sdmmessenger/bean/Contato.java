package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mateus on 25/06/15.
 */
public class Contato implements JsonBean, Serializable {

    private int id;
    private String nome;
    private String apelido;

    private static final String JSON_ID = "id";
    private static final String JSON_NOME = "nome_completo";
    private static final String JSON_APELIDO = "apelido";

    public static String WS_CONTATO_PREFIXO;


    public Contato() {
        this.id = 0;
    }

    public Contato(String nome, String apelido) {
        this();
        this.nome = nome;
        this.apelido = apelido;
    }

    public Contato(int id, String nome, String apelido) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    @Override
    public JSONObject getJsonObject() {
        String prefixo = this.WS_CONTATO_PREFIXO;
        String nome = prefixo + getNome();
        String apelido = prefixo + getApelido();

        try {
            JSONObject jsonObject = new JSONObject();
            if (id != 0) jsonObject.put(this.JSON_ID, getId());
            jsonObject.put(this.JSON_NOME, nome);
            jsonObject.put(this.JSON_APELIDO, apelido);
            return jsonObject;
        } catch (JSONException ex) {
            return null;
        }
    }

    @Override
    public void populate(JSONObject jsonObject) {
        try {
            String nome = jsonObject.getString(this.JSON_NOME);
            String apelido = jsonObject.getString(this.JSON_APELIDO);

            if (nome.contains(this.WS_CONTATO_PREFIXO)) {
                nome = nome.replaceFirst(this.WS_CONTATO_PREFIXO, "");
            }

            if (apelido.contains(this.WS_CONTATO_PREFIXO)) {
                apelido = apelido.replaceFirst(this.WS_CONTATO_PREFIXO, "");
            }

            setId( jsonObject.getInt(this.JSON_ID) );
            setNome(nome);
            setApelido(apelido);
        } catch (Exception ex) {
            return;
        }
    }

    public static boolean isContatoDesteApp(JSONObject jsonObject) {
        try {
            String nome = jsonObject.getString(Contato.JSON_NOME);
            String apelido = jsonObject.getString(Contato.JSON_APELIDO);
            return (nome.contains(Contato.WS_CONTATO_PREFIXO) && apelido.contains(Contato.WS_CONTATO_PREFIXO));
        } catch (Exception ex) {
            return false;
        }
    }
}
