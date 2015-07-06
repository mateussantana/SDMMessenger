package br.edu.ifspsaocarlos.sdm.sdmmessenger.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Config;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ConfigDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.ContatoDAO;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.model.MensagemDAO;

/**
 * Created by mateus on 25/06/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME    = "sdmessenger.db";
    public static final int    DATABASE_VERSION = 1;
    private Context context;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) throws SQLiteHelperException {
        // Inserts iniciais
        String insertConfig = "INSERT INTO " + ConfigDAO.TABLE_NAME + "(nome, valor) VALUES "
                + "('perfilId', null), "
                + "('perfilNome', null), "
                + "('perfilApelido', null); ";

        try {
            //database.beginTransaction();
            database.execSQL(ContatoDAO.TABLE_CREATE_QUERY);
            database.execSQL(MensagemDAO.TABLE_CREATE_QUERY);
            database.execSQL(ConfigDAO.TABLE_CREATE_QUERY);
            database.execSQL(insertConfig);
        } catch (SQLException e) {
            Log.e(context.getString(R.string.app_name), e.getMessage());
            throw new SQLiteHelperException("Erro ao criar o banco de dados", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) throws SQLiteHelperException {
        try {
            database.execSQL("DROP TABLE IF EXISTS " + ContatoDAO.TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + MensagemDAO.TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + ConfigDAO.TABLE_NAME);
        } catch (SQLException e) {
            Log.e(context.getString(R.string.app_name), e.getMessage());
            throw new SQLiteHelperException("Erro ao atualizar o banco de dados", e);
        }
        onCreate(database);
    }

    public void reset() {
        try {
            SQLiteDatabase database = getWritableDatabase();
            onUpgrade(database, 0, this.DATABASE_VERSION);
            database.close();
            close();
        } catch (Exception ex) {
            Log.e(context.getString(R.string.app_name), ex.getMessage());
            throw new SQLiteHelperException("Erro ao resetar o banco de dados", ex);
        }
    }

}
