package com.bing.mvvmbase.base.recycleview

import android.view.LayoutInflater
import android.view.ViewGroup

import com.bing.mvvmbase.base.IsSame

import java.util.ArrayList
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecycleViewAdapter<T : IsSame> : RecyclerView.Adapter<BaseViewHolder<*>>() {
        protected var mData: List<T>? = ArrayList()
        protected var mListener: OnClickListener? = null

        var data: List<T>?
                get() = mData
                set(data) {
                        if (data == null) {
                                return
                        }
                        if (mData == null) {
                                mData = data
                                notifyItemRangeInserted(0, data.size)
                        } else {
                                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                                        override fun getOldListSize(): Int {
                                                return mData!!.size
                                        }

                                        override fun getNewListSize(): Int {
                                                return data.size
                                        }

                                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                                return mData!![oldItemPosition].itemSame(data[newItemPosition])
                                        }

                                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                                return mData!![oldItemPosition].contentSame(data[newItemPosition])
                                        }
                                })
                                mData = data
                                result.dispatchUpdatesTo(this)
                        }
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
                return createHolder(parent, viewType)
        }

        // 在createHolder中使用
        protected fun getBinding(parent: ViewGroup, viewType: Int, layoutId: Int): ViewDataBinding {
                return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        }

        protected abstract fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*>

        /**
         * 子类不用继承
         */
        override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
                holder.binding.root.setOnClickListener {
                        if (mListener != null) {
                                mListener!!.onClick(position)
                        }
                }
                bindData(holder, position)
                holder.binding.executePendingBindings()
        }

        protected abstract fun bindData(holder: BaseViewHolder<*>, position: Int)

        override fun getItemCount(): Int {
                return mData!!.size
        }

        interface OnClickListener {
                fun onClick(position: Int)
        }
}
