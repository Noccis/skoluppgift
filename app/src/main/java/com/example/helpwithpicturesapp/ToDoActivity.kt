package com.example.helpwithpicturesapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    val TAG = "!!!"
    var decision = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)

        decision = intent.getIntExtra(Constants.DAY_CHOSEN, 0)

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


    }

    fun EventChangeListener() {



        if (decision == 1) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("monday").collection("monday")
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

        } else if (decision == 2) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("tuesday").collection("tuesday")
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

        } else  if (decision == 3) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("wednesday").collection("wednesday")
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

        } else if (decision == 4) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("thursday").collection("thursday")
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

        } else  if (decision == 5) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("friday").collection("friday")
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

        } else if (decision == 6) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("saturday").collection("saturday")
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

        } else  if (decision == 7) {

            db = FirebaseFirestore.getInstance()
            db.collection("Weekday").document("sunday").collection("sunday")
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







