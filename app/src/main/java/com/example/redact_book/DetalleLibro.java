package com.example.redact_book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleLibro extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView textViewTitulo;
    private TextView textViewPaginas;
    private TextView textViewValoracion;
    private EditText editTextTitulo;
    private EditText editTextPaginas;
    private EditText editTextValoracion;
    private Button btnBorrar;
    private Button btnEditar;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(Configuracion.DARK_MODE_PREF, false);

        if (isDarkModeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        setContentView(R.layout.detalle_libro);
        dbHelper = new DatabaseHelper(this);

        textViewTitulo = findViewById(R.id.textTitulo);
        textViewPaginas = findViewById(R.id.textPaginas);
        textViewValoracion = findViewById(R.id.textValoracion);
        editTextTitulo = findViewById(R.id.editTitulo);
        editTextPaginas = findViewById(R.id.editPaginas);
        editTextValoracion = findViewById(R.id.editValoracion);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnEditar = findViewById(R.id.btnEditar);
        btnGuardar = findViewById(R.id.btnGuardar);
        Button botonVolver = findViewById(R.id.botonVolver);

        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        int paginas = intent.getIntExtra("paginas", 0);
        int valoracion = intent.getIntExtra("valoracion", 0);

        Resources resources = getResources();
        String tit = resources.getString(R.string.titulo);
        String pag = resources.getString(R.string.p_ginas);
        String val = resources.getString(R.string.valoraci_n);

        textViewTitulo.setText(tit + " " + titulo);
        textViewPaginas.setText(pag + " " + paginas);
        textViewValoracion.setText(val + " " + valoracion);

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                AlertDialog.Builder builder;
                if (isDarkModeEnabled) {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Dark);
                } else {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Light);
                }

                builder.setTitle(getResources().getString(R.string.borrar_libro));
                builder.setMessage(getResources().getString(R.string.borrar_libro_message));
                builder.setPositiveButton(getResources().getString(R.string.delete_database_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tit = resources.getString(R.string.titulo);
                        dbHelper.eliminarLibro(removePrefix(textViewTitulo.getText().toString(), tit + " "));
                        Intent intent = new Intent(DetalleLibro.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (isDarkModeEnabled) {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Dark);
                } else {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Light);
                }

                builder.setTitle(getResources().getString(R.string.confirm_edit_title));
                builder.setMessage(getResources().getString(R.string.confirm_edit_message));
                builder.setPositiveButton(getResources().getString(R.string.confirm_edit_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textViewTitulo.setVisibility(View.GONE);
                        textViewPaginas.setVisibility(View.GONE);
                        textViewValoracion.setVisibility(View.GONE);
                        editTextTitulo.setVisibility(View.VISIBLE);
                        editTextPaginas.setVisibility(View.VISIBLE);
                        editTextValoracion.setVisibility(View.VISIBLE);

                        editTextTitulo.setEnabled(true);
                        editTextPaginas.setEnabled(true);
                        editTextValoracion.setEnabled(true);

                        btnEditar.setVisibility(View.GONE);
                        btnBorrar.setVisibility(View.GONE);
                        btnGuardar.setVisibility(View.VISIBLE);

                        Resources resources = getResources();
                        String tit = resources.getString(R.string.titulo);
                        String pag = resources.getString(R.string.p_ginas);
                        String val = resources.getString(R.string.valoraci_n);
                        editTextTitulo.setText(removePrefix(textViewTitulo.getText().toString(), tit + " "));
                        editTextPaginas.setText(removePrefix(textViewPaginas.getText().toString(), pag + " "));
                        editTextValoracion.setText(removePrefix(textViewValoracion.getText().toString(), val + " "));
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (isDarkModeEnabled) {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Dark);
                } else {
                    builder = new android.app.AlertDialog.Builder(DetalleLibro.this, R.style.AlertDialog_Light);
                }
                builder.setTitle(getResources().getString(R.string.confirm_save_changes));
                builder.setMessage(getResources().getString(R.string.confirm_save_message));
                builder.setPositiveButton(getResources().getString(R.string.save_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Resources resources = getResources();
                        String tit = resources.getString(R.string.titulo);
                        String pag = resources.getString(R.string.p_ginas);
                        String val = resources.getString(R.string.valoraci_n);
                        String tituloAntiguo = removePrefix(textViewTitulo.getText().toString(), tit + " ");
                        String nuevoTitulo = removePrefix(editTextTitulo.getText().toString(), tit + " ");
                        String paginas = removePrefix(editTextPaginas.getText().toString(), pag + " ");
                        if (paginas.isEmpty()) {
                            Toast.makeText(DetalleLibro.this, getResources().getString(R.string.completar), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int nuevasPaginas = Integer.parseInt(removePrefix(editTextPaginas.getText().toString(), pag + " "));
                        int nuevaValoracion = Integer.parseInt(removePrefix(editTextValoracion.getText().toString(), val + " "));

                        if (nuevaValoracion < 0 || nuevaValoracion > 10 || nuevoTitulo.isEmpty()) {
                            Toast.makeText(DetalleLibro.this, getResources().getString(R.string.completar), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dbHelper.actualizarLibro(tituloAntiguo, nuevoTitulo, nuevasPaginas, nuevaValoracion);

                        textViewTitulo.setText(tit + " " + nuevoTitulo);
                        textViewPaginas.setText(pag + " " + nuevasPaginas);
                        textViewValoracion.setText(val + " " + nuevaValoracion);

                        textViewTitulo.setVisibility(View.VISIBLE);
                        textViewPaginas.setVisibility(View.VISIBLE);
                        textViewValoracion.setVisibility(View.VISIBLE);
                        editTextTitulo.setVisibility(View.GONE);
                        editTextPaginas.setVisibility(View.GONE);
                        editTextValoracion.setVisibility(View.GONE);
                        btnGuardar.setVisibility(View.GONE);
                        btnEditar.setVisibility(View.VISIBLE);
                        btnBorrar.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarValoresDesdeBaseDeDatos();
    }

    private void actualizarValoresDesdeBaseDeDatos() {
        String titulo = textViewTitulo.getText().toString();
        Libro libro = dbHelper.getBookDetailsByTitle(titulo);

        if (libro != null) {
            Resources resources = getResources();
            String tit = resources.getString(R.string.titulo);
            String pag = resources.getString(R.string.p_ginas);
            String val = resources.getString(R.string.valoraci_n);
            textViewTitulo.setText(tit + " " + libro.getTitulo());
            textViewPaginas.setText(pag + " " + libro.getPaginas());
            textViewValoracion.setText(val + " " + libro.getValoracion());
        }
    }


    private String removePrefix(String original, String prefix) {
        if (original.startsWith(prefix)) {
            return original.substring(prefix.length());
        }
        return original;
    }
}
