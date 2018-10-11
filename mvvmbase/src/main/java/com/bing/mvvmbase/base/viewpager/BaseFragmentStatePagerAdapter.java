package com.bing.mvvmbase.base.viewpager;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class BaseFragmentStatePagerAdapter<E> extends FragmentStatePagerAdapter {
	protected List<E> mData;

	public BaseFragmentStatePagerAdapter(FragmentManager fm, List<E> data) {
		super(fm);
		mData = data;
	}

	@Override
	public Fragment getItem(int position) {
		return null;
	}

	@Override
	public int getCount() {
		return mData.size();
	}
}
