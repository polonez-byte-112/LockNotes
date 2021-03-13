package com.safenotes.fragments.notes.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.notes.NotesFragment
import com.safenotes.models.Note
import java.util.*

class EditNoteFragmentViewModel: ViewModel(){
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth


    fun addNotes(activity :MainActivity, title: String, text: String){

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        val current = Calendar.getInstance().time

        if(title.isNotEmpty() && text.isNotEmpty()) {
    var note = Note(mAuth.currentUser?.uid.toString(), title, text, current.toString())
    database.child("notes").child(database.push().key.toString()).setValue(note).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(activity.applicationContext, "Added note", Toast.LENGTH_SHORT).show()
                activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
            } else {
                Toast.makeText(activity.applicationContext, "Error while sending data.\nPlease try again.", Toast.LENGTH_SHORT).show()
            }
        }
        }else{
       Toast.makeText(activity.applicationContext, "Please fill data", Toast.LENGTH_SHORT).show()
        }
    }



}