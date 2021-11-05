package com.example.helpwithpicturesapp

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HowToDoItRecycleViewAdapter (val context: Context, val actionStep: List<Actions> ):
    RecyclerView.Adapter<HowToDoItRecycleViewAdapter.ViewHolder>() {

    val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val imageView = itemView.findViewById<ImageView>(R.id.howToImageView)
        val imageText = itemView.findViewById<TextView>(R.id.howToTextView)
        val checkBoxView = itemView.findViewById<CheckBox>(R.id.howToCheckBox)
        var actionsPosition = 0

        init {
            checkBoxView.setOnClickListener {

                actionStep[actionsPosition].checkBox = checkBoxView.isChecked
                if (actionStep[actionsPosition].checkBox) {

                    // Variabler för att göra ActionSteps gråa när man klickar på dom.
                    val grey = ColorMatrix()
                    grey.setSaturation(0F)
                    val greyfilter = ColorMatrixColorFilter(grey)

                    imageView.colorFilter = greyfilter


                } else {
                    imageView.colorFilter = null
                }
            }
        }

    }



    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): HowToDoItRecycleViewAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.how_to_do_it_list_item,parent,false)



        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HowToDoItRecycleViewAdapter.ViewHolder, position: Int) {
        val action = actionStep[position]

        Glide.with(context).load(action.imageId).into(holder.imageView)
        holder.checkBoxView.isChecked = action.checkBox
        holder.actionsPosition = position
        holder.imageText.text = action.imageText


    }

    override fun getItemCount(): Int {
        return actionStep.size
    }
}