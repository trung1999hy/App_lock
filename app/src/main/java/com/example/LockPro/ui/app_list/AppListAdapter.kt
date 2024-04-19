package com.example.LockPro.ui.app_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.LockPro.model.AppLock
import com.thn.applock.R
import com.thn.applock.databinding.LayoutItemAppBinding


class AppListAdapter( val itemOnClick: (AppLock) -> Unit , val itemClickAll : ()-> Unit) :
    ListAdapter<AppLock, AppListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AppLock>() {
        override fun areItemsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class ViewHolder(val layoutItemAppBinding: LayoutItemAppBinding) :
        RecyclerView.ViewHolder(layoutItemAppBinding.root) {
        fun bind(appLock: AppLock) {
            itemView.setOnClickListener {
                if (adapterPosition == 0){
                    itemClickAll.invoke()
                }else{
                    itemOnClick.invoke(appLock)
                }

            }
                layoutItemAppBinding.lock.setImageResource(R.drawable.unlock)
            layoutItemAppBinding.appName.text = appLock.appName
            Glide.with(layoutItemAppBinding.root).load(appLock.drawable)
                .into(layoutItemAppBinding.logoApp)
            if (appLock.packetName.isNullOrEmpty()) {
                layoutItemAppBinding.logoApp.visibility = View.GONE
            } else {
                layoutItemAppBinding.logoApp.visibility = View.VISIBLE
            }
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