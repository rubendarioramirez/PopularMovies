package com.android.rramirez.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MovieDetail extends AppCompatActivity {

    private TextView title_tv;
    private TextView release_tv;
    private TextView vote_tv;
    private TextView plot_tv;
    private ImageView poster_iv;

    private String title;
    private String release;
    private String votes;
    private String plot;
    private String poster;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        //Instantiate the bundle and get those juicy extras
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Get extras by name
        String title = (String) bundle.get("Title");
        String release = (String) bundle.get("Release");
        String votes = (String) bundle.get("Vote");
        String plot = (String) bundle.get("Plot");
        String poster = (String) bundle.get("Poster");

        //Dont forget to get the elements by id
        title_tv = (TextView)findViewById(R.id.title_tv);
        release_tv = (TextView)findViewById(R.id.release_tv);
        vote_tv = (TextView)findViewById(R.id.vote_tv);
        plot_tv = (TextView)findViewById(R.id.plot_tv);
        poster_iv =(ImageView)findViewById(R.id.poster_iv);


        //Set to each element a different dataSource
        title_tv.setText(title);
        release_tv.setText("Release date: " + release);
        vote_tv.setText("Rating: " + votes);
        plot_tv.setText(plot);
        url = "https://image.tmdb.org/t/p/w500" + poster;

      //  Picasso.with(this).load(poster).into(poster_iv);
        Picasso.with(this).load(url).into(poster_iv);

    }
    public String getMovieData(){


        return title;
    }

}
