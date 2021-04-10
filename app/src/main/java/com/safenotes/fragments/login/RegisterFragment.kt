package com.safenotes.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.fragments.walkthrough.WalkthroughFragment
import com.safenotes.models.Amount
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*


class RegisterFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var view =  inflater.inflate(R.layout.fragment_register, container, false)


        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference




        view.register_continue_btn.setOnClickListener {
            //Sprawdzenie danych dodac
            var email = register_input_email.text.toString().toLowerCase().trim()
            var password = register_input_password.text.toString().trim()
            var passwordTwo = register_input_again_password.text.toString().trim()


            if(checkData(email,password, passwordTwo)){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if(it.isSuccessful){
                        Toast.makeText(requireContext(), "Added User", Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).updateUI()
                        var newAmount=0
                        var amount = Amount(mAuth.currentUser?.uid.toString(), newAmount.toString(),"0")

                        database.child("amounts").child(mAuth.currentUser?.uid.toString()).setValue(amount).addOnCompleteListener {

                            if(it.isSuccessful){
                                println("Set new amount : $newAmount")
                            }else{
                                println("Error while setting new amount : $newAmount")
                            }

                        }

                        var count = (activity as MainActivity).supportFragmentManager.backStackEntryCount

                        for (i in 0..count)
                        {
                            (activity as MainActivity).supportFragmentManager.popBackStack()
                        }

                        activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.enter_from_right_to_left, R.anim.exit_from_right_to_left,R.anim.enter_from_right_to_left, R.anim.exit_from_right_to_left)?.replace(R.id.fragment_container, WalkthroughFragment())?.commit()
                        //Dodac tez do firebase db
                    }else{
                        Toast.makeText(requireContext(), "Error while adding User", Toast.LENGTH_SHORT).show()
                        it.exception?.printStackTrace()
                    }

                }
            }

        }

        return view
    }

    private fun checkData(email: String, password: String, passwordTwo: String): Boolean {
            if(email.isNotEmpty() && password.isNotEmpty() && passwordTwo.isNotEmpty()){

                                if(email.length>10 && password.length>4 && passwordTwo.length > 4){
                                        if(password !=passwordTwo){
                                            Toast.makeText(requireContext(), "Wrong passwords", Toast.LENGTH_SHORT).show()
                                            return false
                                        }else{
                                            //Teraz Sprawdzamy email


                                               var isValidEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

                                            if(isValidEmail==false){
                                                Toast.makeText(requireContext(), "Wrong email", Toast.LENGTH_SHORT).show()
                                            }
                                            println("Email is : $isValidEmail" )
                                            return isValidEmail

                                        }
                                }
                                else {
                                    Toast.makeText(requireContext(), "Required Length : \nEmail 10+\nPassword 4+", Toast.LENGTH_SHORT).show()
                                    return false
                                }
            }else{
                Toast.makeText(requireContext(), "Fill all forms", Toast.LENGTH_SHORT).show()
                return false
            }


    }

}