package com.example.helpwithpicturesapp

import android.app.Notification
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
import com.google.firebase.firestore.*

class ToDoActivity : AppCompatActivity() {

    lateinit var addButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    val action = mutableListOf<Actions>()
    lateinit var db : FirebaseFirestore
    lateinit var myAdapter : ActionsRecycleViewAdapter
    val TAG = "!!!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = ActionsRecycleViewAdapter(this, action)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        EventChangeListener()

    }

        fun EventChangeListener() {

            db = FirebaseFirestore.getInstance()
            db.collection("Actions").orderBy("order", Query.Direction.ASCENDING).
                    addSnapshotListener(object : EventListener<QuerySnapshot>{

                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ) {
                            if (error != null) {
                                Log.d("Firestore error", error.message.toString())
                                return
                            }

                            for ( dc: DocumentChange in value?.documentChanges!!){
                                if (dc.type == DocumentChange.Type.ADDED){
                                    action.add(dc.document.toObject(Actions::class.java))
                                }
                            }
                           myAdapter.notifyDataSetChanged()
                        }
                    })

        }        }
