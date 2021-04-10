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


            //Step 1. Taking amount
                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                var newAmount = Integer.parseInt(snapshot.child("amount_amount").value.toString())
                                amount = Amount(mAuth.currentUser?.uid.toString(), newAmount.toString(), "0")

                                //Step 2. Adding note
                                var original_id = (newAmount + 1).toString()

                                var note = Note(title, text, current.toString(), "0", original_id)
                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child((newAmount + 1).toString()).setValue(note).addOnCompleteListener {

                                    if (it.isSuccessful) {
                                        Toast.makeText(activity.applicationContext, "Added note", Toast.LENGTH_SHORT).show()
                                        //Step 3.  Updating amount
                                        newAmount++


                                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_amount").setValue(newAmount).addOnCompleteListener {
                                            newAmount = 0
                                            //We clear locally amount and download new one at begin of process
                                            var count = activity.supportFragmentManager.backStackEntryCount

                                            for (i in 0..count)
                                            {
                                               activity.supportFragmentManager.popBackStack()
                                            }
                                            activity.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up,R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up).replace(R.id.fragment_container, NotesFragment()).commit()
                                        }


                                    } else {
                                        Toast.makeText(activity.applicationContext, "Error while sending data.\nPlease try again.", Toast.LENGTH_SHORT).show()
                                    }

                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }
                    })





        }else{
       Toast.makeText(activity.applicationContext, "Please fill data", Toast.LENGTH_SHORT).show()
        }
    }

    fun editNote(mainActivity: MainActivity, arrayList: ArrayList<Note> , position: Int, title: String, text: String) {

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

         mainActivity.fav_list.clear()
        var local_fav_notes=0


        //Step 1. Take amount of  fav notes

        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var str_local_fav_notes = snapshot.child("amount_fav").value.toString()
                    local_fav_notes = Integer.parseInt(str_local_fav_notes)

                    //Here we download evey item
                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in 1 until (local_fav_notes + 1) step 1) {
                                var local_title = snapshot.child(i.toString()).child("note_name").value.toString()
                                var local_date = snapshot.child(i.toString()).child("note_date").value.toString()
                                var local_text = snapshot.child(i.toString()).child("note_text").value.toString()
                                var local_fav = snapshot.child(i.toString()).child("note_is_fav").value.toString()
                                var local_original_id = snapshot.child(i.toString()).child("note_original_id").value.toString()

                                var local_object = Note(local_title, local_text, local_date, local_fav, local_original_id)
                                mainActivity.fav_list.add(local_object)
                            }


                            local_fav_notes = 0


                            if(arrayList[position].note_is_fav.equals("1")){

                                val current = Calendar.getInstance().time

                                println("Fav size: ${mainActivity.fav_list.size}")


                                for(y in 1..mainActivity.fav_list.size){

                                    if(arrayList[position].note_date ==   mainActivity.fav_list[y-1].note_date && arrayList[position].note_text == mainActivity.fav_list[y-1].note_text && arrayList[position].note_name == mainActivity.fav_list[y-1].note_name){
                                        mainActivity.fav_list[y-1].note_text = text
                                        mainActivity.fav_list[y-1].note_name = title
                                        mainActivity.fav_list[y-1].note_date = current.toString()
                                        mainActivity.fav_list[y-1].note_original_id = arrayList[position].note_original_id

                                        database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).removeValue().addOnCompleteListener {
                                            if(it.isSuccessful){
                                                for(yy in 1..mainActivity.fav_list.size){
                                                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(yy.toString()).setValue(mainActivity.fav_list[yy-1])
                                                }



                                                //We overwrite local file right now and later we will file on database
                                                mainActivity.note_list[position].note_name=title
                                                mainActivity.note_list[position].note_date=current.toString()
                                                mainActivity.note_list[position].note_text=text


                                                //Update on Firebase

                                                var editedNote = Note( arrayList[position].note_name, arrayList[position].note_text,  arrayList[position].note_date,  arrayList[position].note_is_fav, arrayList[position].note_original_id)
                                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position+1).toString()).setValue(editedNote).addOnCompleteListener {
                                                    if(it.isSuccessful){
                                                        println("We updated note!")
                                                        arrayList.clear()

                                                        var count = mainActivity.supportFragmentManager.backStackEntryCount

                                                        for (i in 0..count)
                                                        {
                                                            mainActivity.supportFragmentManager.popBackStack()
                                                        }


                                                        mainActivity.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up,R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up).add(R.id.fragment_container, NotesFragment()).commit()

                                                    }else{
                                                        println("We didnt updated note!")
                                                    }
                                                   }
                                                   }
                                        }
                                    }
                                }


                            }else{

                                //We overwrite local file right now and later we will file on database
                                val current = Calendar.getInstance().time
                                arrayList[position].note_name=title
                                arrayList[position].note_date=current.toString()
                                arrayList[position].note_text=text


                                //Update on Firebase

                                var editedNote = Note( arrayList[position].note_name, arrayList[position].note_text,  arrayList[position].note_date,  arrayList[position].note_is_fav, arrayList[position].note_original_id)
                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position+1).toString()).setValue(editedNote).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        println("We updated note!")
                                        arrayList.clear()
                                    }else{
                                        println("We didnt updated note!")
                                    }

                                    var count = mainActivity.supportFragmentManager.backStackEntryCount

                                    for (i in 0..count)
                                    {
                                        mainActivity.supportFragmentManager.popBackStack()
                                    }


                                    mainActivity.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up,R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up).add(R.id.fragment_container, NotesFragment()).commit()
                                }
                            }





                        }

                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }
                    })
                } else {
                    mainActivity.fav_list.clear()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })




    }
}