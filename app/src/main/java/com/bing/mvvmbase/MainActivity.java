package com.bing.mvvmbase;

import com.bing.mvvmbase.base.BaseActivity;
import com.bing.mvvmbase.databinding.ActivityMainBinding;

import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
	@Override
	protected void initViewModel() {
		mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
	}

	@Override
	protected int layoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void bindAndObserve() {

	}
}
