package com.android.rramirez.popularmovies;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private  ArrayList<String> data;


    //URL Helpers
    private String base_url;
    private String imageSize;
    private String url;

    public ImageAdapter(Context c, ArrayList<String> ArrayPosterData) {
        mContext = c;
        data = ArrayPosterData;
    }

    public int getCount() {
        return 12;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(450, 450));
        } else {
            imageView = (ImageView) convertView;
        }

        //URL Helpers - Possible to change image size for "w92", "w154", "w185", "w342", "w500", "w780"
        base_url = "https://image.tmdb.org/t/p";
        imageSize = "/w342";
        url = base_url + imageSize;



        if(data.size() > 0){
            //Log.v("APPLOG" ,"Data is" + data.get(position));
            Picasso.with(this.mContext).load(url + data.get(position)).resize(100, 100).into(imageView);
        }

        return imageView;
    }

}
