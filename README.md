# AnimeTrack 

Application Android native de gestion de collection d'animes.

##  Installation

| Logiciel | Version | Lien |
|----------|---------|------|
| Android Studio | Dernière version | [developer.android.com/studio](https://developer.android.com/studio) |
| SDK Android | API 24 minimum | Installé avec Android Studio |
| Git | Dernière version | [git-scm.com](https://git-scm.com) |
| Compte TMDB | Gratuit | [themoviedb.org/signup](https://www.themoviedb.org/signup) |
**Cloner le projet** : git clone https://github.com/treso11/AnimeTrack.git
**Ouvrir avec Android Studio**
**Ajouter sa clé API TMDB**  : Créer local.properties à la racine wt ajouter TMDB_API_KEY=ta_cle_api_ici
**Synchroniser et lancer** :min SDK 24

##  Fonctionnalités
**Splash**  :logo animé chargement automatique 2s
**Collection** : Grille d'animes sauvegardés, recherche locale, menu contextuel 
**Recherche**  :Appel API TMDB, filtrage par genre Animation avec ID 16 
**Détail**  :Fiche complète du l anime(image,titre,date de sortie,note) + avis personnel(statut,note, date de visionnage, boutonn ajouter a la collection)
**Paramètres**:Profil utilisateur, thème, suppression

##  Technologies utilisées
**Java** :Langage principal 
**RecyclerView + CardView** : Affichage en grille 
**Glide** : Chargement des images 
**Volley** : Requêtes API TMDB 
**SharedPreferences** : Sauvegarde des paramètres 
**JSON(fichier local)** : Stockage de la collection 
**Material Design** : UI moderne(floating Button, Toolbar, etc.) 

###  Doucmentation
[Documentation TMDB](https://developers.themoviedb.org/3)
[documentation Glide](https://www.glideapps.com/docessentials/data-sources/getting-started-with-the-glide.com)
[Volley pour les requêtes HTTP](youtu.be/0as6FmJsGOM?si=zaeKfOoKOG2jue6p)
[Glide pour les images](https://bumptech.github.io/glide/)
[Material Design Components](https://material.io/components)
