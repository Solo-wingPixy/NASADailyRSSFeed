package com.jc.nasadailyrssfeed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class NasaDailyPagerAdapter extends FragmentStatePagerAdapter {

	private MyDetailFragment detailFragment = null;
	private int countNum;

	public NasaDailyPagerAdapter(FragmentManager fm, int countNum) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.countNum = countNum;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub

		detailFragment = MyDetailFragment.newInstance(position + 1);
		return detailFragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return countNum;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ""+(position+1)+" of "+countNum;
	}

}
