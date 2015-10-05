package com.pack.pack.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pack.pack.adapters.SideNavigationAdapter;
import com.pack.pack.model.NavigatorItem;
import com.pack.pack.navigation.fragments.AccountFragment;
import com.pack.pack.navigation.fragments.GroupsFragment;
import com.pack.pack.navigation.fragments.HomeFragment;
import com.pack.pack.navigation.fragments.PacksFragment;
import com.pack.pack.navigation.fragments.SettingsFragment;

/**
 * 
 * @author Saurav
 *
 */
public class HomeActivity extends AppCompatActivity	{

	private ActionBarDrawerToggle actionBarToggle;
	
	private DrawerLayout sideNavLayout;
	private ListView sideNavView;
	
	private String[] itemTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		sideNavLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		sideNavView = (ListView)findViewById(R.id.sideNavView);
		
		itemTitles = getResources().getStringArray(R.array.nav_item_titles);
		TypedArray typedArray = getResources().obtainTypedArray(R.array.nav_item_icons);
		
		List<NavigatorItem> navigatorItems = new ArrayList<NavigatorItem>();
		int len = itemTitles.length;
		for(int i=0; i<len; i++) {
			String title = itemTitles[i];
			int icon = typedArray.getResourceId(i, -1);
			NavigatorItem item = new NavigatorItem(title, icon);
			navigatorItems.add(item);
		}
		
		sideNavView.setOnItemClickListener(new SideNavigationClickListener());
		
		SideNavigationAdapter navPresenter = new SideNavigationAdapter(getApplicationContext(), navigatorItems);
		sideNavView.setAdapter(navPresenter);
		
		typedArray.recycle();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		actionBarToggle = new ActionBarDrawerToggle(this, sideNavLayout,
				R.drawable.ic_drawer, 
				R.string.app_name, 
				R.string.app_name 
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(getTitle());
				//invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(getTitle());
				//invalidateOptionsMenu();
			}
		};
		sideNavLayout.setDrawerListener(actionBarToggle);
		showDetails(0);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(actionBarToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if(item.getItemId() == R.id.gallery_action) {
			startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/*MenuItem menuItem = menu.findItem(R.menu.main);
		if(menuItem != null) {
			menuItem.setVisible(false);
		}*/
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void showDetails(int position) {
		Fragment fragment = null;
		switch(position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new PacksFragment();
			break;
		case 2:
			fragment = new GroupsFragment();
			break;
		case 3:
			fragment = new AccountFragment();
			break;
		case 4:
			fragment = new SettingsFragment();
			break;
		}
		if(fragment != null) {
			getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
			sideNavView.setItemChecked(position, true);
			sideNavView.setSelection(position);
			setTitle(itemTitles[position]);
			sideNavLayout.closeDrawer(sideNavView);
		}
	}
	
	private class SideNavigationClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			showDetails(position);
		}
	}
}