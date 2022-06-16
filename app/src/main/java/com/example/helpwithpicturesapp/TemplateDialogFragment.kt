package com.example.helpwithpicturesapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.helpwithpicturesapp.activities.DailyListOfActionsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TemplateDialogFragment(val activity: DailyListOfActionsActivity) : DialogFragment() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView : View = inflater.inflate(R.layout.template_fragment, container, false)
        var userTemplateInput = rootView.findViewById<EditText>(R.id.templateUserInput)
        var canselButton = rootView.findViewById<Button>(R.id.templateAbortButton)
        canselButton.setOnClickListener {
            Log.d("ffs", "Avbryt funkar")
            dismiss()
        }

        var saveButton = rootView.findViewById<Button>(R.id.templateSaveButton)
        saveButton.setOnClickListener {
            var userInput = userTemplateInput.text.toString()
            activity.saveTemplate(userInput)

            Log.d("ffs", "Spara funkar")
            // spara mall
            dismiss()
        }
        return rootView
    }
}
