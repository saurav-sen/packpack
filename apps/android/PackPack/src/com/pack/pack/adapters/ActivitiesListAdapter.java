package com.pack.pack.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pack.pack.app.R;
import com.pack.pack.app.controller.AppController;
import com.pack.pack.model.ActivityFeed;
import com.pack.pack.widgets.ImageFeedView;

/**
 * 
 * @author Saurav
 *
 */
public class ActivitiesListAdapter extends ArrayAdapter<ActivityFeed> {
	
	private LayoutInflater inflater;
	
	private ImageLoader imageLoader;
	
	private List<ActivityFeed> activities;

	public ActivitiesListAdapter(Context context, List<ActivityFeed> activities) {
		super(context, R.layout.activity_feed_item,  activities.toArray(new ActivityFeed[activities.size()]));
		this.activities = activities;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	@Override
	public int getCount() {
		return activities.size();
	}
	
	@Override
	public ActivityFeed getItem(int position) {
		return activities.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(inflater == null) {
			inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.activity_feed_item, null);
		}
		if(imageLoader == null) {
			imageLoader = AppController.getInstance().getImageLoader();
		}
		ActivityFeed feed = getItem(position);
		NetworkImageView profilePic = (NetworkImageView)convertView.findViewById(R.id.activity_profilePic);
		TextView name = (TextView)convertView.findViewById(R.id.activity_name);
		TextView timestamp = (TextView)convertView.findViewById(R.id.activity_timestamp);
		TextView statusMsg = (TextView)convertView.findViewById(R.id.activity_txtStatusMsg);
		TextView url = (TextView)convertView.findViewById(R.id.activity_txtUrl);
		ImageFeedView packImage = (ImageFeedView)convertView.findViewById(R.id.activity_packImage);
		
		profilePic.setImageUrl(feed.getProfilePic(), imageLoader);
		name.setText(feed.getName());
		timestamp.setText(DateUtils.getRelativeTimeSpanString(
				Long.parseLong(feed.getTimestamp()),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
		statusMsg.setText(feed.getStatus());
		if (feed.getUrl() != null) {
			url.setText(Html.fromHtml("<a href=\"" + feed.getUrl() + "\">"
					+ feed.getUrl() + "</a> "));
			url.setMovementMethod(LinkMovementMethod.getInstance());
			url.setVisibility(View.VISIBLE);
		} 
		else {
			url.setVisibility(View.GONE);
		}
		
		if(feed.getImage() != null) {
			packImage.setImageUrl(feed.getImage(), imageLoader);
			packImage.setVisibility(View.VISIBLE);
		}
		else {
			packImage.setVisibility(View.GONE);
		}
		return convertView;
	}
}