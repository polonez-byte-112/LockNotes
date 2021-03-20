package com.safenotes.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.safenotes.MainActivity
import com.safenotes.R
import com.safenotes.adapters.FavoritesFragmentAdapter
import kotlinx.android.synthetic.main.fragment_favorites.view.*


class FavoritesFragment : Fragment() {

    lateinit var adapter: FavoritesFragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

      var view=  inflater.inflate(R.layout.fragment_favorites, container, false)


        adapter = FavoritesFragmentAdapter((activity as MainActivity).fav_list, activity as MainActivity)
        view.favorites_recycler_view.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        view.favorites_recycler_view.adapter = adapter
        adapter.downloadFav(activity as MainActivity)

        return view
    }





}