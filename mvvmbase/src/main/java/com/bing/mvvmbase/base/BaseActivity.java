package com.bing.mvvmbase.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;
import android.view.View;

public abstract class BaseActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity implements View.OnClickListener {

	protected DB mBinding;
	protected VM mViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateFirst();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		mBinding = DataBindingUtil.setContentView(this, layoutId());
		bindAndObserve();
	}

	protected void onCreateFirst() {

	}

	protected abstract void initViewModel();
	protected abstract int layoutId();
	protected abstract void bindAndObserve();

	protected void addOnClickListener(@NonNull View... views) {
		for (View view : views) {
			view.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {

	}

	public void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	protected void onDestroy() {
		mCompositeDisposable.dispose();
		super.onDestroy();
	}
}
