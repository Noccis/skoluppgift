package com.example.helpwithpicturesapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ActionsRecycleViewAdapter(val context: Context, val actions: List<Actions>):
RecyclerView.Adapter<ActionsRecycleViewAdapter.ViewHolder>(){

    val layoutInflater = LayoutInflater.from(context)
    val db = FirebaseFirestore.getInstance()
    val actionsRef = db.collection("Weekdays").document("Days")

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val imageButtonView = itemView.findViewById<ImageView>(R.id.imageButton)
        val checkBoxView = itemView.findViewById<CheckBox>(R.id.checkBox_Button)
        var actionsPosition = 0

        init {
            imageButtonView.setOnClickListener{
                val intent = Intent(context,HowToDoItActivity::class.java)
                intent.putExtra(ACTIONS_POSITION_KEY, actionsPosition)
                context.startActivity(intent)
            }
        }

    }



    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): ActionsRecycleViewAdapter.ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_actions,parent,false)



        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionsRecycleViewAdapter.ViewHolder, position: Int) {
        val actions = actions[position]


        Glide.with(context).load(actions.imageId).into(holder.imageButtonView)
        holder.checkBoxView.isChecked = actions.checkBox
        holder.actionsPosition = position


    }

    override fun getItemCount(): Int {
        return actions.size
    }
}
