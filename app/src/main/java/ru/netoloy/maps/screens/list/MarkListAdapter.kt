package ru.netoloy.maps.screens.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netoloy.maps.databinding.CardMarkMapBinding
import ru.netoloy.maps.model.MarkMap


interface OnInteractionListener {
    fun onClick(mapPoint: MarkMap)
    fun onEdit(mapPoint: MarkMap)
    fun onRemove(mapPoint: MarkMap)
}

class MarkListAdapter (
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<MarkMap, PointLineViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointLineViewHolder {
        val binding = CardMarkMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointLineViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PointLineViewHolder, position: Int) {
        val pointLine = getItem(position)
        holder.bind(pointLine)
    }
}

class PointLineViewHolder(
    private val binding: CardMarkMapBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(markMap: MarkMap) {
        binding.apply {
            name.text = markMap.name
            latitudeValue.text = markMap.point.latitude.toString()
            longitudeValue.text = markMap.point.longitude.toString()

            binding.root.setOnClickListener {
                onInteractionListener.onClick(markMap)
            }
            edit.setOnClickListener {
                onInteractionListener.onEdit(markMap)
            }
            remove.setOnClickListener {
                onInteractionListener.onRemove(markMap)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<MarkMap>() {
    override fun areItemsTheSame(oldItem: MarkMap, newItem: MarkMap): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MarkMap, newItem: MarkMap): Boolean {
        return oldItem == newItem
    }
}
