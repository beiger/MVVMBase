package com.bing.mvvmbase.base.page

import android.view.LayoutInflater
import android.view.ViewGroup

import com.bing.mvvmbase.base.IsSame
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

abstract class BasePagedListAdapter<T : IsSame, VH : BaseViewHolder<*>>(private val mListener: OnClickListener?) : PagedListAdapter<T, VH>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.itemSame(newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem.contentSame(newItem)
        }
}) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return createHolder(parent, viewType)
        }

        // 在createHolder中使用
        open fun getBinding(parent: ViewGroup, viewType: Int, layoutId: Int): ViewDataBinding {
                return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        }

        protected abstract fun createHolder(parent: ViewGroup, viewType: Int): VH

        /**
         * 注意，必须调用一次getItem(int postion),子类不需要继承
         * @param holder
         * @param position
         */
        override fun onBindViewHolder(holder: VH, position: Int) {
                holder.binding.root.setOnClickListener {
                        mListener?.onClick(position)
                }
                bindData(holder, position)
                holder.binding.executePendingBindings()
        }

        protected abstract fun bindData(holder: VH, position: Int)

        interface OnClickListener {
                fun onClick(position: Int)
        }
}
