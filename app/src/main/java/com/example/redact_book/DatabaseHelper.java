package com.example.redact_book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "Libros.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para crear la tabla de libros
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE Libros (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "titulo TEXT," +
                    "paginas INTEGER," +
                    "valoracion INTEGER" +
                    ")";

    // Constructor de la clase
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método para crear la tabla cuando se crea la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    // Método para actualizar la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No se necesita implementar aquí ya que es la primera versión
    }

    // Método para eliminar todos los libros de la base de datos
    public void deleteAllBooks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Libros", null, null);
        db.close();
    }

    // Método para obtener los detalles de todos los libros de la base de datos
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

    // Método para eliminar un libro por su título
    public void eliminarLibro(String titulo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Libros", "titulo = ?", new String[]{titulo});
        db.close();
    }

    // Método para actualizar la información de un libro
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

    // Método para obtener los detalles de un libro por su título
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
