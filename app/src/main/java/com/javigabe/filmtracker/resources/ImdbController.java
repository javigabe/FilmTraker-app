package com.javigabe.filmtracker.resources;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javigabe.filmtracker.FilmActivity;
import com.javigabe.filmtracker.HomeActivity;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class ImdbController extends AsyncTask<String, Void, ArrayList<Film>> {
        private static final String KEY = "c6b297a2343bd15b8f36043773dfd35a";
        private HashMap<Long, String> genres;

        private AppCompatActivity activity;

        // No me gusta tener que recibir el home activity como parametro del constructor
        // TODO: BUSCAR SOLUCION PARA NO TENER QUE RECIBIRLO
        public ImdbController(AppCompatActivity activity) {
                this.activity = activity;
                genres = new HashMap<>();
                setUpGenres();
        }


        @Override
        protected ArrayList<Film> doInBackground(String... title) {
                if (title[1].equals("title")) {
                        return searchFilmbyTitle(title[0]);
                } else if (title[1].equals("id")) {
                        return searchFilmbyId(title[0]);
                }
                return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Film> films) {
                if (films != null && films.size() > 0 && !isCancelled()) {
                        if (activity instanceof HomeActivity) {
                                HomeActivity homeActivity = (HomeActivity) activity;
                                homeActivity.setUpRecyclerView(films);
                        } else if (activity instanceof FilmActivity) {
                                FilmActivity filmActivity = (FilmActivity) activity;
                                filmActivity.setView(films.get(0));
                        }
                }
                super.onPostExecute(films);
        }


        private ArrayList<Film> searchFilmbyId(String id) {
                if (!isCancelled()) {
                        String query = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + KEY;
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(query)
                                .get()
                                .build();

                        String jsonResponse = null;
                        try {
                                Response response = client.newCall(request).execute();
                                jsonResponse = response.body().string();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        return parseJsontoFilm(jsonResponse);
                }
                return null;
        }


        private ArrayList<Film> searchFilmbyTitle(String title) {
                if (!isCancelled() && !title.isEmpty()) {
                        String query = "https://api.themoviedb.org/3/search/movie?api_key=" + KEY + "&query=" + title;
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(query)
                                .get()
                                .build();

                        String jsonResponse = null;
                        try {
                                Response response = client.newCall(request).execute();
                                jsonResponse = response.body().string();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        return parseJsontoFilms(jsonResponse);
                }
                return null;
        }

        private ArrayList<Film> parseJsontoFilm(String jsonResponse) {
                JSONParser jsonParser = new JSONParser();
                ArrayList<Film> filmArray = new ArrayList<>();

                try {
                        JSONObject jsonFilm = (JSONObject) jsonParser.parse(jsonResponse);
                        String id = jsonFilm.get("id").toString();
                        String name = (String) jsonFilm.get("title");
                        String sinopsis = (String) jsonFilm.get("overview");
                        JSONArray genres = (JSONArray) jsonFilm.get("genres");
                        String imageUrl = (String) jsonFilm.get("poster_path");
                        Bitmap image = getImage(imageUrl);

                        String mainGenre = null;

                        if (genres != null && genres.size() > 0) {
                                JSONObject genreObject = (JSONObject) genres.get(0);
                                mainGenre = (String) genreObject.get("name");
                        }

                        // TODO cambiar el null por la imagen de la pelicula
                        Film film = new Film(id,name, mainGenre, image, sinopsis);
                        filmArray.add(film);
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                return filmArray;
        }

        private ArrayList<Film> parseJsontoFilms(String jsonResponse) {
                JSONParser jsonParser = new JSONParser();
                ArrayList<Film> filmsArray = new ArrayList<>();

                try {
                        JSONObject jsonFilms = (JSONObject) jsonParser.parse(jsonResponse);
                        JSONArray films = (JSONArray) jsonFilms.get("results");
                        if (films == null) return null;

                        for (Object filmObj : films) {
                                JSONObject jsonFilm = (JSONObject) filmObj;

                                String id = jsonFilm.get("id").toString();
                                String name = (String) jsonFilm.get("title");
                                JSONArray filmGenres = (JSONArray) jsonFilm.get("genre_ids");
                                Long genreId = (Long) (filmGenres != null && filmGenres.size() > 0 ? filmGenres.get(0) : (long) 0);
                                String mainGenre = genres.get(genreId);

                                //JSONObject image = (JSONObject) jsonFilm.get("i");
                                String imageUrl = (String) jsonFilm.get("poster_path");
                                Bitmap image = getImage(imageUrl);

                                // TODO cambiar el null por la imagen de la pelicula
                                Film film = new Film(id, name, mainGenre, image);
                                filmsArray.add(film);
                        }
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                return filmsArray;
        }

        public Bitmap resizeImage(Bitmap myBitmap, int desiredWidth, int desiredHeight) {
                Bitmap resizedBitmap;

                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();
                float scaleWidth = ((float) desiredWidth) / width;
                float scaleHeight = ((float) desiredHeight) / height;
                // CREATE A MATRIX FOR THE MANIPULATION
                Matrix matrix = new Matrix();
                // RESIZE THE BIT MAP
                matrix.postScale(scaleWidth, scaleHeight);

                // "RECREATE" THE NEW BITMAP
                resizedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, width, height,
                        matrix, false);
                return resizedBitmap;
        }

        private Bitmap getImage(String imageUrl) {
                if (imageUrl != null) {
                        String fullUrl = "https://image.tmdb.org/t/p/w500" + imageUrl;
                        try {
                                URL url = new URL(fullUrl);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                return BitmapFactory.decodeStream(input);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
                return null;
        }

        private void setUpGenres() {
                genres.put((long) 28, "Action");
                genres.put((long) 12, "Adventure");
                genres.put((long) 16, "Animation");
                genres.put((long) 35, "Comedy");
                genres.put((long) 80, "Crime");
                genres.put((long) 99, "Documentary");
                genres.put((long) 18, "Drama");
                genres.put((long) 10751, "Family");
                genres.put((long) 14, "Fantasy");
                genres.put((long) 36, "History");
                genres.put((long) 27, "Horror");
                genres.put((long) 10402, "Music");
                genres.put((long) 9648, "Mystery");
                genres.put((long) 10749, "Romance");
                genres.put((long) 878, "Science Fiction");
                genres.put((long) 10770, "TV Movie");
                genres.put((long) 53, "Thriller");
                genres.put((long) 10752, "War");
                genres.put((long) 37, "Western");
        }
}
