package com.android.rramirez.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ImageAdapter MovieArrayAdapter;
    public ArrayList<String> ArrayPosterData;
    public ArrayList<String> ArrayIDData;
    public ArrayList<String> ArrayTitleData;
    public ArrayList<String> ArrayReleaseData;
    public ArrayList<String> ArrayVoteData;
    public ArrayList<String> ArrayPlotData;
    public ArrayList<String> ArrayTrailerData;
    public ProgressDialog loadingDialog;
    public TextView sort_by_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_info);


        //Find the sortbytextview
        sort_by_tv = (TextView)findViewById(R.id.sort_by_tv);

        //Declare Arrays to help with the data
        ArrayPosterData = new ArrayList<String>();
        ArrayTitleData = new ArrayList<String>();
        ArrayReleaseData = new ArrayList<String>();
        ArrayVoteData = new ArrayList<String>();
        ArrayPlotData = new ArrayList<String>();
        ArrayIDData = new ArrayList<String>();
        ArrayTrailerData = new ArrayList<String>();

        //ArrayAdapter uses 2 parameters defined in ImageAdapter.
        //This: which is the app context.
        //ArrayListData: which is the data retrieved from AsyncTask.
        MovieArrayAdapter = new ImageAdapter(this, ArrayPosterData);

        GridView myGridView = (GridView) findViewById(R.id.gridView);
        myGridView.setAdapter(MovieArrayAdapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                //Launch Movie details and pass proper parameters
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("Title", ArrayTitleData.get(position));
                intent.putExtra("Release", ArrayReleaseData.get(position));
                intent.putExtra("Vote", ArrayVoteData.get(position));
                intent.putExtra("Plot", ArrayPlotData.get(position));
                intent.putExtra("Poster", ArrayPosterData.get(position));
                intent.putExtra("ID", ArrayIDData.get(position));
                startActivity(intent);
            }
        });

    };

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
            updateMovies();
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        //Create a dialog to communicate progress to the user. Specially useful for slow connections
        loadingDialog = ProgressDialog.show(this, "Loading your favourite movies", "Please wait", true);
        updateMovies();
        super.onStart();
    }

    //Supporting function to update movies
    public void updateMovies() {
        //Get sharedPref and get info from sort_by_list key
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_by_pref = prefs.getString("sort_by_list","0");

        //Update the textView to display the type of sorting
        switch (sort_by_pref){
            case "0":  sort_by_tv.setText("Sorted By: Most Popular");
                break;
            case "1": sort_by_tv.setText("Sorted By: Highest Rating");
                break;
        }
        FetchMoviesTask fetchMovies = new FetchMoviesTask(this);
        fetchMovies.execute(sort_by_pref);

    }


}
