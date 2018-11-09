package com.bing.mvvmbase.base;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<DB extends ViewDataBinding, VM extends BaseViewModel, AVM extends BaseViewModel> extends Fragment implements View.OnClickListener {
	protected DB mBinding;
	protected VM mViewModel;
	protected AVM mActivityViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	public BaseFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			handleArguments();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
		initView();
		return mBinding.getRoot();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initActivityViewModel();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		bindAndObserve();
	}

	protected abstract void handleArguments();
	protected abstract int layoutId();

	/**
	 * 此时还没有viewModel
 	 */
	protected abstract void initView();

	protected abstract void initActivityViewModel();
	protected abstract void initViewModel();
	protected abstract void bindAndObserve();

	private void addOnClickListener(@NonNull View... views) {
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
	public void onDestroy() {
		mCompositeDisposable.dispose();
		super.onDestroy();
	}
}
