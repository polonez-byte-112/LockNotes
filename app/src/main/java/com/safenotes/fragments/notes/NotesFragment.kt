package com.safenotes.fragments.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.adapters.NotesFragmentAdapter
import com.safenotes.models.Note
import kotlinx.android.synthetic.main.fragment_notes.view.*


class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     var view = inflater.inflate(R.layout.fragment_notes, container, false)


        val adapter = NotesFragmentAdapter((activity as MainActivity).note_list, activity as MainActivity)
         adapter.downloadNote(activity as MainActivity)
        view.notes_recycler_view.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        view.notes_recycler_view.adapter = adapter

        view.notes_floating_btn_add_note.setOnClickListener {
            val tab = ArrayList<Note>()
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up,R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up)?.addToBackStack(null)?.add(R.id.fragment_container, EditNoteFragment(0,tab,0))?.commit()
        }


        return view
    }




}