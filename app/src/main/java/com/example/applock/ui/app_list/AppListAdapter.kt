package com.example.applock.ui.app_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.applock.R
import com.example.applock.databinding.LayoutItemAppBinding
import com.example.applock.model.AppLock


class AppListAdapter(val type: Int = 0, val itemOnClick: (AppLock) -> Unit) :
    ListAdapter<AppLock, AppListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AppLock>() {
        override fun areItemsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem == newItem
        }

    }) {
    private var list: ArrayList<AppLock> = arrayListOf()

    inner class ViewHolder(val layoutItemAppBinding: LayoutItemAppBinding) :
        RecyclerView.ViewHolder(layoutItemAppBinding.root) {
        fun bind(appLock: AppLock) {
            itemView.setOnClickListener {
                itemOnClick.invoke(appLock)
            }
            if (type == 0) {
                layoutItemAppBinding.lock.setImageResource(R.drawable.unlock)
            } else layoutItemAppBinding.lock.setImageResource(R.drawable.lock)
            layoutItemAppBinding.appName.text = appLock.appName
            Glide.with(layoutItemAppBinding.root).load(appLock.drawable)
                .into(layoutItemAppBinding.logoApp)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    fun setData(list: List<AppLock>) {
        submitList(list)
    }

}