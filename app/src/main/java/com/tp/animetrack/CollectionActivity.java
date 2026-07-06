package com.tp.animetrack;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CollectionAdapter adapter;
    private List<Anime> animeList = new ArrayList<>();
    private List<Anime> animeListFull = new ArrayList<>();
    private SearchView searchView;

    private FloatingActionButton fabAdd;
    private int selectedPosition = -1;

    private static final String FILE_NAME = "collection.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerViewCollection);
        searchView = findViewById(R.id.searchView);
        fabAdd = findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CollectionAdapter();
        recyclerView.setAdapter(adapter);

        // Clic sur une carte → aller au Détail
        adapter.setOnItemClickListener((position, anime) -> {
            Intent intent = new Intent(CollectionActivity.this, DetailActivity.class);
            intent.putExtra("tmdb_id", anime.getTmdbId());
            intent.putExtra("title", anime.getTitle());
            intent.putExtra("status", anime.getStatus());
            intent.putExtra("rating", anime.getRating());
            intent.putExtra("poster_url", anime.getPosterUrl());
            startActivity(intent);
        });

        // Long clic → menu contextuel
        adapter.setOnItemLongClickListener(new CollectionAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, Anime anime) {
                selectedPosition = position;
                registerForContextMenu(recyclerView);
                openContextMenu(recyclerView);
            }
        });

        // Chargement de la collection

        loadCollectionFromFile();

        // FAB → aller à la recherche
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Filtre SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }


    // Menu contextuel
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (selectedPosition >= 0 && selectedPosition < adapter.getItemCount()) {
            Anime anime = adapter.getAnimeAt(selectedPosition);
            menu.setHeaderTitle(anime.getTitle());
            menu.add(0, 1, 0, "Modifier statut");
            menu.add(0, 2, 1, "Modifier note");
            menu.add(0, 3, 2, "Supprimer");
        }
    }

    // ========== MENU ==========
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(CollectionActivity.this, SettingsActivity.class);
            startActivity(intent);
            return  true;

        } else if (id == R.id.collect) {
            Intent intent = new Intent(CollectionActivity.this, CollectionActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.search) {
            Intent intent = new Intent(CollectionActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Filtrage de la collection
    private void filter(String text) {
        List<Anime> filteredList = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            filteredList.addAll(animeListFull);
        } else {
            for (Anime anime : animeListFull) {
                if (anime.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(anime);
                }
            }
        }
        adapter.setAnimeList(filteredList);
    }

    // Chargement depuis fichier JSON interne
    private void loadCollectionFromFile() {
        animeList.clear();
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
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
                if (obj.has("personalNote")) {
                    anime.setPersonalNote(obj.getString("personalNote"));
                }
                if (obj.has("viewingDate")) {
                    anime.setViewingDate(obj.getString("viewingDate"));
                }
                animeList.add(anime);
            }
        } catch (Exception e) {
            addSampleData();
        }
        animeListFull.clear();
        animeListFull.addAll(animeList);
        adapter.setAnimeList(animeList);
    }

    // Données sample (conformes à ta maquette)
    private void addSampleData() {
        animeList.add(new Anime("Attack on Titan", "Terminé", 5, "/r6bDOGXogNlWbJ8DB1E7CvH5PnW.jpg", 1429));
        animeList.add(new Anime("Demon Slayer", "En cours", 4.5f, "/n0Xr7lE3zLJZjK5Vq8vM2dL9Q2c.jpg", 85937));
        animeList.add(new Anime("Jujutsu Kaisen", "Termine", 4, "/fHr5M6L7qW8cV9bN0mA1sD2fG3hJ.jpg", 95479));
        animeList.add(new Anime("One Piece", "Terminé", 5, "/tTd0C0n5L6m7N8o9P0q1r2s3t4u5v.jpg", 37854));
        animeList.add(new Anime("Steins;Gate", "En cours", 5, "/f8V3lL9m0n1b2v3c4x5z6a7s8d9f0g.jpg", 984));
        animeList.add(new Anime("My Hero Academia", "En cours", 4.5f, "/h1y2m3n4b5v6c7x8z9a0s1d2f3g4h.jpg", 31964));
    }

    // Sauvegarde dans fichier JSON
    private void saveCollectionToFile() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Anime anime : animeListFull) {
                JSONObject obj = new JSONObject();
                obj.put("title", anime.getTitle());
                obj.put("status", anime.getStatus());
                obj.put("rating", anime.getRating());
                obj.put("posterUrl", anime.getPosterUrl());
                obj.put("tmdbId", anime.getTmdbId());
                if (anime.getPersonalNote() != null) {
                    obj.put("personalNote", anime.getPersonalNote());
                }
                if (anime.getViewingDate() != null) {
                    obj.put("viewingDate", anime.getViewingDate());
                }
                jsonArray.put(obj);
            }
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedPosition >= 0 && selectedPosition < animeListFull.size()) {
            Anime anime = animeListFull.get(selectedPosition);
            switch (item.getItemId()) {
                case 1: // Modifier statut
                    showStatusDialog(selectedPosition, anime);
                    break;
                case 2: // Modifier note
                    showRatingDialog(selectedPosition, anime);
                    break;
                case 3:
                    animeListFull.remove(selectedPosition);
                    saveCollectionToFile();
                    loadCollectionFromFile();
                    Toast.makeText(this, anime.getTitle() + " supprimé", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        selectedPosition = -1;
        return super.onContextItemSelected(item);
    }

    private void showStatusDialog(int position, Anime anime) {
        String[] statuses = {"Planifier", "En cours", "Terminé"};
        new AlertDialog.Builder(this)
                .setTitle("Changer le statut de " + anime.getTitle())
                .setIcon(R.drawable.logo)
                .setItems(statuses, (dialog, which) -> {
                    anime.setStatus(statuses[which]);
                    animeListFull.set(position, anime);
                    saveCollectionToFile();
                    loadCollectionFromFile();
                    Toast.makeText(this, "Statut mis à jour", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showRatingDialog(int position, Anime anime) {
        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating(anime.getRating());

        new AlertDialog.Builder(this)
                .setTitle("Note pour " + anime.getTitle())
                .setIcon(R.drawable.logo)
                .setView(ratingBar)
                .setPositiveButton("OK", (dialog, which) -> {
                    anime.setRating(ratingBar.getRating());
                    animeListFull.set(position, anime);
                    saveCollectionToFile();
                    loadCollectionFromFile();
                    Toast.makeText(this, "Note mise à jour", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // Cycle de vie
    @Override
    protected void onPause() {
        super.onPause();
        saveCollectionToFile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCollectionFromFile();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (searchView != null) {
            outState.putString("search_query", searchView.getQuery().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String query = savedInstanceState.getString("search_query", "");
        if (searchView != null && !query.isEmpty()) {
            searchView.setQuery(query, false);
            filter(query);
        }
    }
}