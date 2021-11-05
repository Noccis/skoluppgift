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

class ActionsRecycleViewAdapter(val context: Context, val action: List<Actions> ,
                                val decision : String , val pinkod :String):
    RecyclerView.Adapter<ActionsRecycleViewAdapter.ViewHolder>(){

    val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val imageButtonView = itemView.findViewById<ImageView>(R.id.imageButton)
        val imageText = itemView.findViewById<TextView>(R.id.imageText)
        val checkBoxView = itemView.findViewById<CheckBox>(R.id.checkBox_Button)
        val stepsImage = itemView.findViewById<ImageView>(R.id.stepsImage)
        var actionsPosition = 0

        init {
            stepsImage.setOnClickListener{
                val intent = Intent(context,HowToDoItActivity::class.java)
                intent.putExtra(ACTIONS_POSITION_KEY, actionsPosition)
                intent.putExtra(Constants.DAY_CHOSEN, decision)
                intent.putExtra(ACTION_LOCATION, action[actionsPosition].documentName)
                intent.putExtra(Constants.PINKOD, pinkod)
                context.startActivity(intent)
            }
            imageButtonView.setOnClickListener{
                val intent = Intent(context,HowToDoItActivity::class.java)
                intent.putExtra(ACTIONS_POSITION_KEY, actionsPosition)
                intent.putExtra(Constants.DAY_CHOSEN, decision)
                intent.putExtra(ACTION_LOCATION, action[actionsPosition].documentName)
                intent.putExtra(Constants.PINKOD, pinkod)
                context.startActivity(intent)
            }
            checkBoxView.setOnClickListener {
                action[actionsPosition].checkBox = checkBoxView.isChecked
                if (action[actionsPosition].checkBox) {

                    val toDoActivity = context as ToDoActivity
                    toDoActivity.reward()
                }
            }
            stepsImage.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ActionsRecycleViewAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_actions,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionsRecycleViewAdapter.ViewHolder, position: Int) {
        val action = action[position]

        if (action.steps) {
            holder.stepsImage.visibility = View.VISIBLE
        } else holder.stepsImage.visibility = View.GONE

        Glide.with(context).load(action.imageId).into(holder.imageButtonView)
        holder.checkBoxView.isChecked = action.checkBox
        holder.actionsPosition = position
        holder.imageText.text = action.imageText
    }

    override fun getItemCount(): Int {
        return action.size
    }
}