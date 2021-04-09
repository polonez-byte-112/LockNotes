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

class FavoritesFragmentAdapter(var fav_list: ArrayList<Note>, val activity: MainActivity): RecyclerView.Adapter<FavoritesFragmentAdapter.MyViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var local_fav_notes=0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var local_title = itemView.note_recycler_row_title
        var local_date = itemView.note_recycler_row_date
        var local_text = itemView.note_recycler_row_text
        var local_fav_btn = itemView.note_recycler_row_favorite_btn
        var local_settings_btn =itemView.note_recycler_row_settings
        val local_settings_box = itemView.note_recycler_view_settings_box
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return  MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Basic update

        holder.local_title.text = fav_list[position].note_name
        holder.local_date.text=fav_list[position].note_date
        holder.local_text.text=fav_list[position].note_text


        //Settings

        holder.local_settings_btn.visibility = View.INVISIBLE
        holder.local_settings_box.visibility=View.INVISIBLE
        holder.local_fav_btn.setImageResource(R.drawable.ic_heart_active)


        //Favourite

        holder.local_fav_btn.setOnClickListener {
            deleteFav(position)
        }



    }

    override fun getItemCount(): Int {
      return fav_list.size
    }

    fun downloadFav(activity: MainActivity){
        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        activity.fav_list.clear()
        local_fav_notes=0


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
                                activity.fav_list.add(local_object)
                            }

                            notifyDataSetChanged()
                            local_fav_notes = 0
                        }

                        override fun onCancelled(error: DatabaseError) {
                            println(error.message)
                        }
                    })
                } else {
                    activity.fav_list.clear()
                    notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })


    }

    fun deleteFav(position: Int){

        database.child("notes").child(mAuth.currentUser?.uid.toString()).child(fav_list[position].note_original_id).child("note_is_fav").setValue("0").addOnCompleteListener {
            if(it.isSuccessful){
                database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val local_str = snapshot.child("amount_fav").value.toString()
                        local_fav_notes = Integer.parseInt(local_str)

                        local_fav_notes--

                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_fav").setValue(local_fav_notes.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child((position + 1).toString()).removeValue().addOnCompleteListener {
                                    fav_list.removeAt(position)
                                    notifyDataSetChanged()

                                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).removeValue()

                                    if (local_fav_notes > 0) {
                                        for (j in 1..local_fav_notes) {
                                            println("New amount : $j")
                                            database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(fav_list[j - 1])


                                        }
                                    }
                                }
                            }
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
            }
        }







    }
}