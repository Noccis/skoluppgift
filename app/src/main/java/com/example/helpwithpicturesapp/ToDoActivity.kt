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
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Array.get
import java.util.*

class ToDoActivity : AppCompatActivity() {

    lateinit var addButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    val action = mutableListOf<Actions>()
    lateinit var db: FirebaseFirestore
    lateinit var myAdapter: ActionsRecycleViewAdapter
    var decision = ""
    lateinit var dayTextView: TextView
    lateinit var lockButton: ImageView
    lateinit var passCard: CardView
    lateinit var editPassword: EditText
    private var longAnimationDuration: Int = 2000
    lateinit var lock: Button
    lateinit var unlock: Button
    var pinkod = ""
    lateinit var close: ImageView
    lateinit var rewardImageView: ImageView
    private var shortAnimationDuration: Int = 400
    lateinit var deletedCard: Actions
    lateinit var templateSave: TextView
    var uid = ""
    var actionId = " "
    val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser // Henrik ny härifrån
        if (currentUser != null) {
            uid = currentUser.uid
            Log.d("!!!!", "onCreate: ToDoActivity userId $uid")
        }

        templateSave = findViewById(R.id.saveTemplateText)
        templateSave.visibility = View.GONE
// Lägg till templateSave.GONE sen när koden är klar.
// Här är spara mall koden
        templateSave.setOnClickListener {

            var dialog = TemplateDialogFragment(this)
            dialog.show(supportFragmentManager, "templateDialog")

        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        pinkod = intent.getStringExtra(Constants.PINKOD).toString()
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

        addButton = findViewById(R.id.floatingActionButton)
        addButton.visibility = View.GONE
        addButton.setOnClickListener {
            val intent = Intent(this, UserCreateAndEditActivity::class.java)
            intent.putExtra(Constants.DAY_CHOSEN, decision)
            startActivity(intent)
        }


        rewardImageView = findViewById(R.id.rewardImageView)
        rewardImageView.visibility = View.GONE

        dayTextView = findViewById(R.id.dayTextView)
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()


        recyclerView = findViewById(R.id.recyclerView)
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

            if (addButton.visibility == View.VISIBLE) {
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition
                Collections.swap(action, startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
                return true
            } else return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            if (addButton.visibility == View.VISIBLE) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deletedCard = action.get(position)
                        action.removeAt(position)
                        myAdapter.notifyItemRemoved(position)
                        Snackbar.make(
                            recyclerView,
                            "Uppgiften är borttagen",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Ångra", View.OnClickListener {
                                action.add(position, deletedCard)
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
            //.orderBy("order", Query.Direction.ASCENDING)
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
        passCard.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(longAnimationDuration.toLong()).setListener(null)
        }

        unlock.setOnClickListener {
            val pass = editPassword.text.toString()
            if (pinkod == pass) {
                passCard.visibility = View.GONE
                addButton.visibility = View.VISIBLE
                editPassword.setText("")
                templateSave.visibility = View.VISIBLE

            } else {
                Toast.makeText(this, "Skriv rätt lösenord! ", Toast.LENGTH_SHORT).show()
            }
        }

        lock.setOnClickListener {
            val pass = editPassword.text.toString()
            if (pinkod == pass) {
                passCard.visibility = View.GONE
                addButton.visibility = View.GONE
                editPassword.setText("")
                templateSave.visibility = View.GONE
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
                            .document(decision).collection("action").document(actionId).collection("steps").get()
                          //  .orderBy("order", Query.Direction.ASCENDING).get()
                            .addOnSuccessListener {documents ->

                            //    Log.d("ffs", "step succes dag: ${decision} actionid: $actionId document size ${documents.documents.size}")

                                val stepList = mutableListOf<Actions>()     // temporär lista för att ladda ner och upp steps
                                for (document in documents.documents) {

                                    val newStep = document.toObject(Actions::class.java)

                                    if (newStep != null){
                                        stepList.add(newStep)
                                    }


                                //   Log.d("ffs", "A step was added! Tjoho! ${newStep!!.documentName}")


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


}







