package com.bing.mvvmbase.base.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecycleViewAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
	protected List<T> mData = new ArrayList<>();
	protected OnClickListener mListener;

	public List<T> getData() {
		return mData;
	}

	@NonNull
	@Override
	public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return createHolder(parent, viewType);
	}

	// 在createHolder中使用
	protected ViewDataBinding getBinding(@NonNull ViewGroup parent, int viewType, int layoutId) {
		return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
	}

	protected abstract VH createHolder(@NonNull ViewGroup parent, int viewType);

	/**
	 * 子类不用继承
	 */
	@Override
	public void onBindViewHolder(@NonNull VH holder, final int position) {
		holder.mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClick(position);
				}
			}
		});
		bindData(holder, position);
		holder.mBinding.executePendingBindings();
	}

	protected abstract void bindData(VH holder, final int position);

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public void setData(final List<T> data) {
		if (data == null) {
			return;
		}
		if (mData == null) {
			mData = data;
			notifyItemRangeInserted(0, data.size());
		} else {
			DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
				@Override
				public int getOldListSize() {
					return mData.size();
				}

				@Override
				public int getNewListSize() {
					return data.size();
				}

				@Override
				public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
					return mData.get(oldItemPosition).equals(data.get(newItemPosition));
				}

				@Override
				public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
					return mData.get(oldItemPosition).equals(data.get(newItemPosition));
				}
			});
			mData = data;
			result.dispatchUpdatesTo(this);
		}
	}

	public interface OnClickListener {
		void onClick(int position);
	}
}
