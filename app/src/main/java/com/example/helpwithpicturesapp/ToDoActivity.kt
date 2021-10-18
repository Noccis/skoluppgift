package com.example.helpwithpicturesapp


import android.app.Notification
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    var decision = ""
    lateinit var dayTextView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        dayTextView = findViewById(R.id.dayTextView)
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()


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
       // getUserUploadPicture()

    }
        fun EventChangeListener() {

            when (decision) {
                "monday" -> {
                    dayTextView.text = "Måndag"
                    dayTextView.setBackgroundColor(Color.parseColor("#92d051"));
                }
                "tuesday" -> {
                    dayTextView.text = "Tisdag"
                    dayTextView.setBackgroundColor(Color.parseColor("#92cddd"));
                }
                "wednesday" -> {
                    dayTextView.text = "Onsdag"
                    dayTextView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                }
                "thursday" -> {
                    dayTextView.text = "Torsdag"
                    dayTextView.setBackgroundColor(Color.parseColor("#c88f58"));
                }
                "friday" -> {
                    dayTextView.text = "Fredag"
                    dayTextView.setBackgroundColor(Color.parseColor("#ffff6d"));
                }
                "saturday" -> {
                    dayTextView.text = "Lördag"
                    dayTextView.setBackgroundColor(Color.parseColor("#faa8d8"));
                }
                "sunday" -> {
                    dayTextView.text = "Söndag"
                    dayTextView.setBackgroundColor(Color.parseColor("#ff4342"));
                }
            }
                db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document(decision).collection(decision)
                .orderBy("order", Query.Direction.ASCENDING)
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


}







