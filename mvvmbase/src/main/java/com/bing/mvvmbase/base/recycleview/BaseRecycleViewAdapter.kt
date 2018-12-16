package com.bing.mvvmbase.base.recycleview

import android.view.LayoutInflater
import android.view.ViewGroup

import com.bing.mvvmbase.base.IsSame

import java.util.ArrayList
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecycleViewAdapter<T : IsSame, VH: BaseViewHolder<*>> : RecyclerView.Adapter<VH>() {
        var data: List<T>? = ArrayList()
                set(data_temp) {
                        if (data_temp == null) {
                                return
                        }
                        if (data == null) {
                                data = data_temp
                                notifyItemRangeInserted(0, data_temp.size)
                        } else {
                                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                                        override fun getOldListSize(): Int {
                                                return data!!.size
                                        }

                                        override fun getNewListSize(): Int {
                                                return data_temp.size
                                        }

                                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                                return data!![oldItemPosition].itemSame(data_temp[newItemPosition])
                                        }

                                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                                                return data!![oldItemPosition].contentSame(data_temp[newItemPosition])
                                        }
                                })
                                data = data_temp
                                result.dispatchUpdatesTo(this)
                        }
                }

        var listener: OnClickListener? = null



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return createHolder(parent, viewType)
        }

        // 在createHolder中使用
        protected fun getBinding(parent: ViewGroup, viewType: Int, layoutId: Int): ViewDataBinding {
                return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        }

        protected abstract fun createHolder(parent: ViewGroup, viewType: Int): VH

        /**
         * 子类不用继承
         */
        override fun onBindViewHolder(holder: VH, position: Int) {
                holder.binding.root.setOnClickListener {
                        if (listener != null) {
                                listener!!.onClick(position)
                        }
                }
                bindData(holder, position)
                holder.binding.executePendingBindings()
        }

        protected abstract fun bindData(holder: VH, position: Int)

        override fun getItemCount(): Int {
                return data!!.size
        }

        interface OnClickListener {
                fun onClick(position: Int)
        }
}
