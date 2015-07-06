package br.edu.ifspsaocarlos.sdm.sdmmessenger.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Config;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.SQLiteHelper;

/**
 * Created by mateus on 26/06/15.
 */
public class ConfigDAO implements DAO<Config, String> {

    private Context context;
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase database;

    public static final String TABLE_NAME = "configuracoes";
    public static final String TABLE_PK = "nome";
    public static final String TABLE_CREATE_QUERY =
                        "CREATE TABLE " + ConfigDAO.TABLE_NAME + " ("
                        + ConfigDAO.TABLE_PK + " VARCHAR(50) PRIMARY KEY, "
                        + "valor TEXT);";


    public ConfigDAO(Context context) {
        this.context = context;

    }

    @Override
    public void open() throws SQLException {
        sqLiteHelper = new SQLiteHelper(context);
        database = sqLiteHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        sqLiteHelper.close();
        database.close();
    }

    @Override
    public List<Config> selectAll() {
        List<Config> configs = new ArrayList<Config>();
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[] {this.TABLE_PK, "valor"},
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Config config = new Config();
                config.setNome(cursor.getString(0));
                config.setValor(cursor.getString(1));
                configs.add(config);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return configs;
    }

    @Override
    public Config findById(String configNome) {
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[]{this.TABLE_PK, "valor"},
                this.TABLE_PK + " LIKE ?",
                new String[]{configNome},
                null,
                null,
                null
        );

        int rows = cursor.getCount();
        if (rows >= 1) {
            if (rows > 1) {
                Log.w(
                        context.getString(R.string.app_name),
                        this.getClass().getName() + ".findById() retornou mais do que um registro ("+ rows
                                +" registros retornados) porém só o primeiro foi considerado.");
            }
            cursor.moveToFirst();
            Config config = new Config();
            config.setNome(cursor.getString(0));
            config.setValor(cursor.getString(1));
            cursor.close();
            return config;
        }

        cursor.close();
        return null;
    }

    @Override
    public int insert(Config obj) {
        ContentValues values = new ContentValues();
        values.put(this.TABLE_PK, obj.getNome());
        values.put("valor", obj.getValor());
        return (int) database.insert(this.TABLE_NAME, null, values);
    }

    @Override
    public boolean update(Config obj) {
        ContentValues values = new ContentValues();
        values.put(this.TABLE_PK, obj.getNome());
        values.put("valor", obj.getValor());
        return (database.update(
                this.TABLE_NAME,
                values,
                this.TABLE_PK + " LIKE ?",
                new String[]{ obj.getNome() }) >= 0);
    }

    @Override
    public boolean delete(Config obj) {
        return (database.delete(
                this.TABLE_NAME,
                this.TABLE_PK + " LIKE ?",
                new String[]{ obj.getNome() }) >= 0);

    }
}
