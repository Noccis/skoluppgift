package com.example.helpwithpicturesapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.google.firebase.firestore.QuerySnapshot

import com.google.android.gms.tasks.Task

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks.await
import java.util.ArrayList





class BrowseTemplateActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var myAdapter: BrowseAdapter
    lateinit var cancelButton: Button
    lateinit var db: FirebaseFirestore
    val templateList = mutableListOf<String>()
    var uid = ""
    var pinkod = ""
    var decision = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_template)

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            uid = currentUser.uid
            Log.d("!!!!", "onCreate: ToDoActivity userId $uid")
        }

        pinkod = intent.getStringExtra(Constants.PINKOD).toString()
        decision = intent.getStringExtra(Constants.DAY_CHOSEN).toString()

        cancelButton = findViewById(R.id.cancelButton)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        myAdapter = BrowseAdapter(this, templateList, pinkod, decision)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = myAdapter

        cancelButton.setOnClickListener {
            finish()
        }

        createList()

    }

    fun createList() {
        Log.d("!!!", "function is working")
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).collection("weekday")
            .get()
            .addOnSuccessListener { result ->
                Log.d("!!"," Size "+result.size())
                for (document in result) {
                    Log.d("!!", "${document.id} => ${document.data}")
                    templateList.add(document.id)
                }
                myAdapter.notifyDataSetChanged()
            }
                .addOnFailureListener { exception ->
                    Log.w("UserDownload", "Error getting documents.", exception)
                }

            }
    }

