package com.yonas.biblequizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.yonas.biblequizapp.R

class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailView = view.findViewById<TextView>(R.id.contactEmail)
        val phoneView = view.findViewById<TextView>(R.id.contactPhone)

        // Set your contact info
        emailView.text = "yonastedla06@gmail.com"
        phoneView.text = "+251707106234"
    }
}