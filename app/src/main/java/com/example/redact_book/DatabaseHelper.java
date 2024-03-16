package com.example.redact_book;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Libros.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE Libros (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "titulo TEXT," +
                    "paginas INTEGER," +
                    "valoracion INTEGER" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteAllBooks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Libros", null, null);
        db.close();
    }

    public ArrayList<Libro> getBookDetails() {
        ArrayList<Libro> listaLibros = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"titulo", "paginas", "valoracion"};
        Cursor cursor = db.query("Libros", projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
            int paginas = cursor.getInt(cursor.getColumnIndexOrThrow("paginas"));
            int valoracion = cursor.getInt(cursor.getColumnIndexOrThrow("valoracion"));
            Libro libro = new Libro(titulo, paginas, valoracion);
            listaLibros.add(libro);
        }

        cursor.close();
        db.close();

        return listaLibros;
    }

    public void eliminarLibro(String titulo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Libros", "titulo = ?", new String[]{titulo});
        db.close();
    }

    public void actualizarLibro(String tituloAntiguo, String tituloNuevo, int paginas, int valoracion) {
        SQLiteDatabase db = this.getWritableDatabase();


        String[] projection = {"id"};
        String selection = "titulo = ?";
        String[] selectionArgs = {tituloAntiguo};
        Cursor cursor = db.query("Libros", projection, selection, selectionArgs, null, null, null);

        long libroId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            libroId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            cursor.close();
        } else {
            db.close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("titulo", tituloNuevo);
        values.put("paginas", paginas);
        values.put("valoracion", valoracion);

        int rowsAffected = db.update("Libros", values, "id = ?", new String[]{String.valueOf(libroId)});
        db.close();

        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Libro actualizado exitosamente");
        } else {
            Log.e("DatabaseHelper", "No se pudo actualizar el libro");
        }
    }

    public Libro getBookDetailsByTitle(String titulo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"titulo", "paginas", "valoracion"};
        String selection = "titulo = ?";
        String[] selectionArgs = {titulo};

        Cursor cursor = db.query("Libros", projection, selection, selectionArgs, null, null, null);

        Libro libro = null;
        if (cursor.moveToNext()) {
            int paginas = cursor.getInt(cursor.getColumnIndexOrThrow("paginas"));
            int valoracion = cursor.getInt(cursor.getColumnIndexOrThrow("valoracion"));
            libro = new Libro(titulo, paginas, valoracion);
        }

        cursor.close();
        db.close();

        return libro;
    }


}
