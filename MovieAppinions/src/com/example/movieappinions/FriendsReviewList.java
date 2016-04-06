package com.example.movieappinions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.movieappinions.MovieListFragment.OnFragmentInteractionListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FriendsReviewList.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FriendsReviewList#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class FriendsReviewList extends Fragment {

	private String from;
	private int movieId;
	TextView friendName,noOfReviews;
	ListView friendList;
	 int i=0;
	private OnFragmentInteractionListener mListener;

	static ArrayList<Contact> friendListReview = new ArrayList<Contact>();

	FriendsReviewListAdapter friendListAdapter;
	ParseQueryAdapter<ParseObject> parseadapterTemp;
	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 *
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment FriendsReviewList.
	 */
	// TODO: Rename and change types and number of parameters
	

	public FriendsReviewList(String from,int movieId) {
		// Required empty public constructor
		this.movieId=movieId;
		this.from=from;
	}
	public void sortListFriendReview(String sortBy)
	{
		Log.d("check",sortBy);
		if(sortBy.equals(getString(R.string.sortByNameAscending)))
		{
		Collections.sort(friendListReview, new Comparator<Contact>() {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				// TODO Auto-generated method stub
				return lhs.getName().compareTo(rhs.getName());
			}
	    });
		}
		else if(sortBy.equals(getString(R.string.sortByReviewsAscending)))
		{
			Collections.sort(friendListReview, new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					// TODO Auto-generated method stub
					return lhs.getReviews()-rhs.getReviews();
				}
		    });
		}
		else if(sortBy.equals(getString(R.string.sortByNameDescending)))
		{
			Collections.sort(friendListReview, new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					// TODO Auto-generated method stub
					return rhs.getName().compareTo(lhs.getName());
				}
		    });
		}
	
		else if(sortBy.equals(getString(R.string.sortByReviewsDescending)))
		{
			Collections.sort(friendListReview, new Comparator<Contact>() {

				@Override
				public int compare(Contact lhs, Contact rhs) {
					// TODO Auto-generated method stub
					return rhs.getReviews()-lhs.getReviews();
				}
		    });
		}
		

		friendListAdapter= new FriendsReviewListAdapter(getActivity(), android.R.layout.simple_list_item_1, friendListReview, getFragmentManager(),from,movieId);
		friendListAdapter.setNotifyOnChange(true);
		friendList.setAdapter(friendListAdapter);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_friends_review_list,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		HomeActivity.showContactSortMenu=true;
		getActivity().invalidateOptionsMenu();
		
		friendName=(TextView)getView().findViewById(R.id.friendsReviewListName);
		noOfReviews=(TextView)getView().findViewById(R.id.noOfReviews);
		friendList=(ListView)getView().findViewById(R.id.friendsReviewList);
		friendListReview.clear();
		new GetShared().execute();
		
			        /* ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("SharedReviews");
			        
			         Log.d("demo", ParseUser.getCurrentUser().getUsername());
			         query.whereEqualTo("sharedTo", ParseUser.getCurrentUser().getUsername());
			         if(from.equals("review"))
			         {
				         query.whereEqualTo("movieId", movieId);
			         }
			         query.findInBackground(new FindCallback<ParseObject>() {
						    public void done(List<ParseObject> objects, ParseException e) {
					// TODO Auto-generated method stub
					if(e == null){
						Log.d("demo", "no of friends  " + objects.size());
						//listView.setAdapter(adapter);
						for(ParseObject obj:objects)
						{
				    		String userNo=(String)obj.get("sharedBy");
				    		ParseQuery<ParseObject> friendNameQuery = ParseQuery.getQuery("Contacts");
				    		friendNameQuery.whereEqualTo("phoneNum", userNo);
							 friendNameQuery.findInBackground(new FindCallback<ParseObject>() {
							    public void done(List<ParseObject> saved, ParseException e) {
							        if (e == null) {
							            Log.d("saved", "Retrieved " + saved.size() + " details");
							            if(saved.size() ==0)
							            {
							            }
							            else
							            {
							            	for(ParseObject parseObj:saved)
							            	{
							            		Log.d("name is "," value "+parseObj.getString("name"));
							            	if(friendListReview.size()==0)
							            	{
							            		Contact c=new Contact();
							            		c.setName(parseObj.getString("name"));
							            		c.setNumber(parseObj.getString("phoneNum"));
							            		c.setReviews(1);
							            		friendListReview.add(c);
							            	}
							            	else
							            	{
							            		for(Contact temp:friendListReview)
							            		{
							            			Log.d("compare","is "+(!temp.getNumber().equals(parseObj.getString("phoneNum"))));
							            		
							            			
							            			if(!temp.getNumber().equals(parseObj.getString("phoneNum")))
							            			{
							            				Contact c=new Contact();
									            		c.setName(parseObj.getString("name"));
									            		c.setNumber(parseObj.getString("phoneNum"));
									            		c.setReviews(1);
									            		friendListReview.add(c);
							            			}
							            			else
							            			{
							            				temp.incrNumber();
							            			}
							            			
							            		}
							            	}
							            }
							            }
							        } else {
							            Log.d("error", "Error: " + e.getMessage());
							        }

Log.d("the array size is","is "+friendListReview.size());
						friendListAdapter= new FriendsReviewListAdapter(getActivity(), android.R.layout.simple_list_item_1, friendListReview, getFragmentManager(),from,movieId);
						friendListAdapter.setNotifyOnChange(true);
						friendList.setAdapter(friendListAdapter);
							    }
							});
							 
						}
				     }
				}
		     });*/
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
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	@Override
	public void onResume() {
		super.onResume();
		mListener.setTitle("Reviews Shared With Me");
	}
	
	public class GetShared extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("SharedReviews");
	        
	         Log.d("demo", ParseUser.getCurrentUser().getUsername());
	         query.whereEqualTo("sharedTo", ParseUser.getCurrentUser().getUsername());
	         if(from.equals("review"))
	         {
		         query.whereEqualTo("movieId", movieId);
	         }
	         List<ParseObject> objects = null;
			try {
				objects = query.find();
			} catch (ParseException e1) {
				return null;
				//e1.printStackTrace();
			}
	     				Log.d("demo", "no of friends  " + objects.size());
	     				//listView.setAdapter(adapter);
	     				for(ParseObject obj:objects)
	     				{
	     		    		String userNo=(String)obj.get("sharedBy");
	     		    		ParseQuery<ParseObject> friendNameQuery = ParseQuery.getQuery("Contacts");
	     		    		friendNameQuery.whereEqualTo("phoneNum", userNo);
	     		    		List<ParseObject> saved = null;
	     					 try {
								saved = friendNameQuery.find();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								Log.d("error", "Error: " + e.getMessage());
								return null;
							}
 					            Log.d("saved", "Retrieved " + saved.size() + " details");
 					            if(saved.size() ==0)
 					            {
 					            }
 					            else
 					            {
 					            	
 					            	//for(ParseObject parseObj:saved)
 					            	//{
 					            		
 					            		
 					            		Log.d("name is "," value "+saved.get(0).getString("name"));
 					            	if(friendListReview.size()==0)
 					            	{
 					            		Contact c=new Contact();
 					            		c.setName(saved.get(0).getString("name"));
 					            		c.setNumber(saved.get(0).getString("phoneNum"));
 					            		c.setReviews(1);
 					            		friendListReview.add(c);
 					            	}
 					            	else
 					            	{
 					            		boolean found = false;
 					            		for(int e = 0; e < friendListReview.size(); e ++)
 					            		{
 					            			Contact temp = friendListReview.get(e);
 					            			Log.d("compare","is "+(!temp.getNumber().equals(saved.get(0).getString("phoneNum"))));

 					            			if(temp.getNumber().equals(saved.get(0).getString("phoneNum")))
 					            			{
 					            				found = true;
 					            				friendListReview.get(e).incrNumber();
 					            				break;
 					            			}
 					            			
 					            		}
 					            		if(!found){
					            				Contact c=new Contact();
							            		c.setName(saved.get(0).getString("name"));
							            		c.setNumber(saved.get(0).getString("phoneNum"));
							            		c.setReviews(1);
							            		friendListReview.add(c);
 					            		}
 					            	}
 					            	}
 					            //}
				            }
				    
	     					 
    		 return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			 Log.d("the array size is","is "+friendListReview.size());
			 if(friendListReview.size()==0)
			 {
				 Toast.makeText(mListener.getContext(), "No Reviews shared with you", Toast.LENGTH_LONG).show();
					mListener.onBackPressed();
			 }
			 else
			 {
				friendListAdapter= new FriendsReviewListAdapter(getActivity(), android.R.layout.simple_list_item_1, friendListReview, getFragmentManager(),from,movieId);
				friendListAdapter.setNotifyOnChange(true);
				friendList.setAdapter(friendListAdapter);
			 }
		}
	}
	
	

}
