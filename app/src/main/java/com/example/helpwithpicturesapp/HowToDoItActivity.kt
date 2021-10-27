package com.example.helpwithpicturesapp

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*

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
    lateinit var lockButton : ImageView
    lateinit var passCard : CardView
    lateinit var editPassword : EditText
    private var longtAnimationDuration: Int = 2000
    lateinit var lock : Button
    lateinit var unlock : Button
    var password = ""
    lateinit var close : ImageView
    lateinit var addButton2 : FloatingActionButton
    lateinit var deletedCard : ActionSteps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_do_it)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

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
        recyclerView = findViewById(R.id.howToDoRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myAdapter = HowToDoItRecycleViewAdapter(this, actionStep)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        actionId = intent.getStringExtra(ACTION_LOCATION).toString()
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()


        addButton2 = findViewById(R.id.floatingActionButton2)
        addButton2.visibility = View.GONE
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


        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {

            if (addButton2.visibility == View.VISIBLE) {
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition
                Collections.swap(actionStep, startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
                return true
            } else return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


            if (addButton2.visibility == View.VISIBLE) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deletedCard = actionStep.get(position)
                        actionStep.removeAt(position)
                        myAdapter.notifyItemRemoved(position)
                        Snackbar.make(
                            recyclerView,
                            "Uppgiften är borttagen",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Ångra", View.OnClickListener {
                                actionStep.add(position, deletedCard)
                                myAdapter.notifyItemInserted(position)
                            }).show()
                    }

                }

            } else {
                val position = null
                myAdapter.notifyDataSetChanged()
            }

        }

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
                addButton2.visibility = View.VISIBLE
                editPassword.setText("")
            } else {
                Toast.makeText(this, "Fel Lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }

        lock.setOnClickListener {
            val pass = editPassword.text.toString()
            if ( password == pass) {
                passCard.visibility = View.GONE
                addButton2.visibility = View.GONE
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
