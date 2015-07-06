package br.edu.ifspsaocarlos.sdm.sdmmessenger.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.SQLiteHelper;

/**
 * Created by mateus on 26/06/15.
 */
public class MensagemDAO implements DAO<Mensagem, Integer> {

    private Context context;
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase database;

    public static final String TABLE_NAME = "mensagens";
    public static final String TABLE_PK = "id";
    public static final String TABLE_CREATE_QUERY =
            "CREATE TABLE " + MensagemDAO.TABLE_NAME + " ("
            + MensagemDAO.TABLE_PK + " INTEGER PRIMARY KEY, "
            + "idOrigem INTEGER, "
            + "idDestino INTEGER, "
            + "assunto VARCHAR(50), "
            + "corpo VARCHAR(150));";

    public MensagemDAO(Context context) {
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
    public List<Mensagem> selectAll() {
        List<Mensagem> mensagens = new ArrayList<Mensagem>();
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[] {this.TABLE_PK, "idOrigem", "idDestino", "assunto", "corpo"},
                null,
                null,
                null,
                null,
                this.TABLE_PK
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Mensagem mensagem = new Mensagem();
                mensagem.setId(cursor.getInt(0));
                mensagem.setIdOrigem(cursor.getInt(1));
                mensagem.setIdDestino(cursor.getInt(2));
                mensagem.setAssunto(cursor.getString(3));
                mensagem.setCorpo(cursor.getString(4));
                mensagens.add(mensagem);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return mensagens;
    }

    /**
     * Busca todas as mensagens "de" ou "para" um determinado contato
     * @param idContato id do contato
     * @return
     */
    public List<Mensagem> selectMensagens(int idContato) {
        List<Mensagem> mensagens = new ArrayList<Mensagem>();
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[] {this.TABLE_PK, "idOrigem", "idDestino", "assunto", "corpo"},
                "idOrigem = ? OR idDestino = ?",
                new String[] {Integer.toString(idContato), Integer.toString(idContato)},
                null,
                null,
                this.TABLE_PK
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Mensagem mensagem = new Mensagem();
                mensagem.setId(cursor.getInt(0));
                mensagem.setIdOrigem(cursor.getInt(1));
                mensagem.setIdDestino(cursor.getInt(2));
                mensagem.setAssunto(cursor.getString(3));
                mensagem.setCorpo(cursor.getString(4));
                mensagens.add(mensagem);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return mensagens;
    }

    /**
     * Busca todas as mensagens "de" ou "para" um determinado contato
     * @param contato
     * @return
     */
    public List<Mensagem> selectMensagens(Contato contato) {
        return selectMensagens(contato.getId());
    }

    /**
     * Retorna o id da última mensagem "de" (que o contato enviou) um determinado contato
     * @param idContato
     * @return
     */
    public int idUltimaMensagem(int idContato) {
        int ultimoId = 0;

        Cursor cursor = database.rawQuery(
                "SELECT MAX("+ TABLE_PK +") FROM "+ TABLE_NAME +" WHERE idOrigem = ?",
                new String[]{ Integer.toString(idContato)}
        );

        if (cursor != null) {
            cursor.moveToFirst();
            ultimoId = cursor.getInt(0);
            cursor.close();
        }

        return ultimoId;
    }

    @Override
    public Mensagem findById(Integer id) {
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[]{this.TABLE_PK, "idOrigem", "idDestino", "assunto", "corpo"},
                this.TABLE_PK + " = ?",
                new String[]{id.toString()},
                null,
                null,
                null
        );

        int rows = cursor.getCount();
        cursor.moveToFirst();
        if (rows > 0) {
            cursor.moveToFirst();
            Mensagem mensagem = new Mensagem();
            mensagem.setId(cursor.getInt(0));
            mensagem.setIdOrigem(cursor.getInt(1));
            mensagem.setIdDestino(cursor.getInt(2));
            mensagem.setAssunto(cursor.getString(3));
            mensagem.setCorpo(cursor.getString(4));
            cursor.close();
            return mensagem;
        }

        cursor.close();
        return null;
    }

    @Override
    public int insert(Mensagem obj) {
        ContentValues values = new ContentValues();
        values.put(this.TABLE_PK, obj.getId());
        values.put("idOrigem", obj.getIdOrigem());
        values.put("idDestino", obj.getIdDestino());
        values.put("assunto", obj.getAssunto());
        values.put("corpo", obj.getCorpo());
        return (int) database.insert(this.TABLE_NAME, null, values);
    }

    @Override
    public boolean update(Mensagem obj) {
        return false;
    }

    @Override
    public boolean delete(Mensagem obj) {
        return (database.delete(
                this.TABLE_NAME,
                this.TABLE_PK + " = ?",
                new String[]{ Integer.toString(obj.getId()) }) >= 0);
    }
}
