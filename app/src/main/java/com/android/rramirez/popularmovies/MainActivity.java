package com.android.rramirez.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> MovieArrayAdapter;
    public static MainActivity baseInstance;
    private ImageAdapter imageAdapter;
    private    GridView gridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);

        MovieArrayAdapter = new ArrayList<String>();


        gridview = (GridView) findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        baseInstance = this;
    }

    @Override
    public void onStart() {

        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute();
        super.onStart();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private String[] getMoviesFromJson(String MoviesJsonStr, int movieAmount)
                throws JSONException {

            JSONObject moviesJson = new JSONObject(MoviesJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray("results");

            //Get the last 10 movies
            String[] resultStrs = new String[movieAmount];
            //Hardcoded 10 because i just want the first 10
            for(int i = 0; i < movieAmount; i++) {

                //Define variables to retrieve pertienent data
                String id;
                String title;
                String release;
                String poster;
                String votes;
                String overview;

                // Get the JSON object representing the day
                JSONObject oneMovie = movieArray.getJSONObject(i);

                //Retrieve all important info, self explanatory by the title
                id = oneMovie.getString("id");
                title = oneMovie.getString("original_title");
                release = oneMovie.getString("release_date");
                poster = oneMovie.getString("poster_path");
                votes = oneMovie.getString("vote_average");
                overview = oneMovie.getString("overview");

                //This is for debugging purposes
                //resultStrs[i] = id + title + release + poster + votes + overview;
                resultStrs[i] = poster;
            }

            for (String s : resultStrs) {
                //Log.v(LOG_TAG, "Movies entry: " + s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJSONstr = null;

            String order = "popularity.desc";
            //Add here your API KEY
            String apiKey = "517f8ef140f1f60cea8eead4849d8e93";
            //How many movies we want to query
            int movieAmount = 10;

            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, order)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJSONstr = buffer.toString();

               // Log.v("APPLOG", moviesJSONstr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();

                }
                if (reader != null) {
                    try {
                        reader.close();

                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);

                    }

                }

            }
            try {
                return getMoviesFromJson(moviesJSONstr, movieAmount);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] results) {
            for (String s : results) {
                MovieArrayAdapter.add(s);

                Log.v("APPLOG", MovieArrayAdapter.get(MovieArrayAdapter.size() - 1) + "   " + MovieArrayAdapter.size());
            }


//            imageAdapter = new ImageAdapter(baseInstance);
//            gridview.setAdapter(imageAdapter);
        }
    }



}
