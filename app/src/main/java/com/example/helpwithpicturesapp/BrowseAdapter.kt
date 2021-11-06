package com.example.helpwithpicturesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BrowseAdapter(val context: Context, val templateList: List<String>):
    RecyclerView.Adapter<BrowseAdapter.ViewHolder>(){

        val layoutInflater = LayoutInflater.from(context)

        inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {


            val templateName = itemView.findViewById<TextView>(R.id.templateName)
            var templatePosition = 0

            init {
                templateName.setOnClickListener{
                    val intent = Intent(context, ToDoActivity::class.java)
                    intent.putExtra(Constants.TEMPLATENAME, templatePosition)
                    context.startActivity(intent)
                }

            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): BrowseAdapter.ViewHolder {
            val itemView = layoutInflater.inflate(R.layout.list_browse,parent,false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ActionsRecycleViewAdapter.ViewHolder, position: Int) {
            val templateList = templateList[position]

            holder.templatePosition = position
            holder.templateName.text = templateList
        }

        override fun getItemCount(): Int {
            return templateList.size
        }

