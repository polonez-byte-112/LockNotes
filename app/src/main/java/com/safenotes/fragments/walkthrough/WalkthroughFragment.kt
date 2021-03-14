package com.safenotes.fragments.walkthrough

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenotes.R
import com.safenotes.adapters.WalkthroughFragmentAdapter
import com.safenotes.fragments.notes.NotesFragment
import com.safenotes.models.Amount


class WalkthroughFragment : Fragment() {

    private    var currentPage : Int=0
    private    lateinit var slideAdapter : WalkthroughFragmentAdapter
    private    lateinit var mDots: Array<TextView>
    private    lateinit var viewPager: ViewPager
    private    lateinit var mDotsLayout: LinearLayout
    private  lateinit var nextBtn  : Button
    private  lateinit var backBtn  : Button
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.fragment_walkthrough, container, false)

        viewPager = view.findViewById(R.id.walkthrough_ViewPager) as ViewPager
        mDotsLayout =view.findViewById(R.id.walkthrough_dots_layout) as LinearLayout
        slideAdapter = WalkthroughFragmentAdapter(requireContext())

        viewPager.adapter=slideAdapter
        addDots(0)


        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                addDots(position)
                currentPage = position

                val lastOne = mDots.size-1
                println("Lastone : $lastOne")

                println("I am at $position")
                if (position == 0) {
                    nextBtn.isEnabled = true
                    backBtn.isEnabled = false

                    backBtn.visibility = View.INVISIBLE
                    nextBtn.text = "Next"
                    backBtn.text = ""
                } else if (position == lastOne) {
                    nextBtn.text = "Finish"
                    backBtn.text = "Back"
                } else {
                    nextBtn.isEnabled = true
                    backBtn.isEnabled = true

                    backBtn.visibility = View.VISIBLE
                    nextBtn.text = "Next"
                    backBtn.text = "Back"
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        backBtn = view.findViewById(R.id.walkthrough_back_btn) as Button
        nextBtn = view.findViewById(R.id.walkthrough_next_btn) as Button

        backBtn.setOnClickListener {
            viewPager.setCurrentItem(currentPage-1)
        }

        nextBtn.setOnClickListener {
        var newAmount=0

            if(currentPage+1==3){
                var amount = Amount(mAuth.currentUser?.uid.toString(), newAmount.toString())

                database.child("amounts").child(mAuth.currentUser?.uid.toString()).setValue(amount).addOnCompleteListener {

                    if(it.isSuccessful){
                        println("Set new amount : $newAmount")
                    }else{
                        println("Error while setting new amount : $newAmount")
                    }

                }

                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragment_container, NotesFragment())?.commit()
            }
            viewPager.setCurrentItem(currentPage+1)

        }


        return view
    }

    private fun addDots(pos: Int) {
        mDots = Array(3) { i -> (TextView(context)) }
        mDotsLayout.removeAllViews()

        var dark_passive = ResourcesCompat.getColor(resources,R.color.project_light_gray, null)
        var dark_active = ResourcesCompat.getColor(resources,R.color.project_dark_grey, null)
        mDots.forEachIndexed { index, textView ->
            mDots[index] = TextView(requireContext())
            mDots[index].text= Html.fromHtml("&#8226;")
            mDots[index].textSize= 35F

            mDots[index].setTextColor(dark_passive)

            mDotsLayout.addView(mDots[index])
        }

        if(mDots.size>0){
            mDots[pos].setTextColor(dark_active)
        }

    }


}