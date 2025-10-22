package com.example.memorygame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.databinding.ItemCardBinding


class GameAdapter(
    private val onCardClicked: (Int) -> Unit
) : ListAdapter<MemoryCard, GameAdapter.CardViewHolder>(CardDiffCallback) {

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: MemoryCard) {
            if (card.isFlipped || card.isMatched) {
                binding.cardImageView.setImageResource(card.id)
            } else {
                binding.cardImageView.setImageResource(R.drawable.ic_launcher_background)
            }
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                onCardClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

}

object CardDiffCallback : DiffUtil.ItemCallback<MemoryCard>() {
    override fun areItemsTheSame(
        oldItem: MemoryCard,
        newItem: MemoryCard
    ): Boolean {
        return oldItem.uniqueId == newItem.uniqueId
    }

    override fun areContentsTheSame(
        oldItem: MemoryCard,
        newItem: MemoryCard
    ): Boolean {
        return oldItem == newItem
    }

}