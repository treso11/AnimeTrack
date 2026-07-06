package com.tp.animetrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import java.io.FileOutputStream;

public class SettingsActivity extends AppCompatActivity {

    private EditText etUsername, etApiKey;
    private RadioGroup radioGroupTheme;
    private Switch switchDarkMode;
    private Button btnSaveApi, btnExport, btnClearData;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AnimeTrackPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_API_KEY = "api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialisation SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialisation des vues
        etUsername = findViewById(R.id.etUsername);
        etApiKey = findViewById(R.id.etApiKey);

        btnSaveApi = findViewById(R.id.btnSaveApi);

        btnClearData = findViewById(R.id.btnClearData);

        // Charger les données sauvegardées
        loadSavedData();

        // Écouteurs
        btnSaveApi.setOnClickListener(v -> saveApiKey());
        btnClearData.setOnClickListener(v -> showClearDataDialog());
    }

    private void loadSavedData() {
        // Charger nom d'utilisateur
        String username = sharedPreferences.getString(KEY_USERNAME, "TRESOR");
        etUsername.setText(username);

        // Sauvegarder auto le nom quand il change
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                sharedPreferences.edit().putString(KEY_USERNAME, etUsername.getText().toString()).apply();
                Toast.makeText(this, "Nom sauvegardé", Toast.LENGTH_SHORT).show();
            }
        });

        // Charger la clé API
        String apiKey = sharedPreferences.getString(KEY_API_KEY, "");
        etApiKey.setText(apiKey);

    }

    private void saveApiKey() {
        String apiKey = etApiKey.getText().toString().trim();
        if (!apiKey.isEmpty()) {
            sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply();
            Toast.makeText(this, "Clé API sauvegardée", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Clé API vide", Toast.LENGTH_SHORT).show();
        }
    }
    private void showClearDataDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Supprimer toutes les données")
                .setIcon(R.drawable.logo)
                .setMessage("Êtes-vous sûr ? Cette action est irréversible.")
                .setPositiveButton("Oui", (dialog, which) -> clearAllData())
                .setNegativeButton("Non", null)
                .show();
    }

    private void clearAllData() {
        try {
            // Supprimer le fichier de collection
            deleteFile("collection.json");
            // Réinitialiser SharedPreferences (garder la clé API)
            String apiKey = sharedPreferences.getString(KEY_API_KEY, "");
            sharedPreferences.edit().clear().apply();
            if (!apiKey.isEmpty()) {
                sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply();
            }
            Toast.makeText(this, "Toutes les données ont été supprimées", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
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