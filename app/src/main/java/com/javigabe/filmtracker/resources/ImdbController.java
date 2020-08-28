package com.javigabe.filmtracker.resources;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class ImdbController extends AsyncTask<String, Void, ArrayList<Film>> {
        private static final String KEY = "88e3cbc73cmsh68e7f56d3d95047p164a5djsn5da5c146a19c";
        private static final String HOST = "imdb8.p.rapidapi.com";

        private AppCompatActivity homeActivity;

        // No me gusta tener que recibir el home activity como parametro del constructor
        // TODO: BUSCAR SOLUCION PARA NO TENER QUE RECIBIRLO
        public ImdbController(AppCompatActivity activity) {
                this.homeActivity = activity;
        }

        public ImdbController() {}

        @Override
        protected ArrayList<Film> doInBackground(String... title) {
                if (title[1].equals("title")) {
                        return searchImagebyTitle(title[0]);
                } else if (title[1].equals("id")) {
                        // search film by its id
                }
                return null;
        }


        private ArrayList<Film> searchImagebyTitle(String title) {
                if (!isCancelled() && !title.isEmpty()) {
                        String query = "https://imdb8.p.rapidapi.com/title/auto-complete?q=" + title;
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url(query)
                                .get()
                                .addHeader("x-rapidapi-host", HOST)
                                .addHeader("x-rapidapi-key", KEY)
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

        private ArrayList<Film> parseJsontoFilms(String jsonResponse) {
                JSONParser jsonParser = new JSONParser();
                ArrayList<Film> filmsArray = new ArrayList<>();

                try {
                        JSONObject jsonFilms = (JSONObject) jsonParser.parse(jsonResponse);
                        JSONArray films = (JSONArray) jsonFilms.get("d");
                        if (films == null) return null;

                        for (Object filmObj : films) {
                                JSONObject jsonFilm = (JSONObject) filmObj;

                                String id = (String) jsonFilm.get("id");
                                String name = (String) jsonFilm.get("l");
                                String genre = (String) jsonFilm.get("q");

                                JSONObject image = (JSONObject) jsonFilm.get("i");
                                String imageUrl = (String) (image != null ? image.get("imageUrl") : null);
                                Bitmap resizedBitmap = resizeImage(imageUrl);

                                Film film = new Film(id, name, genre, resizedBitmap);
                                filmsArray.add(film);
                        }
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                return filmsArray;
        }

        private Bitmap resizeImage(String imageUrl) {
                Bitmap resizedBitmap;

                if (imageUrl != null) {
                        try {
                                URL url = new URL(imageUrl);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                                // DIMENSIONS OF R.ID.POSTER
                                int desiredWidth = 100;
                                int desiredHeight = 130;

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
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
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
                        homeActivity.setUpRecyclerView(films);
                }
                super.onPostExecute(films);
        }
}
