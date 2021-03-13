package com.safenotes.fragments.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.safenotes.R
import kotlinx.android.synthetic.main.fragment_notes.view.*


class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     var view = inflater.inflate(R.layout.fragment_notes, container, false)

        view.notes_floating_btn_add_note.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, EditNoteFragment())?.commit()
        }





        return view
    }


}