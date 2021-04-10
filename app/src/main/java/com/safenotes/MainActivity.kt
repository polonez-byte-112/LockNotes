package com.safenotes

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.fragments.favorites.FavoritesFragment
import com.safenotes.fragments.login.LoginFragment
import com.safenotes.fragments.notes.NotesFragment
import com.safenotes.models.Note
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toogle : ActionBarDrawerToggle
    lateinit var displayed_email: TextView

    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var note_list:ArrayList<Note> =  ArrayList()
    var new_fav_list:ArrayList<Note> =  ArrayList()
    var fav_list:ArrayList<Note> =  ArrayList()

    var LOGIN_STATE=false
    var REGISTER_STATE=false
    var WALKTHROUGH_STATE=false
    var NOTE_STATE=false
    var EDIT_NOTE_STATE=false
    var FAVORITES_STATE=false


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


        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        if(savedInstanceState==null ){

            if(mAuth.currentUser==null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()
           }else{
           supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
            }

        }






        updateUI()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.nav_login_item -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()
            }

            R.id.nav_logout_item->{
                    mAuth.signOut()
                AuthUI.getInstance()
                        .signOut(this) // context
                        .addOnCompleteListener {
                            updateUI()
                            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()


                        }
            }

            R.id.nav_home_item->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
            }
            R.id.nav_fav_item->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FavoritesFragment()).commit()
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

            displayed_email.text = mAuth.currentUser!!.email.toString()
            displayed_email.textSize = 18F



            nav_menu.findItem(R.id.nav_login_item).isVisible = false
            nav_menu.findItem(R.id.nav_home_item).isVisible = true
            nav_menu.findItem(R.id.nav_fav_item).isVisible = true
            nav_menu.findItem(R.id.nav_logout_item).isVisible = true
        }else{

            displayed_email.text = "Guest"
                displayed_email.textSize= 30F


            nav_menu.findItem(R.id.nav_login_item).isVisible = true
            nav_menu.findItem(R.id.nav_home_item).isVisible = false
            nav_menu.findItem(R.id.nav_fav_item).isVisible = false
            nav_menu.findItem(R.id.nav_logout_item).isVisible = false


        }



    }

    override fun onBackPressed() {
        println("Register state: $REGISTER_STATE")
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {

            if(NOTE_STATE==true){
                NOTE_STATE=false
                super.onBackPressed()
            }

            if(LOGIN_STATE==true){
                LOGIN_STATE=false
                super.onBackPressed()
            }


            if(WALKTHROUGH_STATE==true){
                WALKTHROUGH_STATE=false
                super.onBackPressed()
            }


            if(REGISTER_STATE==true){
                REGISTER_STATE=false
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()
                LOGIN_STATE=true
            }



            if(FAVORITES_STATE==true){
                FAVORITES_STATE=false
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
                NOTE_STATE=true
            }


            if(EDIT_NOTE_STATE==true){
                EDIT_NOTE_STATE=false
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, NotesFragment()).commit()
                NOTE_STATE=true
            }



        }
    }

}