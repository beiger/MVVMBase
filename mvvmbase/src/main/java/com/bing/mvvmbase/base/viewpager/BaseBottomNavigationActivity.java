package com.bing.mvvmbase.base.viewpager;

import android.os.Bundle;
import android.view.MenuItem;

import com.bing.mvvmbase.base.BaseViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseBottomNavigationActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {
	protected DB mBinding;
	protected VM mViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	protected ViewPager mViewPager;
	protected FragmentPagerAdapter mFragmentPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateFirst();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		mBinding = DataBindingUtil.setContentView(this, layoutId());
		initViewPagerAndBottomNav();
		bindAndObserve();
	}

	protected void onCreateFirst() {

	}

	protected abstract void initViewModel();
	protected abstract int layoutId();

	protected void initViewPagerAndBottomNav() {
		mViewPager = viewpager();
		mFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), initFragments());
		mViewPager.setAdapter(mFragmentPagerAdapter);
		navigationView().setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
	}

	protected abstract ViewPager viewpager();
	protected abstract BottomNavigationView navigationView();
	protected abstract List<Fragment> initFragments();
	protected abstract void bindAndObserve();

	protected void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.dispose();
	}


	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			return navigationItemSelected(item);
		}
	};

	protected abstract boolean navigationItemSelected(@NonNull MenuItem item);
}
