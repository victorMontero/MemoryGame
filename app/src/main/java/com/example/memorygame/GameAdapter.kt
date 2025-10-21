package com.example.memorygame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class GameAdapter(
    private val onCardClicked: (Int) -> Unit
): ListAdapter<MemoryCard, GameAdapter.CardViewHolder>(CardDiffCallback()){

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imageView: ImageView = itemView.findViewById(R.id.card_image_view)

        fun bind(card: MemoryCard){
            if (card.isFlipped || card.isMatched){
                imageView.setImageResource(card.id)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
            itemView.setOnClickListener {
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CardViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

}

class CardDiffCallback : DiffUtil.ItemCallback<MemoryCard>() {
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