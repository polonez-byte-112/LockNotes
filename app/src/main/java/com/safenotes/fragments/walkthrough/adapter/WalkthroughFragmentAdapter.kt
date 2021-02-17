package com.safenotes.fragments.walkthrough.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.safenotes.R


class WalkthroughFragmentAdapter(val context: Context) : PagerAdapter() {


    var slide_imgs = intArrayOf(R.drawable.ic_person,R.drawable.ic_person, R.drawable.ic_person)
    var slide_titles = arrayOf("Easy", "Fast", "Compatible")
    var slide_descs = arrayOf(
        "LockNote is usefull and easy to understand app where u can store your Notes!\nYour data is encrypted and saved on Google Servers. ",
        "By using LockNotes u can write Notes 104% faster!",
        "This app is compatible with every android phone above 4.4 version."
    )


    override fun getCount(): Int {
      return slide_descs.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
       return view ==  `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.fragment_walkthrough_slide,container,false)

        val slideImg= view.findViewById(R.id.walkthrough_slide_img) as ImageView
        val slideTitle= view.findViewById(R.id.walkthrough_slide_title) as TextView
        val slideDesc= view.findViewById(R.id.walkthrough_slide_desc) as  TextView

        slideImg.setImageResource(slide_imgs[position])
        slideTitle.text = slide_titles[position]
        slideDesc.text = slide_descs[position]

        container.addView(view)
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}