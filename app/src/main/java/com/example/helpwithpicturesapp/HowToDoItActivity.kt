package com.example.helpwithpicturesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import java.text.FieldPosition

const val ACTIONS_POSITION_KEY = "ACTION_KEY"
const val INSTRUCTIONS_POSITION_KEY = "INSTRUCTION_KEY"
const val POSITION_NOT_SET = -1


class HowToDoItActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var textView: TextView

    val diffrentInstructions = InstructionList()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_do_it)

        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.userSeeInstructions_Tv)

    val actionsPosition = intent.getIntExtra(ACTIONS_POSITION_KEY, POSITION_NOT_SET)

        if(actionsPosition != POSITION_NOT_SET) {
            displayInstruction(actionsPosition)
        }



    }



    fun displayInstruction(position: Int) {
       val i = diffrentInstructions.listOfInstructions[position]
        imageView.setImageResource(i.image)
        textView.setText(i.userInstructions)


    }





}