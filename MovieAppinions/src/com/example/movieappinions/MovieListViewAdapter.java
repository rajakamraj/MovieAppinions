package com.example.movieappinions;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Used to create the custom List View items to display movies
 * The standard adapter can't handle more complex list item layouts
 */
public class MovieListViewAdapter extends ArrayAdapter<Movie> {

	Context c;
	static List<Movie> movies;
	int resources;

	public MovieListViewAdapter(Context c, int resources, List<Movie> movies) {
		super(c, android.R.layout.simple_list_item_1, movies);
		this.c = c;
		MovieListViewAdapter.movies = movies;
		this.resources = resources;
		Log.d("demo", "New Adapter");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater layoutView = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = layoutView.inflate(R.layout.movie_list_item,
					parent, false);
		}
		
		//Populate the view
		((TextView) convertView.findViewById(R.id.movie_title)).setText(movies.get(position).getTitle());
		((TextView) convertView.findViewById(R.id.movie_release_date)).setText(movies.get(position).getYear());
		((TextView) convertView.findViewById(R.id.movie_rating)).setText(movies.get(position).getRating());
		Log.d("demo", "G:" + movies.get(position).getRaRating());
		((TextView) convertView.findViewById(R.id.movie_ra_rating)).setText(movies.get(position).getRaRating() + "/100");
		String plot = movies.get(position).getPlot();
		if(plot.length() > 150){
			plot = plot.substring(0, 150) + "...";
		}
		((TextView) convertView.findViewById(R.id.movie_plot)).setText(plot);
		Picasso.with(c).load(movies.get(position).getThumbnailURL()).error(R.drawable.ic_launcher).into((ImageView) convertView.findViewById(R.id.movie_poster));
		
		return convertView;
	}

}
