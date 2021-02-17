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
import com.safenotes.R
import com.safenotes.fragments.walkthrough.adapter.WalkthroughFragmentAdapter


class WalkthroughFragment : Fragment() {

    private    var currentPage : Int=0
    private    lateinit var slideAdapter : WalkthroughFragmentAdapter
    private    lateinit var mDots: Array<TextView>
    private    lateinit var viewPager: ViewPager
    private    lateinit var mDotsLayout: LinearLayout
    private  lateinit var nextBtn  : Button
    private  lateinit var backBtn  : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.fragment_walkthrough, container, false)

        viewPager = view.findViewById(R.id.walkthrough_ViewPager) as ViewPager
        mDotsLayout =view.findViewById(R.id.walkthrough_dots_layout) as LinearLayout
        slideAdapter = WalkthroughFragmentAdapter(requireContext())

        viewPager.adapter=slideAdapter
        addDots(0)


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
            viewPager.setCurrentItem(currentPage+1)
        }


        return view
    }

    private fun addDots(pos: Int) {
        mDots = Array(3) { i -> (TextView(context)) }
        mDotsLayout.removeAllViews()

        var dark_passive = ResourcesCompat.getColor(resources,R.color.project_dark_passive, null)
        var dark_active = ResourcesCompat.getColor(resources,R.color.project_dark_active, null)
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