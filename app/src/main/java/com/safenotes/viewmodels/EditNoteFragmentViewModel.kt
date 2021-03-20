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

            var note = Note(title, text, current.toString(),"0")

            //Step 1. Taking amount
                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                            var    newAmount = Integer.parseInt(snapshot.child("amount_amount").value.toString())
                                amount = Amount(mAuth.currentUser?.uid.toString(), newAmount.toString(),"0")

                                //Step 2. Adding note

                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child((newAmount+1).toString()).setValue(note).addOnCompleteListener {

                                    if (it.isSuccessful) {
                                        Toast.makeText(activity.applicationContext, "Added note", Toast.LENGTH_SHORT).show()
                                        //Step 3.  Updating amount
                                        newAmount++


                                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_amount").setValue(newAmount).addOnCompleteListener {
                                            newAmount=0
                                            //We clear locally amount and download new one at begin of process
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

    fun editNote(mainActivity: MainActivity, arrayList: ArrayList<Note> , position: Int, title: String, text: String) {

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        val current = Calendar.getInstance().time

        //We overwrite local file right now and later we will file on database


        arrayList[position].note_name=title
        arrayList[position].note_date=current.toString()
        arrayList[position].note_text=text

        //Update on Firebase

        var editedNote = Note( arrayList[position].note_name, arrayList[position].note_text,  arrayList[position].note_date,  arrayList[position].note_is_fav)
        database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position+1).toString()).setValue(editedNote).addOnCompleteListener {
            if(it.isSuccessful){
                println("We updated note!")
                arrayList.clear()
            }else{
                println("We didnt updated note!")
            }
            mainActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
        }







    }


}