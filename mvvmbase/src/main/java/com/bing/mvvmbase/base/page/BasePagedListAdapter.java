package com.bing.mvvmbase.base.page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bing.mvvmbase.base.IsSame;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

public abstract class BasePagedListAdapter<T extends IsSame, VH extends BaseViewHolder> extends PagedListAdapter<T, VH> {

	private OnClickListener mListener;

	public BasePagedListAdapter(OnClickListener listener) {
		super(new DiffUtil.ItemCallback<T>() {
			@Override
			public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
				return oldItem.itemSame(newItem);
			}

			@Override
			public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
				return oldItem.contentSame(newItem);
			}
		});
		mListener = listener;
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
	 * 注意，必须调用一次getItem(int postion),子类不需要继承
	 * @param holder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(@NonNull final VH holder, final int position) {
		holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClick(position);
				}
			}
		});
		bindData(holder, position);
		holder.binding.executePendingBindings();
	}

	protected abstract void bindData(VH holder, final int position);

	public interface OnClickListener {
		void onClick(int position);
	}
}
