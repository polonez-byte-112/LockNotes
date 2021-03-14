package com.safenotes.fragments.notes.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.notes.NotesFragment
import com.safenotes.models.Amount
import com.safenotes.models.Note
import java.util.*

class EditNoteFragmentViewModel: ViewModel(){
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    lateinit var amount: Amount
    fun addNotes(activity :MainActivity, title: String, text: String){

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        val current = Calendar.getInstance().time

        if(title.isNotEmpty() && text.isNotEmpty()) {

            var note = Note(title, text, current.toString())

            //Step 1. Taking amount
                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                            var    newAmount = Integer.parseInt(snapshot.child("amount_amount").value.toString())
                                amount = Amount(mAuth.currentUser?.uid.toString(), newAmount.toString())

                                //Step 2. Adding note

                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child((newAmount+1).toString()).setValue(note).addOnCompleteListener {

                                    if (it.isSuccessful) {
                                        Toast.makeText(activity.applicationContext, "Added note", Toast.LENGTH_SHORT).show()
                                        //Step 3.  Updating amount
                                        newAmount++


                                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_amount").setValue(newAmount).addOnCompleteListener {
                                            activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
                                        }


                                    } else {
                                        Toast.makeText(activity.applicationContext, "Error while sending data.\nPlease try again.", Toast.LENGTH_SHORT).show()
                                    }

                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })





        }else{
       Toast.makeText(activity.applicationContext, "Please fill data", Toast.LENGTH_SHORT).show()
        }
    }



}