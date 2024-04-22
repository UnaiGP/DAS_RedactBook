package com.example.redact_book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Configuracion extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    // Constantes para las preferencias de modo oscuro y lenguaje
    public static final String DARK_MODE_PREF = "dark_mode_pref";
    public static final String LANGUAGE_PREF = "language_pref";

    // Botón de cambio de modo oscuro
    private Switch switchModeButton;

    private ImageView imageView;
    private String user;

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

        imageView = findViewById(R.id.profileImageView);
        // Inicialización de vistas y base de datos
        switchModeButton = findViewById(R.id.switch_dark_mode);
        switchModeButton.setChecked(isDarkModeEnabled);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        user = sharedPreferences.getString("usuario", "");

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
        // URL del servidor PHP para verificar y descargar la imagen de perfil
        String urlServidor = "http://34.72.236.55:81/verificacionImagen.php";
        String languageCode = prefs.getString(Configuracion.LANGUAGE_PREF, ""); // Obtenemos el código de idioma guardado en las SharedPreferences
        // Crear una solicitud HTTP POST para verificar la imagen
        new VerificarImagenTask().execute(urlServidor, user, languageCode);


        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
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

        // Listener para el botón de mapas
        Button mapsButton = findViewById(R.id.mapa);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Configuracion.this, Maps.class);
                startActivity(intent);
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

    // Método para mostrar el diálogo de cambio de foto
    public void showChangePhotoDialog(View view) {
        // Configurar el tema de acuerdo al modo oscuro activado/desactivado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(DARK_MODE_PREF, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (isDarkModeEnabled) {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Dark);
        } else {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Light);
        }
        builder.setTitle(R.string.cambiar_foto);
        builder.setMessage(R.string.cambiar_preguntar);
        builder.setPositiveButton(R.string.delete_database_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes mostrar otro diálogo para seleccionar una foto de la galería o tomar una nueva
                // Implementa la lógica para cambiar la foto de perfil y subirla a la base de datos
                showPhotoSelectionDialog();
            }
        });
        builder.setNegativeButton(R.string.delete_database_no, null);
        builder.show();
    }

    // Método para mostrar el diálogo de selección de foto
    private void showPhotoSelectionDialog() {
        // Configurar el tema de acuerdo al modo oscuro activado/desactivado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(DARK_MODE_PREF, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (isDarkModeEnabled) {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Dark);
        } else {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Light);
        }
        builder.setTitle(getString(R.string.seleccion_foto));

        // Obtener las cadenas de texto reales de los recursos de cadena
        String[] options = new String[] {
                getString(R.string.galery),
                getString(R.string.camara),
                getString(R.string.cancel)
        };

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Galería
                        seleccionarFoto();
                        break;
                    case 1: // Cámara
                        if (ContextCompat.checkSelfPermission(Configuracion.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(Configuracion.this, new String[]{
                                    Manifest.permission.CAMERA
                            }, 100);
                        }
                        sacarFoto();
                        break;
                    case 2: // Cancelar
                        // No hacer nada, simplemente cerrar el diálogo
                        break;
                }
            }
        });
        builder.show();
    }


    // Método para seleccionar una foto de la galería
    private void seleccionarFoto() {
        Intent elIntentFoto= new Intent(Intent.ACTION_PICK);
        elIntentFoto.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(elIntentFoto, 101);
    }

    // Método para tomar una foto con la cámara
    private void sacarFoto() {
        Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(elIntentFoto, 100);
    }

    // Método llamado después de seleccionar/tomar una foto
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Si la foto fue tomada desde la cámara
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            handleImage(bitmap, true);
        } else if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            // Si la foto fue seleccionada desde la galería
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                handleImage(bitmap, false);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.error_galery, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para manejar la imagen seleccionada/tomada
    private void handleImage(Bitmap bitmap, boolean saveToGallery) {
        // Mostrar la imagen en el ImageView
        imageView.setImageBitmap(bitmap);
        // Guardar la imagen en la galería solo si se toma desde la cámara
        if (saveToGallery) {
            guardarImagenEnGaleria(bitmap);
        }
        // Convertir la imagen a Base64 y enviarla al servidor
        convertirYEnviarImagen(bitmap);
    }

    // Método para convertir la imagen a Base64 y enviarla al servidor
    private void convertirYEnviarImagen(Bitmap bitmap) {
        // Convertir la imagen a Base64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (esFormatoPNG(bitmap)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }
        byte[] fototransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);
        // Crear una solicitud HTTP POST para enviar la imagen al servidor
        String urlServidor = "http://34.72.236.55:81/uploadImage.php";  // Reemplaza con la URL de tu servidor y script PHP
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString(Configuracion.LANGUAGE_PREF, ""); // Obtenemos el código de idioma guardado en las SharedPreferences
        new EnviarImagenTask().execute(urlServidor, user, fotoen64, languageCode);
    }

    // Método para verificar si la imagen es formato PNG
    private boolean esFormatoPNG(Bitmap bitmap) {
        // Convertir el bitmap a bytes
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Verificar la firma del archivo para determinar el formato
        // La firma de un archivo PNG comienza con los bytes: 89 50 4E 47 0D 0A 1A 0A
        if (byteArray.length >= 8 &&
                byteArray[0] == (byte) 0x89 &&
                byteArray[1] == (byte) 0x50 &&
                byteArray[2] == (byte) 0x4E &&
                byteArray[3] == (byte) 0x47 &&
                byteArray[4] == (byte) 0x0D &&
                byteArray[5] == (byte) 0x0A &&
                byteArray[6] == (byte) 0x1A &&
                byteArray[7] == (byte) 0x0A) {
            return true; // Es un archivo PNG
        } else {
            return false; // No es un archivo PNG
        }
    }

    // Método para guardar la imagen en la galería
    private void guardarImagenEnGaleria(@NonNull Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        OutputStream fos = null;
        File imageFile = null;

        try {
            // Obtener el directorio de almacenamiento público
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            // Crear un archivo en el directorio de imágenes
            imageFile = new File(storageDir, imageFileName);
            fos = new FileOutputStream(imageFile);

            // Comprimir y escribir la imagen en el archivo
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Actualizar la galería para que detecte la nueva imagen
            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFileName, null);

            // Mostrar un mensaje de confirmación
            Toast.makeText(this, R.string.galery_save, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
        } finally {
            // Cerrar el OutputStream después de terminar
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showLogoutConfirmationDialog() {

        // Configurar el tema de acuerdo al modo oscuro activado/desactivado
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(DARK_MODE_PREF, false); // Verificar si el modo oscuro está activado
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Crear un constructor de AlertDialog

        // Elegir el tema oscuro o claro para el cuadro de diálogo según la preferencia de modo oscuro
        if (isDarkModeEnabled) {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Dark); // Tema oscuro
        } else {
            builder = new android.app.AlertDialog.Builder(Configuracion.this, R.style.AlertDialog_Light); // Tema claro
        }

        builder.setTitle(R.string.cerrar_sesi_n); // Establecer el título del cuadro de diálogo como "Cerrar Sesión"
        builder.setMessage(R.string.sure_logout); // Establecer el mensaje del cuadro de diálogo como "¿Estás seguro de que quieres cerrar la sesión?"

        builder.setPositiveButton(R.string.delete_database_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones cuando se hace clic en el botón "Sí" del cuadro de diálogo
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("usuario"); // Eliminar la información del usuario de las preferencias
                editor.putBoolean("isLoggedIn", false); // Cambiar el estado de inicio de sesión a falso
                editor.apply(); // Aplicar los cambios en las preferencias
                Intent intent = new Intent(Configuracion.this, LoginActivity.class); // Crear una nueva intención para iniciar la actividad de inicio de sesión
                startActivity(intent); // Iniciar la actividad de inicio de sesión
                finish(); // Finalizar la actividad actual (cierre de sesión)
            }
        });

        builder.setNegativeButton(R.string.delete_database_no, null); // Configurar el botón "No" que cierra el cuadro de diálogo sin realizar ninguna acción

        builder.show(); // Mostrar el cuadro de diálogo construido
    }


    // Clase AsyncTask para enviar la imagen al servidor
    private class EnviarImagenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlServidor = params[0];
            String user = params[1];
            String fotoPath = params[2];
            String languageCode = params [3];
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlServidor);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("imagen", fotoPath)
                        .appendQueryParameter("usuario", user)
                        .appendQueryParameter("lang", languageCode);
                String parametrosURL = builder.build().getEncodedQuery();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametrosURL.getBytes());
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return getString(R.string.img_err);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(Configuracion.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    // Clase AsyncTask para verificar si el usuario tiene una imagen de perfil
    private class VerificarImagenTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String urlServidor = params[0];
            String usuario = params[1];
            String languageCode = params[2];
            Bitmap bitmap = null;
            try {
                // Crear la URL de la conexión
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // Configurar la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                // Crear los parámetros para enviar al servidor
                String parametros = "usuario=" + usuario + "&lang=" + languageCode;
                // Escribir los datos en el flujo de salida de la conexión
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametros.getBytes());
                outputStream.flush();
                outputStream.close();
                // Leer la respuesta del servidor
                int responseCode = connection.getResponseCode();
                Log.d("VerificarImagenTask", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                } else {
                    Log.e("VerificarImagenTask", "Error en la respuesta del servidor: " + responseCode);
                }

                // Cerrar la conexión
                connection.disconnect();
            } catch (IOException e) {
                Log.e("VerificarImagenTask", "Error al realizar la conexión: " + e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(Configuracion.this, getString(R.string.dwl_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}