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
    private DatabaseHelper dbHelper;
    private EditText editTextTitle;
    private EditText editTextPages;
    private SeekBar seekBarRating;
    private TextView textViewRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(Configuracion.DARK_MODE_PREF, false);

        if (isDarkModeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
        setContentView(R.layout.activity_new_task);
        dbHelper = new DatabaseHelper(this);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextPages = findViewById(R.id.editTextPages);
        seekBarRating = findViewById(R.id.seekBarRating);
        textViewRating = findViewById(R.id.textViewRating);
        Button buttonSave = findViewById(R.id.buttonSave);

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String pagesString = editTextPages.getText().toString();

                if (!title.isEmpty() && !pagesString.isEmpty()) {
                    int pages = Integer.parseInt(pagesString);
                    int rating = seekBarRating.getProgress();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("titulo", title);
                    values.put("paginas", pages);
                    values.put("valoracion", rating);
                    db.insert("Libros", null, values);
                    db.close();
                    createNotificationChannel();
                    finish();
                } else {
                    Resources resources = getResources();
                    Toast.makeText(NewBookActivity.this, resources.getString(R.string.completar), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions(new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            int preferencia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel canal = new NotificationChannel("1", "mi_canal", preferencia);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
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
