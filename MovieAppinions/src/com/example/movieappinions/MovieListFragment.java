package com.example.movieappinions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Used to show a list of movies returned by Rotten Tomatoes or a list of your friends
 *
 */
public class MovieListFragment extends Fragment {
	
	String type;
	String searchTerm;
	EditText itemText;
	ListView textListView;
	Intent i;
	FriendListViewAdapter adapter;
	ArrayAdapter<Movie> moviesAdapter;
	ArrayList<Movie> movies;
	ArrayList<Contact> contacts;
	AlertDialog alertDialog;
	AlertDialog.Builder alertDialogBuilder;
	 
	private OnFragmentInteractionListener mListener;
public void sortList(String sortBy)
{
	Log.d("check",sortBy);
	showDialog();
	if(sortBy.equals(getString(R.string.sortByMovieNameAscending)))
	{
	Collections.sort(movies, new Comparator<Movie>() {

		@Override
		public int compare(Movie lhs, Movie rhs) {
			// TODO Auto-generated method stub
			return lhs.getTitle().compareTo(rhs.getTitle());
		}
    });
	}
	else if(sortBy.equals(getString(R.string.sortByRatingAscending)))
	{
		Collections.sort(movies, new Comparator<Movie>() {

			@Override
			public int compare(Movie lhs, Movie rhs) {
				// TODO Auto-generated method stub
				return lhs.getRaRating()-rhs.getRaRating();
			}
	    });
	}
	else if(sortBy.equals(getString(R.string.sortByYearAscending)))
	{
		Collections.sort(movies, new Comparator<Movie>() {

			@Override
			public int compare(Movie lhs, Movie rhs) {
				// TODO Auto-generated method stub
				return lhs.getYear().compareTo(rhs.getYear());
			}
	    });
	}
	else if(sortBy.equals(getString(R.string.sortByMovieNameDescending)))
	{
	Collections.sort(movies, new Comparator<Movie>() {

		@Override
		public int compare(Movie lhs, Movie rhs) {
			// TODO Auto-generated method stub
			return rhs.getTitle().compareTo(lhs.getTitle());
		}
    });
	}
	else if(sortBy.equals(getString(R.string.sortByRatingDescending)))
	{
		Collections.sort(movies, new Comparator<Movie>() {

			@Override
			public int compare(Movie lhs, Movie rhs) {
				// TODO Auto-generated method stub
				return rhs.getRaRating()-lhs.getRaRating();
			}
	    });
	}
	else if(sortBy.equals(getString(R.string.sortByYearDescending)))
	{
		Collections.sort(movies, new Comparator<Movie>() {

			@Override
			public int compare(Movie lhs, Movie rhs) {
				// TODO Auto-generated method stub
				return rhs.getYear().compareTo(lhs.getYear());
			}
	    });
	}

	moviesAdapter = new MovieListViewAdapter(mListener.getContext(), R.layout.movie_list_item, movies);
	((ListView) getView().findViewById(R.id.contacts_list)).setAdapter(moviesAdapter);
	moviesAdapter.notifyDataSetChanged();
	alertDialog.dismiss();
}
	public MovieListFragment(String type){
		super();
		this.type = type;
	}
	
	public MovieListFragment(String type, String searchTerm){
		super();
		this.type = type;
		this.searchTerm = searchTerm;
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_movie_list, container, false);
		Log.d("demo", "onCreateView()");
		return view;
	}
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("demo", "onCreate()");			
		 
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("demo", "onActivityCreated");
		HomeActivity.showSortMenu=true;
		getActivity().invalidateOptionsMenu();
		alertDialogBuilder = new AlertDialog.Builder(mListener.getContext());
		if(type.equals("Search")){
			alertDialogBuilder.setTitle("Loading Movies...")
				.setIcon(R.drawable.app_logo)
				.setMessage("")
				.setCancelable((false));
		}
		else if(type.equals("Friends")){
			alertDialogBuilder.setTitle("Loading Friends...")
			.setIcon(R.drawable.app_logo)
			.setMessage("")
			.setCancelable((false));
		}
		else if(type.equals("Favorites")){
			alertDialogBuilder.setTitle("Loading Favorites...")
			.setIcon(R.drawable.app_logo)
			.setMessage("")
			.setCancelable((false));
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		showDialog();
		Log.d("demo", "onstart");
		if(type.equals("Search")){
			retrieveMovies();
		}
		else if(type.equals("Friends")){
			retrieveContacts();
		}
		else if(type.equals("Favorites")){
			retrieveFavorites();
		}
        //alertDialog.dismiss();
	}

	/**
	 * Retrieves all contacts and how many movies they have reviewed
	 */
	private void retrieveContacts(){
		contacts = new ArrayList<Contact>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Contacts");
		query.whereEqualTo("owner", ParseUser.getCurrentUser().getUsername());
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> contactsList, ParseException e) {
		        if (e == null) {
		            Log.d("score", "Retrieved " + contactsList.size() + " contacts");
		            for(ParseObject obj : contactsList){
		            	Log.d("demo", "a" + (contactsList == null));
		            	Contact temp = new Contact();
		            	
		            	ArrayList<String> test = (ArrayList<String>) obj.get("name");
		            	temp.setName(test.get(0));
		            	temp.setNumber(obj.getString("phoneNum"));
			        	contacts.add(temp);
			        	Log.d("demo", "b");
			        }
			        adapter = new FriendListViewAdapter(mListener.getContext(), android.R.layout.simple_list_item_1, contacts, getFragmentManager());
			        ((ListView) getActivity().findViewById(R.id.contacts_list)).setAdapter(adapter);
			        
			        
		            
		        } else {
		            Log.d("score", "Error: " + e.getMessage());
		        }
			}
		});
	}
	
	private void retrieveFavorites(){
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorites");
		query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
		//query.whereEqualTo("favourite", true);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> favoritesList, ParseException e) {
				if(e != null){
					Log.d("demo", "g: " + e.toString());
					Toast.makeText(mListener.getContext(), "No Results Found", Toast.LENGTH_LONG).show();
					alertDialog.dismiss();
					mListener.onBackPressed();
				}
				else{
					try {
						movies = JSONUtils.MovieParser.parseMovies((ArrayList<ParseObject>) favoritesList);
					} catch (JSONException e1) {
						//e1.printStackTrace();
					}
					mListener.initializeMovieList(movies);
					moviesAdapter = new MovieListViewAdapter(mListener.getContext(), R.layout.movie_list_item, movies);
					((ListView) getView().findViewById(R.id.contacts_list)).setAdapter(moviesAdapter);
					((ListView) getView().findViewById(R.id.contacts_list)).setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							Log.d("what","cmg");
							mListener.displayReview("main", movies.get(position));
						}
						
					});
					alertDialog.dismiss();
					if(favoritesList.size() == 0){
						Toast.makeText(mListener.getContext(), "No Results Found", Toast.LENGTH_LONG).show();
						mListener.onBackPressed();
					}
				}
			}
		});
	}
	
	/**
	 * Retrieved movies related to the search term
	 */
	private void retrieveMovies(){
		String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=" + APIKeys.ROTTEN_TOMATOES_API_KEY + "&q=" + searchTerm;
		Log.d("Demo", url);
		new GetMovies().execute(url);
	}
	
	public interface OnFragmentInteractionListener {
		public Context getContext();
		public ContentResolver getContentResolver();
		public void onBackPressed();
		public boolean isConnectedOnline();
		public void setTitle(CharSequence title);
		public void displayReview(String from,Movie m);
		public void showfriendsReview(int id);
		public void initializeMovieList(ArrayList<Movie> movieList);
	}

	public void showDialog(){
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
	
	public class GetMovies extends AsyncTask<String, Void, ArrayList<Movie>>{

	
		public GetMovies() {

		}		
		
		@Override
		protected ArrayList<Movie> doInBackground(String... params) {
			try {
				URL url = new URL(params[0]);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int statusCode = con.getResponseCode();
				if(statusCode == HttpURLConnection.HTTP_OK){
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                            sb.append(line);
                    }
                    return JSONUtils.MovieParser.parseMovies(sb.toString());
				}
				else{
					Log.d("demo", "" + statusCode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Movie> result) {
			super.onPostExecute(result);
			Log.d("demo","tt");
			if(result != null){
				movies = result;
				moviesAdapter = new MovieListViewAdapter(mListener.getContext(), R.layout.movie_list_item, movies);
				((ListView) getActivity().findViewById(R.id.contacts_list)).setAdapter(moviesAdapter);
				((ListView) getActivity().findViewById(R.id.contacts_list)).setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						getFragmentManager().beginTransaction()
				    	.replace(R.id.LinearLayout1, new MovieReviewFragment("main",movies.get(position)),"movieReview")
				    	.addToBackStack(null)
				    	.commit();
						HomeActivity.showSortMenu=false;
						getActivity().invalidateOptionsMenu();
					}
					
				});

				if(result.size() == 0){
					Toast.makeText(mListener.getContext(), "No Results Found", Toast.LENGTH_LONG).show();
					mListener.onBackPressed();
				}
			}
			else{
				Toast.makeText(mListener.getContext(), "No Results Found", Toast.LENGTH_LONG).show();
				mListener.onBackPressed();
			}
			
			alertDialog.dismiss();
		}	
	}

	@Override
	public void onResume() {
		super.onResume();
		HomeActivity.showSortMenu=true;
		getActivity().invalidateOptionsMenu();
		if(type.equals("Search")){
			mListener.setTitle("Movie Results");
		}
		else if(type.equals("Friends")){
			mListener.setTitle("Your Friends");
		}
		else if(type.equals("Favorites")){
			mListener.setTitle("Favorite Movies");
		}
	}
	
	
}
