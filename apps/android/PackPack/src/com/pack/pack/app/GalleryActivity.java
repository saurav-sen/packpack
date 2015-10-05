package com.pack.pack.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;

import com.pack.pack.adapters.GalleryAdapter;

/**
 * 
 * @author Saurav
 *
 */
public class GalleryActivity extends AppCompatActivity {
	
	private static final String[] TABS = new String[] {"Cards", "Toys & Gifts"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_layout);
		ViewPager pager = (ViewPager)findViewById(R.id.gallery_pager);
		
		pager.setAdapter(new GalleryAdapter(getSupportFragmentManager()));
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener listener = new TabSelectionListener(pager);
		int pos = 0;
		for(String TAB : TABS) {
			ActionBar.Tab tab = getSupportActionBar().newTab().setText(TAB);
			tab.setTabListener(listener);
			getSupportActionBar().addTab(tab);
			pos++;
		}
		
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	private class TabSelectionListener implements ActionBar.TabListener {
		
		private ViewPager pager;
		
		private TabSelectionListener(ViewPager pager) {
			this.pager = pager;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			pager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}
	}
}