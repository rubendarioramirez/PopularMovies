package com.android.rramirez.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchTrailersTask  extends AsyncTask<String, Void, String[]> {

    private MovieDetail movieDetail;

    public FetchTrailersTask(MovieDetail movieDetail) {
        this.movieDetail = movieDetail;
    }


    private String[] getTrailerFromJson(String TrailerJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(TrailerJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray("results");


            String[] resultsStr = new String[1];
            //Define variables to retrieve pertienent data
            String key;

            // Get the JSON object representing the day
            JSONObject oneMovie = movieArray.getJSONObject(0);

            //Retrieve all important info, self explanatory by the title
            key = oneMovie.getString("key");


            resultsStr[0] = key;

        /* for (String s : resultStrs) {
            //Log.v(LOG_TAG, "Movies entry: " + s);
        }*/
        return resultsStr;
    }

    @Override
    protected String[] doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJSONstr = null;

        //Add here your API KEY
        String apiKey = "517f8ef140f1f60cea8eead4849d8e93";

        try {


            //http://api.themoviedb.org/3/movie/150540/videos
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            //Get the passed ID from MovieDetail
            final String trailerID = params[0];
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(trailerID)
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY, apiKey)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v("APPLOG", "URL FOR THE MOVIE IS: " + url);

            // Create the request, and open the connection
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
            Log.e("Movie Trailer", "Error ", e);
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
                    Log.e("Movie Trailer", "Error closing stream", e);

                }

            }

        }
        try {
            return getTrailerFromJson(moviesJSONstr);
        } catch (JSONException e) {
            Log.e("Movie Trailer", e.getMessage(), e);
                    e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(String[] results) {
        if (results != null) {


            for (String s : results) {
                //ParseInfo, comencing from 0 will return in a different Index each result such as Title, Poster, etc
                movieDetail.ArrayTrailerData.add(s);
                Log.v("Results are", "" + s);

            }

            movieDetail.showData();
        }

    }
}
