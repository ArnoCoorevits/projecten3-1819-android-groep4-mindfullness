package com.groep4.mindfulness.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.groep4.mindfulness.R
import com.groep4.mindfulness.adapters.SessiePagerAdapter
import com.groep4.mindfulness.model.Sessie
import kotlinx.android.synthetic.main.fragment_sessie_page.view.*

class FragmentSessiePage : Fragment() {

    lateinit var sessie: Sessie
    var page: Int = 0

    companion object {
        fun newInstance(): FragmentSessiePage {
            return FragmentSessiePage()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_sessie_page, container, false)

        val bundle = this.arguments

        if (bundle != null) {
            sessie = bundle.getParcelable("key_sessie")
            page = bundle.getInt("key_page")

            view.tv_page.text = sessie.naam
            view.tv_page.text = "Sessie $page"
        }

        val fragmentAdapter = SessiePagerAdapter(activity!!.supportFragmentManager, sessie, page)
        val viewpager = view.sessie_viewpager as ViewPager
        val tablayout = view.sessie_tabs as TabLayout
        viewpager.adapter = fragmentAdapter
        tablayout.setupWithViewPager(viewpager)
        return view
    }
}