package com.tp.animetrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private int tmdbId;
    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private double voteAverage;
    private String existingStatus = "";
    private float existingRating = 0;

    private ImageView ivPoster;
    private TextView tvTitle, tvReleaseDate, tvRating, tvOverview, tvSelectedDate;
    private Spinner spinnerStatus;
    private RatingBar ratingBarPersonal;
    private Button btnSelectDate, btnAddToCollection;

    private RequestQueue requestQueue;
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private Calendar selectedDate = null;
    private boolean isFromCollection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Récupération des données
        tmdbId = getIntent().getIntExtra("tmdb_id", 0);
        title = getIntent().getStringExtra("title");
        posterPath = getIntent().getStringExtra("poster_path");
        overview = getIntent().getStringExtra("overview");
        releaseDate = getIntent().getStringExtra("release_date");
        voteAverage = getIntent().getDoubleExtra("vote_average", 0);
        isFromCollection = getIntent().getBooleanExtra("from_collection", false);

        // Si on vient de la collection, récupérer statut et note existants
        if (isFromCollection) {
            existingStatus = getIntent().getStringExtra("status");
            existingRating = getIntent().getFloatExtra("rating", 0);
        }

        // Initialisation des vues
        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvRating = findViewById(R.id.tvRating);
        tvOverview = findViewById(R.id.tvOverview);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        ratingBarPersonal = findViewById(R.id.ratingBarPersonal);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnAddToCollection = findViewById(R.id.btnAddToCollection);

        tvTitle.setText(title);

        // Affiche les données si disponibles
        if (releaseDate != null && !releaseDate.isEmpty() && !releaseDate.equals("Date inconnue")) {
            tvReleaseDate.setText("Sortie : " + releaseDate);
        }
        if (voteAverage > 0) {
            tvRating.setText("⭐ " + voteAverage + "/10");
        }
        if (overview != null && !overview.isEmpty() && !overview.equals("Aucun synopsis")) {
            tvOverview.setText(overview);
        }

        // Charger l'affiche
        if (posterPath != null && !posterPath.isEmpty()) {
            String fullUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
            Glide.with(this).load(fullUrl).placeholder(android.R.drawable.ic_menu_gallery).into(ivPoster);
        }

        // Spinner Statut
        String[] statuses = {"Planifier","En cours", "Terminé" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Si on vient de la collection, pré-remplir
        if (isFromCollection && !existingStatus.isEmpty()) {
            int position = 0;
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(existingStatus)) {
                    position = i;
                    break;
                }
            }
            spinnerStatus.setSelection(position);
            ratingBarPersonal.setRating(existingRating);
        }

        // DatePicker
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Volley
        requestQueue = Volley.newRequestQueue(this);

        // Si on vient de la recherche, charger les détails depuis l'API
        if (!isFromCollection && tmdbId != 0) {
            fetchMovieDetails();
        } else {
            // On a déjà toutes les infos
            if (tvOverview.getText().toString().isEmpty()) {
                tvOverview.setText("Aucun synopsis disponible");
            }
        }

        // Bouton Ajouter
        btnAddToCollection.setOnClickListener(v -> addToCollection());
    }

    private void fetchMovieDetails() {
        String url = "https://api.themoviedb.org/3/movie/" + tmdbId + "?api_key=" + API_KEY + "&language=fr-FR";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String date = response.optString("release_date", "Date inconnue");
                        if (tvReleaseDate.getText().toString().isEmpty()) {
                            tvReleaseDate.setText("📅 Sortie : " + date);
                        }
                        double rating = response.optDouble("vote_average", 0);
                        if (rating > 0) {
                            tvRating.setText("⭐ " + rating + "/10");
                        }
                        String synopsis = response.optString("overview", "Aucun synopsis disponible");
                        if (tvOverview.getText().toString().isEmpty()) {
                            tvOverview.setText(synopsis);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erreur chargement des détails", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(request);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                    tvSelectedDate.setText(sdf.format(selectedDate.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addToCollection() {
        String status = spinnerStatus.getSelectedItem().toString();
        float personalRating = ratingBarPersonal.getRating();
        String viewingDate = tvSelectedDate.getText().toString();
        if (viewingDate.equals("Aucune date sélectionnée")) {
            viewingDate = "";
        }

        Anime anime = new Anime(title, status, personalRating, posterPath, tmdbId);
        anime.setPersonalNote(viewingDate);

        List<Anime> collection = loadCollectionFromFile();

        boolean exists = false;
        for (Anime a : collection) {
            if (a.getTmdbId() == tmdbId) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            collection.add(anime);
            saveCollectionToFile(collection);
            Toast.makeText(this, title + " ajouté à votre collection", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,  title + " est déjà dans votre collection", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private List<Anime> loadCollectionFromFile() {
        List<Anime> list = new ArrayList<>();
        try {
            java.io.FileInputStream fis = openFileInput("collection.json");
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            String json = new String(data);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Anime anime = new Anime(
                        obj.getString("title"),
                        obj.getString("status"),
                        (float) obj.getDouble("rating"),
                        obj.getString("posterUrl"),
                        obj.getInt("tmdbId")
                );
                list.add(anime);
            }
        } catch (Exception e) {
            // Fichier inexistant
        }
        return list;
    }

    private void saveCollectionToFile(List<Anime> collection) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Anime anime : collection) {
                JSONObject obj = new JSONObject();
                obj.put("title", anime.getTitle());
                obj.put("status", anime.getStatus());
                obj.put("rating", anime.getRating());
                obj.put("posterUrl", anime.getPosterUrl());
                obj.put("tmdbId", anime.getTmdbId());
                jsonArray.put(obj);
            }
            java.io.FileOutputStream fos = openFileOutput("collection.json", MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}