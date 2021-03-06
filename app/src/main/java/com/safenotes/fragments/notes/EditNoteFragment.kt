package com.safenotes.fragments.notes

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.notes.viewmodels.EditNoteFragmentViewModel
import com.safenotes.models.Note
import kotlinx.android.synthetic.main.fragment_edit_note.view.*


class EditNoteFragment(var state : Int , var lista : ArrayList<Note>, var position: Int) : Fragment() {

    lateinit var vm : ViewModel
    lateinit var titleEditText: EditText
    lateinit var textEditText: EditText
    lateinit var paste_text: CharSequence
     var is_clip_empty: Boolean =true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var view= inflater.inflate(R.layout.fragment_edit_note, container, false)

        vm =  ViewModelProvider(activity!!).get(EditNoteFragmentViewModel::class.java)
        titleEditText = view.findViewById(R.id.edit_note_title)
        textEditText =view.findViewById(R.id.edit_note_text)

        var clipboard: ClipboardManager = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        var clip : ClipData? = clipboard.primaryClip

        if(clip==null){
            println("Empty clipboard")
            paste_text=""
            is_clip_empty=true
        }else{
           paste_text= clip.getItemAt(0).text
            is_clip_empty=false

        }
        paste_btn_behavior(view)


        //We check here if we are going to update current note

        if(state==1){
            if(lista.size>0) {
                view.edit_note_title.setText(lista[position].note_name)
                view.edit_note_text.setText(lista[position].note_text)
            }
        }

        view.edit_note_save_btn.setOnClickListener {

            if(state==0) {
                (vm as EditNoteFragmentViewModel).addNotes(activity as MainActivity, titleEditText.text.toString(), textEditText.text.toString())
            }else{
                //Other fun for posting edited note
                (vm as EditNoteFragmentViewModel).editNote(activity as MainActivity,lista, position, titleEditText.text.toString(), textEditText.text.toString())
            }

            }

        view.edit_note_paste_btn.setOnClickListener {
            if(textEditText.isFocused){
                textEditText.text.insert(textEditText.selectionStart, paste_text)
            }

            if(titleEditText.isFocused){
                titleEditText.text.insert(titleEditText.selectionStart, paste_text)
            }
        }

        clipboard.addPrimaryClipChangedListener {
            clip = clipboard.primaryClip

            if(clip==null){
                println("Empty clipboard")
                paste_text=""
                is_clip_empty=true
            }else{
                paste_text= clip!!.getItemAt(0).text
                is_clip_empty=false

            }

            paste_btn_behavior(view)
        }



        return view
    }

    private fun paste_btn_behavior(view: View) {
        if(is_clip_empty==false){
            view.edit_note_paste_btn.alpha=1f
            //active colors
        }else{
            //passive colors
            view.edit_note_paste_btn.alpha=0.7f
        }
    }



}