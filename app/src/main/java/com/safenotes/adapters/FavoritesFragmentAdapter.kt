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
        var view = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_row, parent, false))
        return view
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
            //This place should be delete from fav list and change icon
            deleteFav(activity,position)
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

        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var str_local_fav_notes = snapshot.child("amount_fav").value.toString()
                    local_fav_notes= Integer.parseInt(str_local_fav_notes)

                    //Here we download evey item
                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                           for (i in 1 until (local_fav_notes+1) step 1){
                               var local_title = snapshot.child(i.toString()).child("note_name").value.toString()
                               var local_date =  snapshot.child(i.toString()).child("note_date").value.toString()
                               var local_text =  snapshot.child(i.toString()).child("note_text").value.toString()
                               var local_fav =  snapshot.child(i.toString()).child("note_is_fav").value.toString()

                               var local_object = Note(local_title, local_text, local_date, local_fav)
                               activity.fav_list.add(local_object)
                           }

                            notifyDataSetChanged()
                            local_fav_notes=0
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }else{
                    activity.fav_list.clear()
                    notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    fun deleteFav(activity: MainActivity, position: Int){

        /**
         *
         * Naprawic to z usuwaniem.Są tu  problemy z logika
         * Zobaczyc na problemy z kolejnoscia
         *
         * Tez pamietac ze to wyswietla tyle ulub ile jest podanych w amount_fav
         *
         * Problem tu jest taki że to usuwa zły obiekt
         *
         */
        local_fav_notes=0
        var deleted_note = activity.note_list[position]
        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    local_fav_notes = Integer.parseInt(snapshot.child("amount_fav").value.toString())

                    local_fav_notes--

                    //We update new amount in amounts
                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_fav").setValue(local_fav_notes.toString()).addOnCompleteListener {

                        if (it.isSuccessful) {

                            //We change state of real note

                            database.child("notes").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if (snapshot.exists()) {
                                        for (i in 1 until (local_fav_notes + 2) step 1) {
                                          var text =   snapshot.child(i.toString()).child("note_date").value.toString()
                                            if(text == deleted_note.note_date){
                                                database.child("notes").child(mAuth.currentUser?.uid.toString()).child(i.toString()).child("note_is_fav").setValue("0")

                                                activity.fav_list.removeAt(position)

                                                //Update Firebase DB


                                                //Step 3. Delete item with index
                                                for (h in 1..(local_fav_notes + 2)) {
                                                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(h.toString()).removeValue().addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            //Step 4. Update new list

                                                            if(local_fav_notes>0){
                                                                for (j in 1..local_fav_notes) {
                                                                    println("New amount : $j")
                                                                    var indexJ = j-1
                                                                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(activity.note_list[(indexJ)]).addOnCompleteListener {
                                                                        if(it.isSuccessful){
                                                                            local_fav_notes=0

                                                                        }
                                                                    }

                                                                }
                                                            }
                                                        } else {
                                                            println("To big last index")
                                                        }
                                                    }
                                                }
                                            }
                                            //Problem here is displaying by numer, we need to delete from list also to fix that

                                        }


                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })


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