package com.pack.pack.navigation.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pack.pack.adapters.ActivitiesListAdapter;
import com.pack.pack.app.controller.AppController;
import com.pack.pack.model.ActivityFeed;

/**
 * 
 * @author Saurav
 *
 */
public class HomeFragment extends ListFragment {

	private ActivitiesListAdapter adapter;
	
	private List<ActivityFeed> activities;
	
	private static final String url = "http://api.androidhive.info/feed/feed.json"; //$NON-NLS-1$
	
	private static final String LOGGER_TAG = HomeFragment.class.getSimpleName();
	
	private ProgressDialog pDialog;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		activities = new ArrayList<ActivityFeed>();
		adapter = new ActivitiesListAdapter(getActivity(), activities);
		setListAdapter(adapter);
		
		openProgressDialog();
		
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, new Response.Listener<JSONObject>() {
			public void onResponse(JSONObject jObj) {
				Log.d(LOGGER_TAG, jObj.toString());
				try {
					JSONArray arr = jObj.getJSONArray("feed");
					int len = arr.length();
					for(int i=0; i<len; i++) {
						JSONObject jsonObject = arr.getJSONObject(i);
						ActivityFeed item = new ActivityFeed();
						item.setId(jsonObject.getLong("id"));
						item.setName(jsonObject.getString("name"));
						String image = jsonObject.isNull("image") ? null : jsonObject.getString("image");
						item.setImage(image);
						item.setStatus(jsonObject.getString("status"));
						item.setProfilePic(jsonObject.getString("profilePic"));
						item.setTimestamp(jsonObject.getString("timeStamp"));
						String feedUrl = jsonObject.isNull("url") ? null : jsonObject
								.getString("url");
						item.setUrl(feedUrl);
						activities.add(item);
					}
				} catch (JSONException e) {
					Log.d(LOGGER_TAG, e.getMessage(), e);
				}
				closeProgressDialog();
				adapter.notifyDataSetChanged();
			};
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError err) {
				closeProgressDialog();
				Log.d(LOGGER_TAG, err.getMessage(), err.getCause());
			}
		});
		
		AppController.getInstance().addToRequestQueue(request);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		closeProgressDialog();
	}
	
	private void openProgressDialog() {
		if(pDialog == null) {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
		}
		pDialog.show();
	}
	
	private void closeProgressDialog() {
		if(pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}
}