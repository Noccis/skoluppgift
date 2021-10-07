package com.example.helpwithpicturesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ToDoActivity : AppCompatActivity() {

    val diffrentActions = ActionsList()
    lateinit var addButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ActionsRecycleViewAdapter(this,diffrentActions.listOfActions)

        recyclerView.adapter = adapter

        addButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        addButton.setOnClickListener {
            // Add new instruction
        }





    }









}