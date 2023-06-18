package com.example.applock.ui.list_app_lock_private

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.applock.databinding.LayoutItemPrivateLockBinding
import com.example.applock.model.AppLock

class AppLockAdapter(val itemOnClick: (AppLock) -> Unit) :
    ListAdapter<AppLock, AppLockAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AppLock>() {
        override fun areItemsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppLock, newItem: AppLock): Boolean {
            return oldItem == newItem
        }

    }) {

    inner class ViewHolder(val binding: LayoutItemPrivateLockBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appLock: AppLock) {
            binding.root.setOnClickListener {
                itemOnClick.invoke(appLock)
            }
            binding.lock.isChecked = !appLock.pass.isNullOrEmpty()
            binding.lock.isClickable = false
            binding.appName.text = appLock.appName
            Glide.with(binding.root).load(appLock.drawable)
                .into(binding.logoApp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutItemPrivateLockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}