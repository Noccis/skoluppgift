package com.example.helpwithpicturesapp

import android.app.Notification
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: ActionsRecycleViewAdapter
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




        addButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            val intent = Intent(this, UserCreateAndEditActivity::class.java)
            startActivity(intent)
        }
        getUserUploadPicture()

    }

    fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("Actions").orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.d("Firestore error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            action.add(dc.document.toObject(Actions::class.java))
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            })

    }

    fun getUserUploadPicture() {

        val actionsRef = db.collection("Actions")

        val userAction1 = Actions(
            "UserAction",
            "https://firebasestorage.googleapis.com/v0/b/helpwithpicturesapp-f9c12.appspot.com/o/UploadedPictures%2F2021_10_15_12_45_45?alt=media&token=848fb693-32fa-4632-9f93-a04cef2d50e9",
            false,
            "Soffa"
        )
        db.collection("Actions").document("UploadedPictures").set(userAction1)

            actionsRef.get().addOnSuccessListener { doucmentSnapShot -> // doucumentSnapShot == ger Listener ett namn
                for (document in doucmentSnapShot.documents) { // Går igenom ett dokument i taget

                    val newUserAction =
                        document.toObject(Actions::class.java) // Addar nytt item till ItemFolder i Firebase

                    if (newUserAction != null) {
                        action.add(newUserAction)
                    }
                }

            }

    }
}

