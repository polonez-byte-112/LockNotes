package com.safenotes.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.safenotes.R
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment



      var view = inflater.inflate(R.layout.fragment_login, container, false)




        view.login_jump_to_register_text_view.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, RegisterFragment())?.commit()
            }

        })
   return view
    }


}