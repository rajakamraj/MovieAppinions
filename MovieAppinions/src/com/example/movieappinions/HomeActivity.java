package com.example.movieappinions;

import java.util.ArrayList;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeActivity extends Activity implements MovieListFragment.OnFragmentInteractionListener{

	ImageView myReviews, favorites, search, friends, logout, sync;
	LinearLayout layout1, layout2, layout3, layout4;
	ProgressDialog dialog;	
	static boolean showSortMenu=false;
	static boolean showContactSortMenu=false;
	ArrayList<Movie> movieList=new ArrayList<Movie>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		/*ParsePush.subscribeInBackground("P" + ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
			  @Override
			  public void done(ParseException e) {
			    if (e == null) {
			      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
			    } else {
			      Log.e("com.parse.push", "failed to subscribe for push", e);
			    }
			  }
			});*/
		
		myReviews = (ImageView) findViewById(R.id.imageView1);
		favorites = (ImageView) findViewById(R.id.imageView2);
		search = (ImageView) findViewById(R.id.imageView3);
		friends = (ImageView) findViewById(R.id.imageView4);
		sync = (ImageView) findViewById(R.id.imageView5);
		logout = (ImageView) findViewById(R.id.imageView6);
		layout1 = (LinearLayout) findViewById(R.id.layout1);
		layout2 = (LinearLayout) findViewById(R.id.layout2);
		layout3 = (LinearLayout) findViewById(R.id.layout3);
		layout4 = (LinearLayout) findViewById(R.id.layout4);
		
		dialog = new ProgressDialog(this);
		dialog.setMessage("Syncing Contacts, Please wait...");
		dialog.setIndeterminate(false);
		dialog.setMax(100);
		dialog.setProgress(0);
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		myReviews.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isConnectedOnline()){
					Toast.makeText(HomeActivity.this, "Not Connected to Internet", Toast.LENGTH_LONG).show();
				}
				else{
					getFragmentManager().beginTransaction()
					.replace(R.id.LinearLayout1, new MyReviews("Reviews"), "reviews")
					.addToBackStack("reviews")
					.commit();
				//	HomeActivity.showSortMenu=false;
					layout1.setVisibility(View.GONE);
					layout2.setVisibility(View.GONE);
					layout3.setVisibility(View.GONE);
					layout4.setVisibility(View.GONE);
				}
			}
		});
		favorites.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isConnectedOnline()){
					Toast.makeText(HomeActivity.this, "Not Connected to Internet", Toast.LENGTH_LONG).show();
				}
				else{
					getFragmentManager().beginTransaction()
					.replace(R.id.LinearLayout1, new MovieListFragment("Favorites"), "movie_list")
					.addToBackStack("favorites")
					.commit();
					showSortMenu=true;
					layout1.setVisibility(View.GONE);
					layout2.setVisibility(View.GONE);
					layout3.setVisibility(View.GONE);
					layout4.setVisibility(View.GONE);
				}
			}
		});
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				getFragmentManager().beginTransaction()
				.add(R.id.LinearLayout1, new MovieSearchFragment(), "movie_search")
				.addToBackStack("movie_list")
				.commit();
				//HomeActivity.showSortMenu=false;
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.GONE);
				layout3.setVisibility(View.GONE);
				layout4.setVisibility(View.GONE);
			}
		});
		friends.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isConnectedOnline()){
					Toast.makeText(HomeActivity.this, "Not Connected to Internet", Toast.LENGTH_LONG).show();
				}
				else{
					getFragmentManager().beginTransaction()
					.add(R.id.LinearLayout1, new FriendsReviewList("allreview",0), "friends_review")
					.addToBackStack("friends_review")
					.commit();
				//	HomeActivity.showSortMenu=false;
					layout1.setVisibility(View.GONE);
					layout2.setVisibility(View.GONE);
					layout3.setVisibility(View.GONE);
					layout4.setVisibility(View.GONE);	
				}
			}
		});
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, MainActivity.class);
				/*ParsePush.unsubscribeInBackground("P" + ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
					  @Override
					  public void done(ParseException e) {
					    if (e == null) {
					      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
					    } else {
					      Log.e("com.parse.push", "failed to subscribe for push", e);
					    }
					  }
					});*/
				ParseUser.logOut();
				finish();
				startActivity(i);				
			}
		});
		sync.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isConnectedOnline()){
					Toast.makeText(HomeActivity.this, "Not Connected to Internet", Toast.LENGTH_LONG).show();
				}
				else{
					dialog.show();
					new MergeContacts().execute();
				}
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if(getFragmentManager().getBackStackEntryCount() > 0){
			getFragmentManager().popBackStackImmediate();
			if(getFragmentManager().getBackStackEntryCount() == 0){
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.VISIBLE);
				layout3.setVisibility(View.VISIBLE);
				layout4.setVisibility(View.VISIBLE);
				setTitle("Home");
				HomeActivity.showSortMenu=false;
				HomeActivity.showContactSortMenu=false;
				invalidateOptionsMenu();
			}
		}
		else{
			finish();
			super.onBackPressed();
		}

			
	}

	public class MergeContacts extends AsyncTask<Void, Integer, Void>{
		ArrayList<ParseObject> users;
		@Override
		protected synchronized Void doInBackground(Void... params) {
			
			//Remove old contacts
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Contacts");
			query.whereEqualTo("owner", ParseUser.getCurrentUser().getUsername());
			query.setLimit(1000);
			ArrayList<ParseObject> contactsList = null;
			try {
				contactsList = (ArrayList<ParseObject>) query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	for(int a = 0; a < contactsList.size(); a ++){
	        	try {
					contactsList.get(a).delete();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
	        	publishProgress((int) (10f * (a / contactsList.size())));
	        }
			
			query = ParseQuery.getQuery("_User");
			query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
			query.setLimit(1000);
			try {
				users = (ArrayList<ParseObject>) query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			publishProgress(20);
			syncContacts();

			dialog.dismiss();
			return null;
		}
		
		protected void syncContacts(){
			Log.d("demo", "GG");
			ContentResolver cr = getContentResolver();
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
	                null, null, null, null);
	        float count = cur.getCount();
	        float current = 1;
	        if (cur.getCount() > 0) {
	            while (cur.moveToNext()) {
	                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	                  if (Integer.parseInt(cur.getString(
	                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
	                     Cursor pCur = cr.query(
	                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                               null,
	                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
	                               new String[]{id}, null);
	                     if (pCur.moveToNext()) {
	                         String phoneNum = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                         if(phoneNum.length() > 10){
	                        	 if(phoneNum.substring(0,1).equals("+")){
	                        		 phoneNum = phoneNum.substring(phoneNum.length() - 10);
	                        	 }
	                        	 else{
	                        		 Log.d("demo", "Phone O: " + phoneNum);
	                        		 phoneNum = phoneNum.replaceAll("[(|)| |-]", "");
	                        		 Log.d("demo", "Phone C: " + phoneNum);
	                        	 }
	                         }
	                         ParseObject contact = new ParseObject("Contacts");
	                         contact.put("owner", ParseUser.getCurrentUser().getUsername());
	                         contact.put("name", name);
	                         contact.put("phoneNum", phoneNum);
	                         if(name.equals("AppinionsTest")){
	                        	 Log.d("phoneNum", phoneNum + ":");
	                         }
	                         for(ParseObject obj : users){
	             				if(obj.getString("username").equals(phoneNum)){
	             					contact.saveInBackground();
	             				}
	             			}
	                     }
	                     pCur.close();
	                }
                     publishProgress(20 + (int)(90f * current / count));

                     current ++;
	            }
	        }
			cur.close();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			publishProgress(0);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			dialog.setProgress(values[0]);
		}
		
	}
	
	public boolean isConnectedOnline(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()){
			return true;
		}
		return false;
	}

	@Override
	public Context getContext() {
		return this;
	}
	
	@Override
	public void showfriendsReview(int id) {
		getFragmentManager().beginTransaction()
    	.replace(R.id.LinearLayout1, new FriendsReviewList("review",id),"friends_review")
    	.addToBackStack(null)
    	.commit();
		//HomeActivity.showSortMenu=false;
	}
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
		 if(HomeActivity.showSortMenu)
	        getMenuInflater().inflate(R.menu.apps_menu, menu);
		 else if(HomeActivity.showContactSortMenu)
			 getMenuInflater().inflate(R.menu.apps_sort_contacts, menu);
	        return true;
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	       MovieListFragment fragment;
	       fragment = (MovieListFragment) getFragmentManager().findFragmentByTag("movie_list"); 

      	 MyReviews fragmentReview;
      	 fragmentReview = (MyReviews) getFragmentManager().findFragmentByTag("reviews"); 
      	 
    	 FriendsReviewList fragmentFriendReview;
    	 fragmentFriendReview = (FriendsReviewList) getFragmentManager().findFragmentByTag("friends_review"); 	
      	 
	        if(fragment!=null)
	        {
	        	
	        if (id == R.string.sortByMovieNameAscending) {
	        	fragment.sortList(getString(R.string.sortByMovieNameAscending)); 
	        }
	        else  if (id == R.string.sortByRatingAscending) {
	        	fragment.sortList(getString(R.string.sortByRatingAscending));
	        }
	        else  if (id == R.string.sortByYearAscending) {
	        	fragment.sortList(getString(R.string.sortByYearAscending));
	        }
	        else  if (id == R.string.sortByMovieNameDescending) {
	        	fragment.sortList(getString(R.string.sortByMovieNameDescending)); 
	        }
	        else  if (id == R.string.sortByRatingDescending) {
	        	fragment.sortList(getString(R.string.sortByRatingDescending));
	        }
	        else  if (id == R.string.sortByYearDescending) {
	        	fragment.sortList(getString(R.string.sortByYearDescending));
	        }
	        }
	        else if(fragmentReview!=null)
	        {
	        	 if (id == R.string.sortByMovieNameAscending) {
	        		 fragmentReview.sortMyReviewList(getString(R.string.sortByMovieNameAscending)); 
	 	        }
	 	        else  if (id == R.string.sortByRatingAscending) {
	 	        	fragmentReview.sortMyReviewList(getString(R.string.sortByRatingAscending));
	 	        }
	 	        else  if (id == R.string.sortByYearAscending) {
	 	        	fragmentReview.sortMyReviewList(getString(R.string.sortByYearAscending));
	 	        }
	 	        else  if (id == R.string.sortByMovieNameDescending) {
	 	        	fragmentReview.sortMyReviewList(getString(R.string.sortByMovieNameDescending)); 
	 	        }
	 	        else  if (id == R.string.sortByRatingDescending) {
	 	        	fragmentReview.sortMyReviewList(getString(R.string.sortByRatingDescending));
	 	        }
	 	        else  if (id == R.string.sortByYearDescending) {
	 	        	fragmentReview.sortMyReviewList(getString(R.string.sortByYearDescending));
	 	        }
	        }
	        else if(fragmentFriendReview!=null)
	        {
	        	if (id == R.string.sortByNameAscending) {
	        		fragmentFriendReview.sortListFriendReview(getString(R.string.sortByNameAscending)); 
	 	        }
	 	        else  if (id == R.string.sortByNameDescending) {
	 	        	fragmentFriendReview.sortListFriendReview(getString(R.string.sortByNameDescending));
	 	        }
	 	        else  if (id == R.string.sortByReviewsAscending) {
	 	        	fragmentFriendReview.sortListFriendReview(getString(R.string.sortByReviewsAscending));
	 	        }
	 	        else  if (id == R.string.sortByReviewsDescending) {
	 	        	fragmentFriendReview.sortListFriendReview(getString(R.string.sortByReviewsDescending)); 
	 	        }
	        }
	        return true;
	    }
	@Override
	public void displayReview(String from, Movie movie) {
		Log.d("here","but no");
		getFragmentManager().beginTransaction()
    	.replace(R.id.LinearLayout1, new MovieReviewFragment(from,movie),"movieReview")
    	.addToBackStack(null)
    	.commit();
		//HomeActivity.showSortMenu=false;
	}

	@Override
	public void initializeMovieList(ArrayList<Movie> movieList) {
		// TODO Auto-generated method stub
		this.movieList= movieList;
	}
}
