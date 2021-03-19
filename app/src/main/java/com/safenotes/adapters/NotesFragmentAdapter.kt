package com.safenotes.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.notes.EditNoteFragment
import com.safenotes.models.Note
import kotlinx.android.synthetic.main.note_recycler_row.view.*

class NotesFragmentAdapter(var list: ArrayList<Note>,val activity: MainActivity): RecyclerView.Adapter<NotesFragmentAdapter.MyViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var local_amount_notes=0


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var local_title = itemView.note_recycler_row_title
        var local_date = itemView.note_recycler_row_date
        var local_text = itemView.note_recycler_row_text


        var local_settings_box = itemView.note_recycler_view_settings_box
        var local_settings_btn = itemView.note_recycler_row_settings
        var local_settings_delete =itemView.note_recycler_view_settings_delete
        var local_settings_edit = itemView.note_recycler_view_settings_edit
        var local_settings_cancel = itemView.note_recycler_view_settings_cancel

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

       var view = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_row, parent, false))
        return view
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.local_title.text = list[position].note_name
        holder.local_date.text=list[position].note_date
        holder.local_text.text=list[position].note_text


        holder.local_settings_box.setBackgroundColor(Color.argb(200, 255, 255, 252))

        holder.local_settings_btn.setOnClickListener {
            holder.local_settings_box.visibility=View.VISIBLE

        }

        holder.local_settings_delete.setOnClickListener {
            deleteNote(holder,activity, position)
        }


        holder.local_settings_edit.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, EditNoteFragment(1, list, position))?.commit()
        }

        holder.local_settings_cancel.setOnClickListener {
            holder.local_settings_box.visibility=View.INVISIBLE

        }


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
                            } else {
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


    fun deleteNote(holder: MyViewHolder,activity: MainActivity, position: Int){

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()



        activity.note_list.removeAt(position)


        //Step 1. We download old amount
        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    local_amount_notes = Integer.parseInt(snapshot.child("amount_amount").value.toString())
                    if (local_amount_notes > 0) {
                        local_amount_notes--


                        //Step 2. Update new amount
                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_amount").setValue(local_amount_notes.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {


                                //Step 3. Delete item with index
                                for (i in 1..(local_amount_notes + 1)) {
                                    database.child("notes").child(mAuth.currentUser?.uid.toString()).child(i.toString()).removeValue().addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            //Step 4. Update new list

                                            if(local_amount_notes>0){
                                                for (j in 1..local_amount_notes) {
                                                    println("New amount : $j")
                                                    var indexJ = j -1
                                                    database.child("notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(activity.note_list[(indexJ)]).addOnCompleteListener {
                                                        if(it.isSuccessful){
                                                            local_amount_notes=0
                                                            holder.local_settings_box.visibility=View.INVISIBLE
                                                        }
                                                    }

                                                }
                                            }
                                        } else {
                                            println("To big last index")
                                        }
                                    }
                                }



                                notifyDataSetChanged()

                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}
