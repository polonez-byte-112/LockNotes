package com.safenotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.models.Note
import kotlinx.android.synthetic.main.note_recycler_row.view.*

class NotesFragmentAdapter(var list: ArrayList<Note>): RecyclerView.Adapter<NotesFragmentAdapter.MyViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var local_amount_notes=0


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val local_title = itemView.note_recycler_row_title
        val local_date = itemView.note_recycler_row_date
        val local_text = itemView.note_recycler_row_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_row, parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.local_title.text = list[position].note_name
        holder.local_date.text=list[position].note_date
        holder.local_text.text=list[position].note_text


        //Here will be delete options etc
    }
   override fun getItemCount(): Int {
      return list.size
  }





    fun downloadNote(activity: MainActivity){
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        activity.note_list.clear()


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
                                    activity.note_list.add(local_note)
                                }
                                notifyDataSetChanged()
                            }else{
                                activity.note_list.clear()
                                notifyDataSetChanged()
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
