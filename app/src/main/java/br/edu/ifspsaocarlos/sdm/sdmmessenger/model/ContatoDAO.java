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
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Conversa;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.util.SQLiteHelper;

/**
 * Created by mateus on 25/06/15.
 */
public class ContatoDAO implements DAO<Contato, Integer> {

    private Context context;
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase database;

    public static final String TABLE_NAME = "contatos";
    public static final String TABLE_PK = "id";
    public static final String TABLE_CREATE_QUERY =
                        "CREATE TABLE " + ContatoDAO.TABLE_NAME + " ("
                        + ContatoDAO.TABLE_PK + " INTEGER PRIMARY KEY, "
                        + "nome VARCHAR(100), "
                        + "apelido VARCHAR(100));";

    public ContatoDAO(Context context) {
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
    public List<Contato> selectAll() {
        List<Contato> contatos = new ArrayList<Contato>();
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[] {this.TABLE_PK, "nome", "apelido"},
                null,
                null,
                null,
                null,
                "nome"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setApelido(cursor.getString(2));
                contatos.add(contato);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return contatos;
    }

    /**
     * Retorna as últimas mensagens dos contatos que o usuário conversou
     * @return a lista de conversas
     */
    public List<Conversa> ultimasMensagensDosContatos() {

        // Gambiarra de última hora. Desculpe Pedro :( rs
        MensagemDAO mensagemDAO = new MensagemDAO(context);
        mensagemDAO.open();

        List<Conversa> conversas = new ArrayList<Conversa>();
        String query = "SELECT c.id, c.nome, c.apelido, "
                + "(SELECT MAX(m.id) FROM mensagens m WHERE idOrigem = c.id OR idDestino = c.id) as `ultimaMsg` "
                + "FROM contatos c "
                + "WHERE `ultimaMsg` IS NOT NULL "
                + "ORDER BY `ultimaMsg` DESC";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Conversa conversa = new Conversa();
                conversa.setId(cursor.getInt(0));
                conversa.setNome(cursor.getString(1));
                conversa.setApelido(cursor.getString(2));

                int idUltimaMsg = cursor.getInt(3);
                Mensagem mensagem = mensagemDAO.findById(idUltimaMsg);

                conversa.setUltimaMensagem(mensagem);
                conversas.add(conversa);
                cursor.moveToNext();
            }
        }

        mensagemDAO.close();
        return conversas;
    }

    @Override
    public Contato findById(Integer id) {
        Cursor cursor = database.query(
                this.TABLE_NAME,
                new String[]{this.TABLE_PK, "nome", "apelido"},
                this.TABLE_PK + " = ?",
                new String[]{id.toString()},
                null,
                null,
                null
        );

        int rows = cursor.getCount();
        if (rows >= 1) {
            if (rows > 1) {
                Log.w(
                        context.getString(R.string.app_name),
                        this.getClass().getName() + ".findById() retornou mais do que um registro (" + rows
                                + " registros retornados) porém só o primeiro foi considerado.");
            }
            cursor.moveToFirst();
            Contato contato = new Contato();
            contato.setId(cursor.getInt(0));
            contato.setNome(cursor.getString(1));
            contato.setApelido(cursor.getString(2));
            cursor.close();
            return contato;
        }

        cursor.close();
        return null;
    }

    @Override
    public int insert(Contato obj) {
        ContentValues values = new ContentValues();
        values.put(this.TABLE_PK, obj.getId());
        values.put("nome", obj.getNome());
        values.put("apelido", obj.getApelido());
        return (int) database.insert(this.TABLE_NAME, null, values);
    }

    @Override
    public boolean update(Contato obj) {
        ContentValues values = new ContentValues();
        values.put("nome", obj.getNome());
        values.put("apelido", obj.getApelido());
        return (database.update(
                this.TABLE_NAME,
                values,
                this.TABLE_PK + " = ?",
                new String[]{ Integer.toString(obj.getId()) }) >= 0);
    }

    @Override
    public boolean delete(Contato obj) {
        return (database.delete(
                this.TABLE_NAME,
                this.TABLE_PK + " = ?",
                new String[]{ Integer.toString(obj.getId()) }) >= 0);
    }

}
