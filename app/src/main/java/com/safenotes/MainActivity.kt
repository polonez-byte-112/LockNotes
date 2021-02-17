package com.safenotes

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.fragments.login.LoginFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toogle : ActionBarDrawerToggle
    lateinit var displayed_email: TextView

    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.itemIconTintList=null
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View
        headerView = navigationView.getHeaderView(0)

        displayed_email = headerView.findViewById(R.id.nav_header_name)





        toogle = ActionBarDrawerToggle(this, drawerLayout,findViewById(R.id.toolbar), R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()




        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()
        }

        updateUI()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_login_item -> {
                Toast.makeText(applicationContext, "Login clicked",Toast.LENGTH_SHORT).show()
            }

            R.id.nav_logout_item->{
                    mAuth.signOut()
                AuthUI.getInstance()
                        .signOut(this) // context
                        .addOnCompleteListener {
                            updateUI()
                            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment())?.commit()

                        }
            }

            R.id.nav_home_item->{
                Toast.makeText(applicationContext, "Home clicked",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_fav_item->{
                Toast.makeText(applicationContext, "Favorite clicked",Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    fun updateUI(){
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        val navigationView: NavigationView
        navigationView = findViewById(R.id.nav_view)
        val nav_menu  = navigationView.menu


        if(mAuth.currentUser!=null) {

            displayed_email.setText(mAuth.currentUser!!.email.toString())
            displayed_email.textSize = 18F



            nav_menu.findItem(R.id.nav_login_item).isVisible = false
            nav_menu.findItem(R.id.nav_home_item).isVisible = true
            nav_menu.findItem(R.id.nav_fav_item).isVisible = true
            nav_menu.findItem(R.id.nav_logout_item).isVisible = true
        }else{

                displayed_email.setText("Guest")
                displayed_email.textSize= 30F


            nav_menu.findItem(R.id.nav_login_item).isVisible = true
            nav_menu.findItem(R.id.nav_home_item).isVisible = false
            nav_menu.findItem(R.id.nav_fav_item).isVisible = false
            nav_menu.findItem(R.id.nav_logout_item).isVisible = false


        }



    }

}