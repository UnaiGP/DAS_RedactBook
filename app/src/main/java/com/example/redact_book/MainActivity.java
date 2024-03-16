package com.example.redact_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Declaración de variables miembro
    private DatabaseHelper dbHelper;
    private ArrayList<Libro> listaLibros;
    private ArrayAdapter<Libro> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener preferencias de la aplicación
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkModeEnabled = prefs.getBoolean(Configuracion.DARK_MODE_PREF, false);

        // Establecer tema de acuerdo al modo seleccionado
        if (isDarkModeEnabled) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        // Establecer el diseño de la actividad principal
        setContentView(R.layout.activity_main);

        // Inicializar el objeto DatabaseHelper y obtener la lista de libros
        dbHelper = new DatabaseHelper(this);
        listaLibros = dbHelper.getBookDetails();

        // Crear un adaptador personalizado para la lista de libros
        adaptador = new ArrayAdapter<Libro>(this, android.R.layout.simple_list_item_1, listaLibros) {
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflar el diseño personalizado para cada elemento de la lista
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent, false);
                }

                // Obtener referencias a los elementos de la vista
                TextView titleTextView = convertView.findViewById(R.id.titleTextView);
                TextView pagesTextView = convertView.findViewById(R.id.pagesTextView);
                ImageView star1 = convertView.findViewById(R.id.star1);
                ImageView star2 = convertView.findViewById(R.id.star2);
                ImageView star3 = convertView.findViewById(R.id.star3);
                ImageView star4 = convertView.findViewById(R.id.star4);
                ImageView star5 = convertView.findViewById(R.id.star5);

                // Obtener el libro actual y establecer los datos en la vista
                Libro libro = getItem(position);
                if (libro != null) {
                    Resources resources = getResources();
                    String titulo = resources.getString(R.string.titulo);
                    titleTextView.setText(titulo + " " + libro.getTitulo());
                    String paginas = resources.getString(R.string.p_ginas);
                    pagesTextView.setText(paginas + " " + libro.getPaginas());
                    int rating = libro.getValoracion();

                    // Establecer la visibilidad de las estrellas de acuerdo a la puntuación
                    if (rating >= 2) {
                        star1.setImageResource(R.drawable.star_full);
                    } else if (rating == 1) {
                        star1.setImageResource(R.drawable.semi_starred_symbolic);
                    } else {
                        star1.setImageResource(R.drawable.empty_star);
                    }
                    if (rating >= 4) {
                        star2.setImageResource(R.drawable.star_full);
                    } else if (rating == 3) {
                        star2.setImageResource(R.drawable.semi_starred_symbolic);
                    } else {
                        star2.setImageResource(R.drawable.empty_star);
                    }

                    if (rating >= 6) {
                        star3.setImageResource(R.drawable.star_full);
                    } else if (rating == 5) {
                        star3.setImageResource(R.drawable.semi_starred_symbolic);
                    } else {
                        star3.setImageResource(R.drawable.empty_star);
                    }

                    if (rating >= 8) {
                        star4.setImageResource(R.drawable.star_full);
                    } else if (rating == 7) {
                        star4.setImageResource(R.drawable.semi_starred_symbolic);
                    } else {
                        star4.setImageResource(R.drawable.empty_star);
                    }

                    if (rating >= 10) {
                        star5.setImageResource(R.drawable.star_full);
                    } else if (rating == 9) {
                        star5.setImageResource(R.drawable.semi_starred_symbolic);
                    } else {
                        star5.setImageResource(R.drawable.empty_star);
                    }
                }
                return convertView;
            }
        };

        // Configurar el adaptador en la ListView
        ListView lalista = findViewById(R.id.listViewBooks);
        lalista.setAdapter(adaptador);

        // Manejar el evento de clic en un elemento de la lista
        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el libro seleccionado y abrir la actividad DetalleLibro
                Libro libroSeleccionado = listaLibros.get(position);

                Intent intent = new Intent(view.getContext(), DetalleLibro.class);
                intent.putExtra("titulo", libroSeleccionado.getTitulo());
                intent.putExtra("paginas", libroSeleccionado.getPaginas());
                intent.putExtra("valoracion", libroSeleccionado.getValoracion());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verificar si ha cambiado el idioma o el modo oscuro
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = prefs.getString(Configuracion.LANGUAGE_PREF, "");
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();

        if (!savedLanguage.equals(currentLanguage)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Configuracion.LANGUAGE_PREF, currentLanguage);
            editor.apply();
            recreate(); // Reconstruir la actividad para aplicar los cambios en el idioma
            return;
        }

        boolean isDarkModeEnabled = prefs.getBoolean(Configuracion.DARK_MODE_PREF, false);
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        int newNightMode = isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

        if (currentNightMode != newNightMode) {
            AppCompatDelegate.setDefaultNightMode(newNightMode);
            recreate(); // Reconstruir la actividad para aplicar los cambios en el modo oscuro
            return;
        }

        // Actualizar la lista de libros y el adaptador
        listaLibros.clear();
        listaLibros.addAll(dbHelper.getBookDetails());
        adaptador.notifyDataSetChanged();

        // Actualizar el texto del botón de añadir libro
        Button btnAddBook = findViewById(R.id.btnAddBook);
        btnAddBook.setText(R.string.añadir_libro);
    }

    // Manejador de inicio de actividad para el lanzamiento de actividades con resultados
    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // Actualizar la lista de libros después de añadir un nuevo libro
            if (result.getResultCode() == RESULT_OK){
                listaLibros.clear();
                listaLibros.addAll(dbHelper.getBookDetails());
                adaptador.notifyDataSetChanged();
            }
        }
    });

    // Método llamado al hacer clic en el botón de añadir libro
    public void onAñadirLibro(View v) {
        // Lanzar la actividad NewBookActivity para añadir un nuevo libro
        Intent i = new Intent(v.getContext(), NewBookActivity.class);
        startActivityIntent.launch(i);
    }

    // Método llamado al hacer clic en el botón de configuración
    public void onAñadirConfig(View v) {
        // Lanzar la actividad Configuracion
        Intent i = new Intent(v.getContext(), Configuracion.class);
        startActivityIntent.launch(i);
    }
}
