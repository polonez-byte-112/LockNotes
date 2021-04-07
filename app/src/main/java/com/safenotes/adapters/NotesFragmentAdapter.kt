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
    var local_amount_notes = 0
    var local_fav_notes=0
    var amount_fav = 0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var local_title = itemView.note_recycler_row_title
        var local_date = itemView.note_recycler_row_date
        var local_text = itemView.note_recycler_row_text


        var local_settings_box = itemView.note_recycler_view_settings_box
        var local_settings_btn = itemView.note_recycler_row_settings
        var local_settings_delete = itemView.note_recycler_view_settings_delete
        var local_settings_edit = itemView.note_recycler_view_settings_edit
        var local_settings_cancel = itemView.note_recycler_view_settings_cancel

        var local_fav_btn = itemView.note_recycler_row_favorite_btn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var view = MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_row, parent, false))
        return view
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.local_title.text = list[position].note_name
        holder.local_date.text = list[position].note_date
        holder.local_text.text = list[position].note_text


        holder.local_settings_btn.visibility = View.VISIBLE

        holder.local_settings_box.setBackgroundColor(Color.argb(200, 255, 255, 252))

        holder.local_settings_btn.setOnClickListener {
            holder.local_settings_box.visibility = View.VISIBLE

        }

        holder.local_settings_delete.setOnClickListener {
            deleteNote(holder, activity, position)
        }


        holder.local_settings_edit.setOnClickListener {
            activity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, EditNoteFragment(1, list, position)).commit()
        }

        holder.local_settings_cancel.setOnClickListener {
            holder.local_settings_box.visibility = View.INVISIBLE

        }

        //We download normal state
        database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position + 1).toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var str_state = snapshot.child("note_is_fav").value.toString()
                    var state = Integer.parseInt(str_state)

                    if (state == 1) {
                        holder.local_fav_btn.setImageResource(R.drawable.ic_heart_active)

                    } else if (state == 0) {
                        holder.local_fav_btn.setImageResource(R.drawable.ic_heart_passive)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        holder.local_fav_btn.setOnClickListener {

            //We check state from base

            database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position + 1).toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var str_state = snapshot.child("note_is_fav").value.toString()
                        var state = Integer.parseInt(str_state)

                        //Now we switch depending on state

                        if (state == 0) {
                            holder.local_fav_btn.setImageResource(R.drawable.ic_heart_active)
                            state++
                            list[position].note_is_fav = "1"

                            //We change state here
                            database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position + 1).toString()).child("note_is_fav").setValue(state.toString()).addOnCompleteListener {

                                if (it.isSuccessful) {
                                    //Here we will add note to fav


                                    var fav_note = list[position]


                                    //Fix here adding note not by position by only a amount from internet
                                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.exists()) {
                                                var str_amount_of_fav = snapshot.child("amount_fav").value.toString()
                                                amount_fav = Integer.parseInt(str_amount_of_fav)


                                                database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child((amount_fav + 1).toString()).setValue(fav_note).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        println("We added note to fav!")
                                                        //Now we take current amount and update it ++

                                                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                                if (snapshot.exists()) {
                                                                    var str_amount_of_fav_ = snapshot.child("amount_fav").value.toString()
                                                                    amount_fav = Integer.parseInt(str_amount_of_fav)

                                                                    amount_fav++

                                                                    //Here we update new amount of fav
                                                                    database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_fav").setValue(amount_fav.toString()).addOnCompleteListener {
                                                                        if (it.isSuccessful) {
                                                                            println("We updated amount of fav")
                                                                        } else {
                                                                            println("We didnt updated amount of fav")
                                                                        }
                                                                    }


                                                                }

                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                TODO("Not yet implemented")
                                                            }
                                                        })
                                                    } else {
                                                        println("Error while adding note to fav!")
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
                        } else if (state == 1) {
                            //Here we delete notes from favorites (only from fav)
                            holder.local_fav_btn.setImageResource(R.drawable.ic_heart_passive)
                            state--

                            deleteFavWhileNotes(position, activity)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


    }



    override fun getItemCount(): Int {
        return list.size
    }


    fun downloadNote(activity: MainActivity) {
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
                                    var local_is_fav: String = snapshot.child(i.toString()).child("note_is_fav").value.toString()
                                    var local_original_id = snapshot.child(i.toString()).child("note_original_id").value.toString()
                                    var local_note = Note(local_title, local_text, local_date, local_is_fav, local_original_id)

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

    fun deleteNote(holder: MyViewHolder, activity: MainActivity, position: Int) {

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        deleteFavWithNote(activity,position)
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
                                            if (local_amount_notes > 0) {
                                                for (j in 1..local_amount_notes) {
                                                    var indexJ = j - 1
                                                    activity.note_list[indexJ].note_original_id = j.toString()
                                                    //We need to update here also original id of fav_items
                                                    database.child("notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(activity.note_list[(indexJ)]).addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            local_amount_notes = 0
                                                            holder.local_settings_box.visibility = View.INVISIBLE
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


    private fun deleteFavWhileNotes(note_position: Int, activity: MainActivity ) {

    var fav_adapter =    FavoritesFragmentAdapter(activity.fav_list, activity)
        fav_adapter.downloadFav(activity)

        database.child("notes").child(mAuth.currentUser?.uid.toString()).child((note_position+1).toString()).child("note_is_fav").setValue("0").addOnCompleteListener {

            activity.note_list[note_position].note_is_fav="0"

            if(it.isSuccessful){
                database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){

                            println("Ilosc notatek w fav: ${activity.fav_list.size}")
                        local_fav_notes = Integer.parseInt(snapshot.child("amount_fav").value.toString())
                        local_fav_notes--

                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_fav").setValue(local_fav_notes).addOnCompleteListener {
                            if (it.isSuccessful) {

                                activity.fav_list.forEach {
                                    if(!it.note_original_id.equals((note_position+1).toString())){
                                        activity.new_fav_list.add(it)
                                    }
                                }


                                database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).removeValue().addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        for (j in 1..activity.new_fav_list.size) {
                                            database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(activity.new_fav_list[j - 1])
                                        }


                                        activity.new_fav_list.clear()
                                    }
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


    }




    private fun deleteFavWithNote(activity: MainActivity, position: Int){

        /**
         * Naprawic zmiane note original id po usunie
         * Dodac Usuwanie fav notatki z fav wraz z oryginalna
         * Dodac edytowanie fav notatki wraz z oryginalna
         */
        database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position+1).toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(Integer.parseInt(snapshot.child("note_is_fav").value.toString())==1) {
                        var fav_adapter = FavoritesFragmentAdapter(activity.fav_list, activity)
                        fav_adapter.downloadFav(activity)

                        database.child("notes").child(mAuth.currentUser?.uid.toString()).child((position+1).toString()).child("note_is_fav").setValue("0").addOnCompleteListener {



                            if(it.isSuccessful){
                                database.child("amounts").child(mAuth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()){

                                            local_fav_notes = Integer.parseInt(snapshot.child("amount_fav").value.toString())
                                            local_fav_notes--

                                            database.child("amounts").child(mAuth.currentUser?.uid.toString()).child("amount_fav").setValue(local_fav_notes).addOnCompleteListener {
                                                if (it.isSuccessful) {

                                                    activity.fav_list.forEach {
                                                        if(!it.note_original_id.equals((position+1).toString())){
                                                            activity.new_fav_list.add(it)
                                                        }
                                                    }


                                                    database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).removeValue().addOnCompleteListener {
                                                        if (it.isSuccessful) {

                                                            updateOriginalID(activity)
                                                        }
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

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



    private fun updateOriginalID(activity: MainActivity){

        for(i in 1..activity.note_list.size){
            for(j in 1..activity.new_fav_list.size){

                if(activity.note_list[i-1].note_date.equals(activity.new_fav_list[j-1].note_date) && activity.note_list[i-1].note_text.equals(activity.new_fav_list[j-1].note_text) &&    activity.note_list[i-1].note_name.equals(activity.new_fav_list[j-1].note_name)  ){
                    activity.new_fav_list[j-1].note_original_id= activity.note_list[i-1].note_original_id
                }
            }
        }


        for (j in 1..activity.new_fav_list.size) {
            database.child("fav_notes").child(mAuth.currentUser?.uid.toString()).child(j.toString()).setValue(activity.new_fav_list[j - 1])
        }



        activity.new_fav_list.clear()





    }



}
