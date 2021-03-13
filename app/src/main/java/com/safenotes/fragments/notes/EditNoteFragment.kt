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
import kotlinx.android.synthetic.main.fragment_edit_note.view.*


class EditNoteFragment : Fragment() {

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



        view.edit_note_save_btn.setOnClickListener {
            (vm as EditNoteFragmentViewModel).addNotes(activity as MainActivity,titleEditText.text.toString(),textEditText.text.toString())
        }

        view.edit_note_paste_btn.setOnClickListener {
            var newText : String=""
            if(textEditText.isFocused){
                 newText= textEditText.text.append(paste_text).toString()
                textEditText.setText(newText)
            }

            if(titleEditText.isFocused){
                newText= titleEditText.text.append(paste_text).toString()
                titleEditText.setText(newText)
            }


        }


        ClipboardManager.OnPrimaryClipChangedListener {

            /**
             * Post here same stuff as above with clip .
             *
             * Problem : When i copy another text it doesn't switch data
             *
             * Same problem occurs in non-virtual phone.
             */

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
        //Leave it on observables becouse it can change

        if(is_clip_empty==false){
            view.edit_note_paste_btn.alpha=1f
            //active colors
        }else{
            //passive colors
            view.edit_note_paste_btn.alpha=0.7f
        }
    }


}