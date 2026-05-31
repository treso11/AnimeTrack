package com.tp.animetrack;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;
import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.*;
import com.bumptech.glide.Glide;
import org.json.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultAdapter adapter;
    private List<AnimeSearchResult> results = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView tvNoResults, tvNoConnection;
    private SearchView searchView;
    private RequestQueue requestQueue;

    // Récupération sécurisée de la clé API
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        try {
            // Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Initialisation des vues
            recyclerView = findViewById(R.id.recyclerViewResults);
            progressBar = findViewById(R.id.progressBar);
            tvNoResults = findViewById(R.id.tvNoResults);
            tvNoConnection = findViewById(R.id.tvNoConnection);
            searchView = findViewById(R.id.searchView);

            // RecyclerView
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
                adapter = new SearchResultAdapter(this, results, anime -> {
                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    intent.putExtra("tmdb_id", anime.getId());
                    intent.putExtra("title", anime.getTitle());
                    intent.putExtra("poster_path", anime.getPosterPath());
                    intent.putExtra("overview", anime.getOverview());
                    intent.putExtra("release_date", anime.getReleaseDate());
                    intent.putExtra("vote_average", anime.getVoteAverage());
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            }

            // Volley
            requestQueue = Volley.newRequestQueue(this);

            // SearchView Listener

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchAnime(query);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.length() > 2) {
                            searchAnime(newText);
                        } else if (newText.isEmpty()) {
                            fetchPopularAnimes();
                        }
                        return true;
                    }
                });


            fetchPopularAnimes();

        } catch (Exception e) {
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void searchAnime(String query) {
        if (!isNetworkAvailable()) {
            progressBar.setVisibility(View.GONE);
            tvNoConnection.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        tvNoConnection.setVisibility(View.GONE);

        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY
                + "&query=" + query.replace(" ", "%20")
                + "&with_genres=16&language=fr-FR";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        results.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            JSONArray genreIds = obj.getJSONArray("genre_ids");
                            boolean isAnime = false;
                            for (int j = 0; j < genreIds.length(); j++) {
                                if (genreIds.getInt(j) == 16) {
                                    isAnime = true;
                                    break;
                                }
                            }
                            if (isAnime) {
                                results.add(new AnimeSearchResult(
                                        obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.optString("overview", "Aucun synopsis"),
                                        obj.optString("poster_path", ""),
                                        obj.optString("release_date", "Date inconnue"),
                                        obj.optDouble("vote_average", 0)
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (results.isEmpty()) {
                            tvNoResults.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvNoResults.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    tvNoConnection.setVisibility(View.VISIBLE);
                });
        requestQueue.add(request);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    // Menu (réutilise menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(SearchActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.collect) {
            Intent intent = new Intent(SearchActivity.this, CollectionActivity.class);
            startActivity(intent);
        } else if (id == R.id.search) {
            Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
    private void fetchPopularAnimes() {
        if (!isNetworkAvailable()) {
            tvNoConnection.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        tvNoConnection.setVisibility(View.GONE);

        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY
                + "&with_genres=16&sort_by=popularity.desc&language=fr-FR";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        results.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            results.add(new AnimeSearchResult(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.optString("overview", "Aucun synopsis"),
                                    obj.optString("poster_path", ""),
                                    obj.optString("release_date", "Date inconnue"),
                                    obj.optDouble("vote_average", 0)
                            ));
                        }
                        adapter.updateResults(results);
                        if (results.isEmpty()) {
                            tvNoResults.setVisibility(View.VISIBLE);
                            tvNoResults.setText("Aucun anime populaire trouvé");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvNoResults.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    tvNoConnection.setVisibility(View.VISIBLE);
                });
        requestQueue.add(request);
    }

}