package com.safenotes.viewmodels

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.notes.NotesFragment

class LoginFragmentViewModel:  ViewModel() {

    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    fun login(activity: MainActivity, email : String, password: String) {

        val context : Context = activity.applicationContext
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

      if(checkData(context,email,password)){
          mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
              if(it.isSuccessful){
                  activity.updateUI()

                  activity.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up,R.anim.enter_from_down_to_up, R.anim.exit_from_down_to_up).replace(R.id.fragment_container, NotesFragment()).commit()

              }else{
                  Toast.makeText(context, "Wrong email or password",Toast.LENGTH_SHORT).show()
              }
          }
      }



    }

    private fun checkData(context: Context,email: String, password: String): Boolean {


        if(email.isNotEmpty() && password.isNotEmpty() ){

            if(email.length>10 && password.length>4 ){

                    var isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()

                    if(isValidEmail==false){
                        Toast.makeText(context, "Wrong email", Toast.LENGTH_SHORT).show()
                    }
                    println("Email is : $isValidEmail" )
                    return isValidEmail


            }
            else {
                Toast.makeText(context, "Required Length : \nEmail 10+\nPassword 4+", Toast.LENGTH_SHORT).show()
                return false
            }
        }else{
            Toast.makeText(context, "Fill all forms", Toast.LENGTH_SHORT).show()
            return false
        }


    }
}