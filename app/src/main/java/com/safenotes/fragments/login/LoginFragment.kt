package com.safenotes.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.login.viewmodels.LoginFragmentViewModel
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {


   private lateinit var viewModel : ViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment



      var view = inflater.inflate(R.layout.fragment_login, container, false)


        viewModel = ViewModelProvider(this).get(LoginFragmentViewModel::class.java)

        view.login_jump_to_register_text_view.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, RegisterFragment())?.commit()
            }


        })
        view.login_continue_btn.setOnClickListener {
            var email = view.login_input_email.text.toString()
            var password = view.login_input_password.text.toString()
            (viewModel as LoginFragmentViewModel).login((activity as MainActivity) ,email,password)




        }
   return view
    }


}