package com.safenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.safenotes.fragments.login.LoginFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toogle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val navigationView: NavigationView
        navigationView = findViewById(R.id.nav_view)
        navigationView.itemIconTintList=null
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View
        headerView = navigationView.getHeaderView(0)

        //Dokonczyc jutro
        //Dodac tez fragmenty z logowaniem i rejestracja
        //Kazdy ma miec swoje view modele


        toogle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LoginFragment()).commit()

        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_login_item -> {
                Toast.makeText(applicationContext, "Login clicked",Toast.LENGTH_SHORT).show()
            }

            R.id.nav_logout_item->{
                Toast.makeText(applicationContext, "Logout clicked",Toast.LENGTH_SHORT).show()
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
}