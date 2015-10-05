package com.pack.pack.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.app.R;
import com.pack.pack.model.NavigatorItem;

/**
 * 
 * @author Saurav
 *
 */
public class SideNavigationAdapter extends BaseAdapter {
	
	private Context appContext;
	private List<NavigatorItem> navItems;
	
	public SideNavigationAdapter(Context appContext, List<NavigatorItem> navItems) {
		this.appContext = appContext;
		this.navItems = navItems;
	}

	@Override
	public int getCount() {
		return navItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater layoutInflator = (LayoutInflater) appContext
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflator.inflate(R.layout.navigator_item, null);
		}
		ImageView imageView = (ImageView)convertView.findViewById(R.id.nav_item_icon);
		imageView.setImageResource(navItems.get(position).getIcon());
		TextView textView = (TextView)convertView.findViewById(R.id.nav_item_title);
		textView.setText(navItems.get(position).getTitle());
		return convertView;
	}

}
