package com.yonas.biblequizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yonas.biblequizapp.MainActivity
import com.yonas.biblequizapp.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the cards by ID and set click listeners
        val oldCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.oldTestamentCard)
        val newCard = view.findViewById<androidx.cardview.widget.CardView>(R.id.newTestamentCard)

        oldCard.setOnClickListener {
            val fragment = LevelFragment().apply {
                arguments = Bundle().apply {
                    putString("section", "old")
                }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }

        newCard.setOnClickListener {
            val fragment = LevelFragment().apply {
                arguments = Bundle().apply {
                    putString("section", "new")
                }
            }
            (activity as MainActivity).replaceFragment(fragment)
        }
    }
}