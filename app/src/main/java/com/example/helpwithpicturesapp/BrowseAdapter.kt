package com.example.helpwithpicturesapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView

class BrowseAdapter(val context: Context, val templateList: List<String> ,
                    val pinkod :String ,
                    val decision : String ):
    RecyclerView.Adapter<BrowseAdapter.ViewHolder>() {

    val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val templateName = itemView.findViewById<TextView>(R.id.templateName)
        var templatePosition = 0

        init {
            templateName.setOnClickListener {
                val intent = Intent(context, ToDoActivity::class.java)
                intent.putExtra(Constants.DAY_CHOSEN, templateList[templatePosition])
                intent.putExtra(Constants.PINKOD, pinkod).toString()
                Log.d("!!!" , "pinkod $pinkod")
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BrowseAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_browse, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BrowseAdapter.ViewHolder, position: Int) {
        val templateList = templateList[position]

        holder.templatePosition = position
        holder.templateName.text = templateList.toString()
    }

    override fun getItemCount(): Int {
        return templateList.size
    }

}