package com.pack.pack.adapters;

import com.pack.pack.navigation.fragments.ECardsTab;
import com.pack.pack.navigation.fragments.EToysGiftsTab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 
 * @author Saurav
 *
 */
public class GalleryAdapter extends FragmentPagerAdapter {

	public GalleryAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		Fragment fragment = null;
		switch(index) {
		case 0:
			fragment = new ECardsTab();
			break;
		case 1:
			fragment = new EToysGiftsTab();
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}
}