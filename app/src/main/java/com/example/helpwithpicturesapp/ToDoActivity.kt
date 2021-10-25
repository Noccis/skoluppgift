package com.example.helpwithpicturesapp


import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

import android.view.View
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ToDoActivity : AppCompatActivity() {

    lateinit var addButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    val action = mutableListOf<Actions>()
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: ActionsRecycleViewAdapter
    var decision = ""
    lateinit var dayTextView : TextView
    lateinit var lockButton : ImageView
    lateinit var passCard : CardView
    lateinit var editPassword : EditText
    private var longtAnimationDuration: Int = 2000
    lateinit var lock : Button
    lateinit var unlock : Button
    var password = ""
    lateinit var close : ImageView
    lateinit var rewardImageView: ImageView
    private var shortAnimationDuration: Int = 400
    lateinit var deletedCard : Actions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        password = intent.getStringExtra(Constants.PASSWORD).toString()

        editPassword = findViewById(R.id.editPassword)
        passCard = findViewById(R.id.passCard)
        passCard.visibility = View.GONE
        close = findViewById(R.id.close)

        lock = findViewById(R.id.lock)
        unlock = findViewById(R.id.unlock)

        lockButton = findViewById(R.id.lockButton)
        lockButton.setOnClickListener {
            lockEditing()

        }
        rewardImageView = findViewById(R.id.rewardImageView)
        rewardImageView.visibility = View.GONE

        dayTextView = findViewById(R.id.dayTextView)
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()


        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = ActionsRecycleViewAdapter(this, action , decision)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter
        EventChangeListener()

        addButton = findViewById(R.id.floatingActionButton)
        addButton.visibility = View.GONE
        addButton.setOnClickListener {
            val intent = Intent(this, UserCreateAndEditActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            startActivity(intent)
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

        var simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.LEFT. or (ItemTouchHelper.RIGHT)){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition
                Collections.swap(action, startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition,endPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.adapterPosition
                when (direction){
                    ItemTouchHelper.LEFT -> {
                        deletedCard= action.get(position)
                        action.removeAt(position)
                        myAdapter.notifyItemRemoved(position)

                        Snackbar.make(recyclerView, "Uppgiften är borttagen", Snackbar.LENGTH_LONG)
                            .setAction("Ångra", View.OnClickListener {
                            action.add(position, deletedCard)
                            myAdapter.notifyItemInserted(position)
                        }).show()
                        }

                    ItemTouchHelper.RIGHT -> {
                        deletedCard= action.get(position)
                        action.removeAt(position)
                        myAdapter.notifyItemRemoved(position)

                        Snackbar.make(recyclerView, "Uppgiften är borttagen", Snackbar.LENGTH_LONG)
                            .setAction("Ångra", View.OnClickListener {
                                action.add(position, deletedCard)
                                myAdapter.notifyItemInserted(position)
                            }).show()
                    }

                }

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


    fun lockEditing(){
        passCard.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(longtAnimationDuration.toLong()).setListener(null)
        }

        unlock.setOnClickListener {
            val pass = editPassword.text.toString()
            if ( password == pass) {
                passCard.visibility = View.GONE
                addButton.visibility = View.VISIBLE
                editPassword.setText("")
            } else {
                Toast.makeText(this, "Fel Lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }

        lock.setOnClickListener {
            val pass = editPassword.text.toString()
            if ( password == pass) {
                passCard.visibility = View.GONE
                addButton.visibility = View.GONE
                editPassword.setText("")
            } else {
                Toast.makeText(this, "Fel Lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }
        close.setOnClickListener {
            passCard.visibility = View.GONE
        }
    }
}







