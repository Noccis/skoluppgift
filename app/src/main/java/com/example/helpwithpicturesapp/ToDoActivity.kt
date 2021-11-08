package com.example.helpwithpicturesapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class ToDoActivity : AppCompatActivity() {

    lateinit var addButton: ImageView
    lateinit var homeButton : ImageView
    lateinit var saveTemplate: ImageView
    lateinit var close: ImageView
    lateinit var logoutButton : ImageView
    lateinit var refreshButton: ImageView
    lateinit var rewardImageView: ImageView
    lateinit var lockButton: ImageView
    lateinit var instructionButton : ImageView
    lateinit var backButton : ImageView
    lateinit var dayTextView: TextView
    lateinit var editPassword: EditText
    lateinit var lock: Button
    lateinit var unlock: Button
    lateinit var passCard: CardView
    lateinit var menuCard : CardView
    lateinit var recyclerView: RecyclerView
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: ActionsRecycleViewAdapter
    lateinit var deletedCard: Actions
    var pinkod = ""
    var decision = ""
    var shortAnimationDuration: Int = 400
    val action = mutableListOf<Actions>()
    var uid = ""
    var actionId = " "
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

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
        rewardImageView = findViewById(R.id.rewardImageView)
        recyclerView = findViewById(R.id.recyclerView)
        dayTextView = findViewById(R.id.dayTextView)
        backButton= findViewById(R.id.backButton)
        instructionButton = findViewById(R.id.instructionButton)

        menuCard.visibility = View.GONE
        passCard.visibility = View.GONE
        rewardImageView.visibility = View.GONE

        pinkod = intent.getStringExtra(Constants.PINKOD).toString()
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            uid = currentUser.uid
            Log.d("!!!!", "onCreate: ToDoActivity userId $uid")
        }

        backButton.setOnClickListener {
            val intent = Intent(this, WeekdaysActivity::class.java)
            startActivity(intent)
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
            var dialog = TemplateDialogFragment(this)
            dialog.show(supportFragmentManager, "templateDialog")
        }

        addButton.setOnClickListener {
            val intent = Intent(this, UserCreateAndEditActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, WeekdaysActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        myAdapter = ActionsRecycleViewAdapter(this, action, decision, pinkod)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        EventChangeListener()

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
                Collections.swap(action, startPosition, endPosition)  // Byter plats i listan
                recyclerView.adapter?.notifyItemMoved( // säger till adapter att vi gjort förändring i position.
                    startPosition,
                    endPosition
                )
                setNewOrder()

                return true
            } else return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            if (menuCard.visibility == View.VISIBLE) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {

                        deletedCard = action[position]
                        val docId = action[position].documentName
                        if (docId != null) {
                            db.collection("users").document(uid).collection("weekday")
                                .document(decision).collection("action")
                                .document(docId)
                                .delete()
                            action.removeAt(position)
                            myAdapter.notifyDataSetChanged()
                        }
                        Snackbar.make(recyclerView, "Uppgiften är borttagen", Snackbar.LENGTH_LONG).show()
                    }
                }
            } else {
                val position = null

                myAdapter.notifyDataSetChanged()
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
        db.collection("users").document(uid).collection("weekday")
            .document(decision).collection("action")
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

    override fun onResume() {
        Log.d("TAG", "ON RESUME KÖRS")
        setNewOrder()
        super.onResume()
    }

    fun setNewOrder() {
        Log.d("ffs", "SETNEWORDER KÖRS")
        var newOrder: Long = 1

        for (step in action) {
            Log.d("TAG", "setNewOrder, ny action i listan:${step.documentName.toString()} order ${step.order}")
        }

        for (step in action) {
            step.order = newOrder
            actionId = step.documentName.toString()
            val db = Firebase.firestore
            db.collection("users").document(uid).collection("weekday")
                .document(decision).collection("action").document(actionId).set(step)
                .addOnSuccessListener {
                    Log.d("TAG","setNewOrder:${step.documentName.toString()} added to db order ${step.order}")
                }
                .addOnFailureListener {
                    Log.d("TAG", "setNewOrderDelete: action add failure")
                }
            newOrder++
        }
    }

    fun reward() {
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

    private fun hideReward() {
        rewardImageView.visibility = View.GONE
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

    fun saveTemplate(name: String) {
        uid = auth.currentUser!!.uid
        for (action in action) {
            if (action != null) {               // Ta bort detta?

                actionId = action.documentName.toString()

                db.collection("users").document(uid).collection("weekday")
                    .document(name).collection("action").document(actionId)
                    .set(action)                    // Laddar upp lokala actions i listan till users egen mall.
                    .addOnSuccessListener {
                        //    Log.d("ffs", "saveTemplate MAINaction fun actionId: $actionId  ${action.documentName} added ")

                        actionId = action.documentName.toString()

                        db.collection("users").document(uid).collection("weekday")
                            .document(decision).collection("action").document(actionId)
                            .collection("steps")
                            .orderBy("order", Query.Direction.ASCENDING).get()
                            .addOnSuccessListener { documents ->
                                //    Log.d("ffs", "step succes dag: ${decision} actionid: $actionId document size ${documents.documents.size}")
                                val stepList =
                                    mutableListOf<Actions>()     // temporär lista för att ladda ner och upp steps
                                for (document in documents.documents) {

                                    val newStep = document.toObject(Actions::class.java)

                                    if (newStep != null) {
                                        stepList.add(newStep)
                                    }
                                }
                                for (step in stepList) {

                                    db.collection("users").document(uid).collection("weekday")
                                        .document(name).collection("action").document(actionId)
                                        .collection("steps")
                                        .add(step)                    // Laddar upp lokala actions i listan till users egen mall.
                                        .addOnSuccessListener {
                                            //     Log.d("ffs", "saveSTEP fun ${step.documentName} step added in $uid Mallnamn:$name, action ID: $actionId")
                                        }
                                        .addOnFailureListener {
                                            //      Log.d("ffs", "$it add step funkar inte")
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Log.d("ffs", "$actionId fail $it")
                            }
                    }
            }
        }
    }

    fun refresh() {
        action.clear();
        myAdapter.notifyDataSetChanged();
        EventChangeListener()
        }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}