package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ConfigDAO;

/**
 * Created by mateus on 27/06/15.
 */
public class MeuPerfil extends Contato {

    private static MeuPerfil ourInstance = new MeuPerfil();

    public static MeuPerfil getInstance() {
        return ourInstance;
    }

    private MeuPerfil() {
        super();
    }

    public void carregarPerfil(Context context) throws SQLException {
        ConfigDAO configDAO = new ConfigDAO(context);
        Config config;
        try {
            configDAO.open();

            config = configDAO.findById("perfilId");
            if (config.getValor() == null) setId(0);
            else setId(Integer.parseInt(config.getValor()));

            config = configDAO.findById("perfilNome");
            setNome(config.getValor());

            config = configDAO.findById("perfilApelido");
            setApelido(config.getValor());

            configDAO.close();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void salvarPerfil(Context context) throws SQLException {
        ConfigDAO configDAO = new ConfigDAO(context);
        Config config;
        try {
            configDAO.open();

            config = new Config("perfilId", Integer.toString(getId()));
            configDAO.update(config);

            config = new Config("perfilNome", getNome());
            configDAO.update(config);

            config = new Config("perfilApelido", getApelido());
            configDAO.update(config);

            configDAO.close();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void apagarPerfil(Context context) throws SQLException {
        ConfigDAO configDAO = new ConfigDAO(context);
        Config config;
        try {
            configDAO.open();

            config = new Config("perfilId", null);
            configDAO.update(config);

            config = new Config("perfilNome", null);
            configDAO.update(config);

            config = new Config("perfilApelido", null);
            configDAO.update(config);

            configDAO.close();
        } catch (SQLException ex) {
            throw ex;
        }
    }
}
