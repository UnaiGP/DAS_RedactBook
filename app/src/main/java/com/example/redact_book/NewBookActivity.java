package com.example.redact_book;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NewBookActivity extends AppCompatActivity {
    // Declaración de variables miembro
    private DatabaseHelper dbHelper;
    private EditText editTextTitle;
    private EditText editTextPages;
    private SeekBar seekBarRating;
    private TextView textViewRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener preferencias de la aplicación para el modo oscuro
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(Configuracion.DARK_MODE_PREF, false);

        // Establecer el tema de la actividad de acuerdo al modo seleccionado
        if (isDarkModeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        // Establecer el diseño de la actividad
        setContentView(R.layout.activity_new_task);

        // Inicializar el objeto DatabaseHelper y obtener referencias a los elementos de la vista
        dbHelper = new DatabaseHelper(this);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextPages = findViewById(R.id.editTextPages);
        seekBarRating = findViewById(R.id.seekBarRating);
        textViewRating = findViewById(R.id.textViewRating);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button botonVolver = findViewById(R.id.botonVolver);

        // Configurar el cambio de valor en el SeekBar para actualizar el texto de la valoración
        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewRating.setText(getResources().getString(R.string.valoraci_n) + " " + seekBarRating.getProgress());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Configurar el botón de volver para cerrar la actividad actual
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Manejar el clic en el botón de guardar
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String pagesString = editTextPages.getText().toString();

                // Verificar si se proporcionaron datos de título y páginas
                if (!title.isEmpty() && !pagesString.isEmpty()) {
                    int pages = Integer.parseInt(pagesString);
                    int rating = seekBarRating.getProgress();

                    // Abrir la base de datos y agregar el nuevo libro
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("titulo", title);
                    values.put("paginas", pages);
                    values.put("valoracion", rating);
                    db.insert("Libros", null, values);
                    db.close();

                    // Crear un canal de notificación y mostrar la notificación
                    createNotificationChannel();
                    finish(); // Finalizar la actividad después de guardar
                } else {
                    Resources resources = getResources();
                    // Mostrar un mensaje de advertencia si los campos están vacíos
                    Toast.makeText(NewBookActivity.this, resources.getString(R.string.completar), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para crear un canal de notificación
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Solicitar permiso para mostrar notificaciones si es necesario
            requestPermissions(new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, 1);

            // Configurar el nivel de importancia de la notificación
            int preferencia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel canal = new NotificationChannel("1", "mi_canal", preferencia);

            // Obtener el servicio NotificationManager y crear el canal
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);

            // Crear una notificación y mostrarla
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.ic_notificacion)
                    .setContentTitle("Libro añadido")
                    .setContentText("Libro añadido correctamente.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificacionman = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
            notificacionman.notify(Integer.parseInt("1"), builder.build());
        }
    }
}
