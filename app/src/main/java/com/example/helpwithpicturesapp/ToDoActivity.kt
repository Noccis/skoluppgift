package com.example.helpwithpicturesapp


import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

import android.view.View
import android.widget.ImageView

import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    lateinit var rewardImageView: ImageView
    private var shortAnimationDuration: Int = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        rewardImageView = findViewById(R.id.rewardImageView)
        rewardImageView.visibility = View.GONE

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
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            startActivity(intent)
        }



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

    fun reward () {
        fadeIn()
        rewardSound()
        Handler(Looper.getMainLooper()).postDelayed({
            hideReward()
        }, 1000)
    }
    private fun fadeIn() {
        rewardImageView.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
    }

    private fun rewardSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.ra)
        mediaPlayer.start()
    }
    private fun hideReward () {
        rewardImageView.visibility = View.GONE
    }

}







