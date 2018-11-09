package com.bing.mvvmbase.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel implements DefaultLifecycleObserver {
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	protected AppExecutors mAppExecutors;

	public BaseViewModel(@NonNull Application application) {
		super(application);
		mAppExecutors = ((BaseApplication) application).getAppExecutors();
	}

	public void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	protected void onCleared() {
		mCompositeDisposable.dispose();
		super.onCleared();
	}

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
                
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {

        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {

        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {

        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {

        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {

        }
}
