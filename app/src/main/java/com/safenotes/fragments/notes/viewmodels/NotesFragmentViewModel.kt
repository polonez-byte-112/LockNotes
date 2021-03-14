package com.safenotes.fragments.notes.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safenotes.models.Note

class NotesFragmentViewModel: ViewModel(){
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var local_amount_notes=0
     var note_list:ArrayList<Note> =  ArrayList()


    fun downloadNote(){
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        note_list.clear()


        //Step 1. Take int a (for amount)
        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    local_amount_notes = Integer.parseInt(snapshot.child("amount_amount").value.toString())

                    //Step 2. Take object with name i where i++
                    database.child("notes").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {

                                for (i in 1 until (local_amount_notes + 1) step 1) {
                                    var local_date: String = snapshot.child(i.toString()).child("note_date").value.toString()
                                    var local_title: String = snapshot.child(i.toString()).child("note_name").value.toString()
                                    var local_text: String = snapshot.child(i.toString()).child("note_text").value.toString()
                                    var local_note = Note(local_title, local_text, local_date)

                                    println("\n\nId: $i\nDate: $local_date,\nTitle: $local_title,\nText: $local_text\n")
                                    note_list.add(local_note)
                                }
                            }else{
                                note_list.clear()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

}