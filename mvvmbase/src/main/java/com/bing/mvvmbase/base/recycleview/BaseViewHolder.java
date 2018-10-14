package com.bing.mvvmbase.base.recycleview;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
	protected final DB mBinding;

	public BaseViewHolder(DB binding) {
		super(binding.getRoot());
		mBinding = binding;
	}

}
