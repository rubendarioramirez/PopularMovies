package com.android.rramirez.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ImageAdapter MovieArrayAdapter;
    public ArrayList<String> ArrayPosterData;
    public ArrayList<String> ArrayTitleData;
    public ArrayList<String> ArrayReleaseData;
    public ArrayList<String> ArrayVoteData;
    public ArrayList<String> ArrayPlotData;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);

        //Create a dialog to communicate progress to the user. Specially useful for slow connections
        loadingDialog = ProgressDialog.show(this, "Loading your favourite movies", "Please wait", true);

        //Declare Arrays to help with the data
        ArrayPosterData = new ArrayList<String>();
        ArrayTitleData = new ArrayList<String>();
        ArrayReleaseData = new ArrayList<String>();
        ArrayVoteData = new ArrayList<String>();
        ArrayPlotData = new ArrayList<String>();

        //ArrayAdapter uses 2 parameters defined in ImageAdapter.
        //This: which is the app context.
        //ArrayListData: which is the data retrieved from AsyncTask.
        MovieArrayAdapter = new ImageAdapter(this, ArrayPosterData);

        GridView myGridView = (GridView) findViewById(R.id.gridView);
        myGridView.setAdapter(MovieArrayAdapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Toast for debugging purposes.
                //Toast.makeText(MainActivity.this, ArrayTitleData.get(position), Toast.LENGTH_SHORT).show();

                //Launch Movie details and pass proper parameters
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("Title", ArrayTitleData.get(position));
                intent.putExtra("Release", ArrayReleaseData.get(position));
                intent.putExtra("Vote", ArrayVoteData.get(position));
                intent.putExtra("Plot", ArrayPlotData.get(position));
                intent.putExtra("Poster", ArrayPosterData.get(position));
                startActivity(intent);
            }
        });

    }

    ;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        updateMovies();
        super.onStart();
    }

    //Supporting function to update movies
    public void updateMovies() {
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute();
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
            for (int i = 0; i < movieAmount; i++) {

                //Define variables to retrieve pertienent data
                String id;
                String title;
                String release;
                String poster;
                String votes;
                String plot;

                // Get the JSON object representing the day
                JSONObject oneMovie = movieArray.getJSONObject(i);

                //Retrieve all important info, self explanatory by the title
                id = oneMovie.getString("id");
                title = oneMovie.getString("original_title");
                release = oneMovie.getString("release_date");
                poster = oneMovie.getString("poster_path");
                votes = oneMovie.getString("vote_average");
                plot = oneMovie.getString("overview");

                //This is for debugging purposes
                //resultStrs[i] = id + title + release + poster + votes + overview;
                resultStrs[i] = poster + "," + title + "," + release + "," + votes + "," + plot;
            }

            /*Only for debug uses
            for (String s : resultStrs) {
                //Log.v(LOG_TAG, "Movies entry: " + s);
            }*/
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
            int movieAmount = 12;

            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, order)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

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
            if (results != null) {
                ArrayPosterData.clear();
                for (String s : results) {
                    //ParseInfo, comencing from 0 will return in a different Index each result such as Title, Poster, etc
                    String[] splitResult = s.split(",");
                    //Log.v("APPLOG", "" + splitResult[1] );

                    //Add data to the PosterArray list
                    ArrayPosterData.add(splitResult[0]);
                    //Add data to the TItle List
                    ArrayTitleData.add(splitResult[1]);
                    //ADd data to the Release list
                    ArrayReleaseData.add(splitResult[2]);
                    //Add data to the Votes list
                    ArrayVoteData.add(splitResult[3]);
                    //Add data to the Plot list
                    ArrayPlotData.add(splitResult[4]);

                    //Notify the adapter that the list has data now, otherwise will be empty.
                    MovieArrayAdapter.notifyDataSetChanged();
                }
            }
            //Dismiss dialog after AsyncTask is done.
            loadingDialog.dismiss();
        }
    }


}
