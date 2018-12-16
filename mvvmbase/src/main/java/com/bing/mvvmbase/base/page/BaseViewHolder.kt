package com.bing.mvvmbase.base.page

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<DB : ViewDataBinding>(val binding: DB) : RecyclerView.ViewHolder(binding.root)
