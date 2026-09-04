package com.absensiseleksi.ui.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.absensiseleksi.R
import com.absensiseleksi.data.local.entity.AbsensiEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatAdapter(private val onItemClick: (AbsensiEntity) -> Unit) :
    ListAdapter<AbsensiEntity, RiwayatAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvNameHistory)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(item: AbsensiEntity) {
            tvName.text = item.userId
            
            val dateSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            tvDate.text = dateSdf.format(Date(item.timestamp))
            tvTime.text = timeSdf.format(Date(item.timestamp))
            
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AbsensiEntity>() {
            override fun areItemsTheSame(oldItem: AbsensiEntity, newItem: AbsensiEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: AbsensiEntity, newItem: AbsensiEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}