package com.example.redact_book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Configuracion extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    // Constantes para las preferencias de modo oscuro y lenguaje
    public static final String DARK_MODE_PREF = "dark_mode_pref";
    public static final String LANGUAGE_PREF = "language_pref";

    // Botón de cambio de modo oscuro
    private Switch switchModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar el tema de acuerdo al modo oscuro activado/desactivado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(DARK_MODE_PREF, false);
        if (isDarkModeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        setContentView(R.layout.menu_main);

        // Inicialización de vistas y base de datos
        switchModeButton = findViewById(R.id.switch_dark_mode);
        switchModeButton.setChecked(isDarkModeEnabled);
        dbHelper = new DatabaseHelper(this);

        // Configuración de los listeners para los botones
        switchModeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el estado del modo oscuro en las preferencias
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(DARK_MODE_PREF, isChecked);
                editor.apply();
                // Recrear la actividad para aplicar los cambios
                recreate();
                // Mostrar un mensaje de confirmación
                Toast.makeText(Configuracion.this, isChecked ? "Modo oscuro activado" : "Modo claro activado", Toast.LENGTH_SHORT).show();
            }
        });

        // Listeners para cambiar el idioma
        Button btnSpanish = findViewById(R.id.btn_spanish);
        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("es");
                Toast.makeText(Configuracion.this, "Idioma cambiado a Español", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnEnglish = findViewById(R.id.btn_english);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("en");
                Toast.makeText(Configuracion.this, "Language changed to English", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnEuskera = findViewById(R.id.btn_euskera);
        btnEuskera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLanguage("eu");
                Toast.makeText(Configuracion.this, "Hizkuntza Euskerara aldatua", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el botón de eliminar datos
        Button deleteDataButton = findViewById(R.id.btn_delete_data);
        deleteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(isDarkModeEnabled);
            }
        });

        // Listener para el botón de volver
        Button backButton = findViewById(R.id.botonVolver);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Método para cambiar el idioma de la aplicación
    private void setLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Guardar el idioma seleccionado en las preferencias
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_PREF, languageCode);
        editor.apply();

        // Recrear la actividad para aplicar los cambios de idioma
        recreate();
    }

    // Método para mostrar el diálogo de confirmación para eliminar datos
    private void showDeleteConfirmationDialog(boolean isDarkModeEnabled) {
        AlertDialog.Builder builder;
        if (isDarkModeEnabled) {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Dark);
        } else {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Light);
        }
        Resources resources = getResources();

        // Obtener cadenas de recursos para el título, mensaje y botones del diálogo
        String title = resources.getString(R.string.delete_database_title);
        String message = resources.getString(R.string.delete_database_message);
        String positiveButtonText = resources.getString(R.string.delete_database_yes);
        String negativeButtonText = resources.getString(R.string.delete_database_no);

        // Configurar el diálogo
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Eliminar todos los libros de la base de datos
                dbHelper.deleteAllBooks();
                // Mostrar un mensaje de confirmación
                Toast.makeText(Configuracion.this, resources.getString(R.string.database_deleted), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(negativeButtonText, null);
        builder.show();
    }
}
