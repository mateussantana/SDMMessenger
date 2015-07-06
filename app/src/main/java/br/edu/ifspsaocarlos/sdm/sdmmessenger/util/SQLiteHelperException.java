package br.edu.ifspsaocarlos.sdm.sdmmessenger.util;

import android.database.sqlite.SQLiteException;

/**
 * Created by mateus on 26/06/15.
 */
public class SQLiteHelperException extends SQLiteException {

    public SQLiteHelperException() {
    }

    public SQLiteHelperException(String error) {
        super(error);
    }

    public SQLiteHelperException(String error, Throwable cause) {
        super(error, cause);
    }

}
