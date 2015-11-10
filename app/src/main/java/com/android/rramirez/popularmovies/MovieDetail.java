package com.android.rramirez.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity {

    private TextView release_tv;
    private TextView vote_tv;
    private TextView plot_tv;
    private ImageView poster_iv;
    private Button trailer_btn;

    private String title;
    private String ID;
    private String release;
    private String votes;
    private String plot;
    private String poster;
    private String url;

    public ArrayList<String> ArrayTrailerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        ArrayTrailerData = new ArrayList<String>();

        setUpActivity();

        getTrailers();


    }

    public void showData(){
        Log.v("APPLOG", "The item is: " + ArrayTrailerData.get(0));
    }

    public void getTrailers(){
        FetchTrailersTask fetchTrailers = new FetchTrailersTask(this);
        fetchTrailers.execute(ID);
    }

    public void setUpActivity(){
        //Instantiate the bundle and get those juicy extras
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //Get extras by name
        title = (String) bundle.get("Title");
        release = (String) bundle.get("Release");
        votes = (String) bundle.get("Vote");
        plot = (String) bundle.get("Plot");
        poster = (String) bundle.get("Poster");
        ID = (String) bundle.get("ID");

        //Dont forget to get the elements by id
        release_tv = (TextView)findViewById(R.id.release_tv);
        vote_tv = (TextView)findViewById(R.id.vote_tv);
        plot_tv = (TextView)findViewById(R.id.plot_tv);
        poster_iv =(ImageView)findViewById(R.id.poster_iv);
        trailer_btn =(Button)findViewById(R.id.trailer_btn);

        //Set actionTitle bar with the movie title.
        setTitle(title);
        //Set to each element a different dataSource
        release_tv.setText("Release date: " + release);
        vote_tv.setText("Rating: " + votes);
        plot_tv.setText(plot);
        url = "https://image.tmdb.org/t/p/w500" + poster;
        //Picasso to take care of display the picture
        Picasso.with(this).load(url).into(poster_iv);
    }


}
