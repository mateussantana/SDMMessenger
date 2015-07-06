package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

import org.json.JSONObject;

/**
 * Created by mateus on 28/06/15.
 */
public interface JsonBean {

    /**
     * Gera um objeto JSON com os dados do bean
     * @return
     */
    JSONObject getJsonObject();

    /**
     * Popula o bean com os dados constantes no objeto json
     * @param jsonObject
     */
    void populate(JSONObject jsonObject);

}
