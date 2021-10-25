package com.example.helpwithpicturesapp

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*

const val ACTIONS_POSITION_KEY = "ACTION_KEY"
const val INSTRUCTIONS_POSITION_KEY = "INSTRUCTION_KEY"
const val POSITION_NOT_SET = -1
const val ACTION_LOCATION = "ACTION_LOCATION"


class HowToDoItActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    val actionStep = mutableListOf<ActionSteps>()
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: HowToDoItRecycleViewAdapter
    var actionId = ""
    var decision = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_do_it)

        recyclerView = findViewById(R.id.howToDoRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = HowToDoItRecycleViewAdapter(this, actionStep)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        //actionId = intent.getStringExtra("ActionChosen").toString()
        actionId = intent.getStringExtra(ACTION_LOCATION).toString()
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()

        val addButton2 = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        addButton2.setOnClickListener {
            val intent = Intent(this, CreateAndEditActionSteps::class.java)
            intent.putExtra(INSTRUCTIONS_POSITION_KEY, actionId)
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            startActivity(intent)
        }

        val previousButton = findViewById<Button>(R.id.previous_Button)
        previousButton.setOnClickListener {
            finish()
        }



        eventChangeListener()

    }

    fun eventChangeListener (){
        db = FirebaseFirestore.getInstance()
        db.collection("Weekday").document(decision).collection(decision).document(actionId).collection(actionId)
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
                            actionStep.add(dc.document.toObject(ActionSteps::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
            })
    }
}
