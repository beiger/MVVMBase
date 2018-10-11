package com.bing.mvvmbase.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;

public abstract class BaseActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity {

	protected DB mBinding;
	protected VM mViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateFirst();
		mViewModel = initViewModel();
		getLifecycle().addObserver(mViewModel);
		mBinding = DataBindingUtil.setContentView(this, layoutId());
		bindAndObserve();
	}

	protected void onCreateFirst() {

	}

	protected abstract VM initViewModel();
	protected abstract int layoutId();
	protected abstract void bindAndObserve();

	protected void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.dispose();
	}
}
