package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ToDoActivity : AppCompatActivity() {

    lateinit var addButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
   // val diffrentInstructions = mutableListOf<Weekday>()
    val action = mutableListOf<Actions>()
    val TAG = "!!!"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)


        recyclerView = findViewById(R.id.recyclerView)


        val db = FirebaseFirestore.getInstance()
        val actionsRef = db.collection("Actions").document("breakfast")

        
        actionsRef.get().addOnSuccessListener { snapshot ->
            if(snapshot != null) {
                Log.d(TAG,"1. onCreate: database changed!")

                val newAction = snapshot.toObject(Actions::class.java)
                Log.d(TAG, "2. onCreate: ${newAction}")
                if (newAction != null) {
                    action.add(newAction)
                }
                recyclerView.adapter?.notifyDataSetChanged()

            }

        }
        Log.d(TAG,"3 .onCreate: ${action.size}")




        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ActionsRecycleViewAdapter(this,action)

        recyclerView.adapter = adapter

        addButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            val intent = Intent(this,UserCreateAndEditActivity::class.java)
            startActivity(intent)
        }





    }


}
/*
db.collection("Weekday").document("monday").get()
  .addOnSuccessListener { snapshot ->
      if (snapshot != null) {
          val monday = snapshot.toObject(Weekday::class.java)
          if (monday != null) {
              Log.d("ffs", "Hurra! ${monday.name} har färgId ${monday.color}")
              dayTextView.text = monday.name
              dayTextView.setBackgroundColor(monday.color)
          }
      }

  }

*/




/*
        val db = FirebaseFirestore.getInstance()
        val newActions = mutableListOf<Actions>()
        val actionsRef = db.collection("Weekdays").document("Days")

        actionsRef.get().addOnSuccessListener { document ->
            if(document != null) {

                val image1 = document.getString("brushteeth")
               // val image2 = document.getString("clean")
               // val image3 = document.getString("dinner")
               // val image4 = document.getString("vacumclean")
                Glide.with(this).load(image1).into(imageView)

           }

        }

 */
