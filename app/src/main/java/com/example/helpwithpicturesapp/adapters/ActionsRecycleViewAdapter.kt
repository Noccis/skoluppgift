package com.example.helpwithpicturesapp.adapters

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
import com.example.helpwithpicturesapp.*
import com.example.helpwithpicturesapp.activities.ToDoActivity
import com.example.helpwithpicturesapp.model.Actions


class ActionsRecycleViewAdapter(val context: Context, val action: List<Actions>,
                                val decision : String, val pinkod :String):
    RecyclerView.Adapter<ActionsRecycleViewAdapter.ViewHolder>(){

    val layoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val imageButtonView = itemView.findViewById<ImageView>(R.id.imageButton)
        val imageText = itemView.findViewById<TextView>(R.id.imageText)
        val checkBoxView = itemView.findViewById<CheckBox>(R.id.checkBox_Button)
        val stepsImage = itemView.findViewById<ImageView>(R.id.stepsImage)
        val spareImage = itemView.findViewById<TextView>(R.id.spareImage)
        var actionsPosition = 0

        init {
            imageButtonView.setOnClickListener{
                val intent = Intent(context, HowToDoItActivity::class.java)
                intent.putExtra(ACTIONS_POSITION_KEY, actionsPosition)
                intent.putExtra(Constants.DAY_CHOSEN, decision)
                intent.putExtra(ACTION_LOCATION, action[actionsPosition].documentName)
                intent.putExtra(Constants.PINKOD, pinkod)
                context.startActivity(intent)
            }
            stepsImage.setOnClickListener{
                val intent = Intent(context, HowToDoItActivity::class.java)
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
                    toDoActivity.stepIsDone(action[actionsPosition])
                }
                else {
                    val toDoActivity = context as ToDoActivity
                    toDoActivity.uncheckCheckBox(action[actionsPosition])
                }
            }
            stepsImage.visibility = View.GONE
            spareImage.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_actions,parent,false)


        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = action[position]

        if ( action.steps){
            holder.stepsImage.visibility = View.VISIBLE
            holder.spareImage.visibility = View.GONE
        } else {holder.stepsImage.visibility = View.GONE
            holder.spareImage.visibility = View.VISIBLE}

        Glide.with(context).load(action.imageId).into(holder.imageButtonView)
        holder.checkBoxView.isChecked = action.checkBox
        holder.actionsPosition = position
        holder.imageText.text = action.imageText


    }
    override fun getItemCount(): Int {
        return action.size
    }
}


