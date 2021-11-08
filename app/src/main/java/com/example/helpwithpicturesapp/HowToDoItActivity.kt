package com.example.helpwithpicturesapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

const val ACTIONS_POSITION_KEY = "ACTION_KEY"
const val INSTRUCTIONS_POSITION_KEY = "INSTRUCTION_KEY"
const val POSITION_NOT_SET = -1
const val ACTION_LOCATION = "ACTION_LOCATION"

class HowToDoItActivity : AppCompatActivity() {

    lateinit var homeButton : ImageView
    lateinit var saveTemplate: ImageView
    lateinit var logoutButton : ImageView
    lateinit var refreshButton: ImageView
    lateinit var lockButton: ImageView
    lateinit var addButton: ImageView
    lateinit var close: ImageView
    lateinit var instructionButton : ImageView
    lateinit var backButton : ImageView
    lateinit var lock: Button
    lateinit var unlock: Button
    lateinit var menuCard : CardView
    lateinit var passCard: CardView
    lateinit var editPassword: EditText
    lateinit var emptyPage : TextView
    lateinit var deletedCard: Actions
    lateinit var recyclerView: RecyclerView
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: HowToDoItRecycleViewAdapter
    val actionStep = mutableListOf<Actions>()
    val auth = Firebase.auth
    var actionId = ""
    var decision = ""
    var pinkod = ""
    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_do_it)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        logoutButton = findViewById(R.id.logoutButton)
        refreshButton = findViewById(R.id.refreshButton)
        homeButton = findViewById(R.id.homeButton)
        menuCard = findViewById(R.id.menuCard)
        saveTemplate = findViewById(R.id.saveTemplate)
        editPassword = findViewById(R.id.editPassword)
        passCard = findViewById(R.id.passCard)
        close = findViewById(R.id.close)
        lock = findViewById(R.id.lock)
        unlock = findViewById(R.id.unlock)
        lockButton = findViewById(R.id.lockButton)
        addButton = findViewById(R.id.addButton)
        recyclerView = findViewById(R.id.howToDoRecycleView)
        backButton= findViewById(R.id.backButton)
        instructionButton = findViewById(R.id.instructionButton)
        emptyPage = findViewById(R.id.emptyPage)


        menuCard.visibility = View.GONE
        passCard.visibility = View.GONE
        emptyPage.visibility = View.GONE

        pinkod = intent.getStringExtra(Constants.PINKOD).toString()
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()
        actionId = intent.getStringExtra(ACTION_LOCATION).toString()


        val currentUser: FirebaseUser? =
            FirebaseAuth.getInstance().currentUser // Henrik ny härifrån
        if (currentUser != null) {
            uid = currentUser.uid
            Log.d("!!!!", "onCreate: ToDoActivity userId $uid")
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        myAdapter = HowToDoItRecycleViewAdapter(this, actionStep)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        backButton.setOnClickListener {
            finish()
        }

        instructionButton.setOnClickListener {
            // val intent = Intent(this, InstructionsActivity::class.java)
            //  startActivity(intent)
        }
        lockButton.setOnClickListener {
            lockEditing()
        }

        refreshButton.setOnClickListener {
            refresh()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        saveTemplate.setOnClickListener {
        }

        addButton.setOnClickListener {
            val intent = Intent(this, CreateAndEditActionSteps::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            intent.putExtra(INSTRUCTIONS_POSITION_KEY, actionId)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, WeekdaysActivity::class.java)
            startActivity(intent)
            finish()
        }

        eventChangeListener()

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT
    ) {

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            if (menuCard.visibility == View.VISIBLE) {
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition
                Collections.swap(actionStep, startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
                setNewOrder()
                return true
            } else return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (menuCard.visibility == View.VISIBLE) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deletedCard = actionStep[position]
                        val docId = actionStep[position].documentName
                        if (docId != null) {
                            db.collection("users").document(uid).collection("weekday")
                                .document(decision).collection("action").document(actionId)
                                .collection("steps").document(docId)
                                .delete()
                            actionStep.removeAt(position)
                            myAdapter.notifyDataSetChanged()
                        }

                        Snackbar.make(recyclerView, "Uppgiften är borttagen", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                val position = null
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    fun eventChangeListener() {


        var uid = auth.currentUser!!.uid

        db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).collection("weekday")
            .document(decision).collection("action").document(actionId)
            .collection("steps")
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
                            actionStep.add(dc.document.toObject(Actions::class.java))
                        }
                    }

                    myAdapter.notifyDataSetChanged()

                    if (actionStep.size == 0) {
                        val stepRef = db.collection("users").document(uid).collection("weekday")
                            .document(decision).collection("action").document(actionId)

                        stepRef.update("steps", false)
                        emptyPage.visibility = View.VISIBLE
                    } else  emptyPage.visibility = View.GONE
                }
            })

    }


    override fun onResume() {
        Log.d("TAG", "ON RESUME KÖRS")
        setNewOrder()
        super.onResume()
    }
    
    fun setNewOrder () {
        Log.d("ffs", "setNewOrder körs")
        var newOrder:Long = 1

/*
        for (step in action){
            Log.d("TAG", "setNewOrder:${step.documentName.toString()} order ${step.order}")
        }

 */
            for (step in actionStep) {
                step.order = newOrder
                val stepId = step.documentName.toString()
                val db = Firebase.firestore
                db.collection("users").document(uid).collection("weekday")
                    .document(decision).collection("action").document(actionId).collection("steps")
                    .document(stepId).set(step)
                    .addOnSuccessListener {
                        Log.d(
                            "TAG",
                            "setNewOrder:${step.documentName.toString()} added to db order ${step.order}"
                        )
                    }
                    .addOnFailureListener {
                        Log.d("TAG", "setNewOrderDelete: action add failure")
                    }

                newOrder++
            }
        }


    fun lockEditing() {

        passCard.visibility = View.VISIBLE
        unlock.setOnClickListener {
            it.hideKeyboard()
            val pass = editPassword.text.toString()
            if (pinkod == pass) {
                passCard.visibility = View.GONE
                menuCard.visibility = View.VISIBLE
                editPassword.setText("")

            } else {
                Toast.makeText(this, "Skriv rätt lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }

        lock.setOnClickListener {
            it.hideKeyboard()
            val pass = editPassword.text.toString()
            if (pinkod == pass) {
                passCard.visibility = View.GONE
                menuCard.visibility = View.GONE
                editPassword.setText("")
            } else {
                Toast.makeText(this, "Skriv rätt lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }
        close.setOnClickListener {
            passCard.visibility = View.GONE
        }
    }
    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
    fun refresh() {
        actionStep.clear();
        myAdapter.notifyDataSetChanged();
        eventChangeListener()
    }

}

