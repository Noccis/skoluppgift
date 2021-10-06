package com.example.helpwithpicturesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ToDoActivity : AppCompatActivity() {

    val diffrentActions = ActionsList()
    lateinit var recyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ActionsRecycleViewAdapter(this,diffrentActions.listOfActions)

        recyclerView.adapter = adapter


    }









}