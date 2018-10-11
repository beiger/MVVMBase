package com.bing.mvvmbase.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseViewModel extends AndroidViewModel implements DefaultLifecycleObserver {
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	public BaseViewModel(@NonNull Application application) {
		super(application);
	}

	protected void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		mCompositeDisposable.dispose();
	}
}
