package com.example.helpwithpicturesapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpwithpicturesapp.activities.CreateAndEditActionSteps
import com.example.helpwithpicturesapp.R

class ImageAdapter2(val activity: CreateAndEditActionSteps,
                    val urls: List<String>
): RecyclerView.Adapter<ImageAdapter2.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val recyclerViewImageViewButton = itemView.findViewById<ImageButton>(R.id.recyclerViewImageButton)
        var url = ""

        init {
            recyclerViewImageViewButton.setOnClickListener {
             activity.setImage(url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
       holder.url = urls[position]
        Glide.with(holder.itemView).load(holder.url).into(holder.recyclerViewImageViewButton)
    }

    override fun getItemCount(): Int {
        return urls.size
    }
}