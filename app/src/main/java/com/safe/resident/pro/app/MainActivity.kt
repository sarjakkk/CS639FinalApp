package com.safe.resident.pro.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.safe.resident.pro.app.databinding.ActivityMainBinding
import com.safe.resident.pro.app.fragments.LiveFragment
import com.safe.resident.pro.app.fragments.TrackFragment
import com.safe.resident.pro.app.fragments.U911Fragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerTextViews: MutableList<TextView>
    private var selectedFragment: Fragment? = null

    private var trackFragment: Fragment? = null
    private var liveFragment: Fragment? = null
    private var u911Fragment: Fragment? = null
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.white) // set status background white
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(navListener)

        setupFragments()
        setDrawer()
    }

    private fun setDrawer() {
        drawerTextViews = ArrayList()
        drawerTextViews.add(binding.drawerMyAccount)
        drawerTextViews.add(binding.drawerContact)
        for (textView in drawerTextViews) {
            textView.setOnClickListener { view: View? ->
                binding.drawerLayout.closeDrawer(
                    GravityCompat.START
                )
            }
        }
        binding.ivUser.setOnClickListener { view -> binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.tvVersion.setText("v" + BuildConfig.VERSION_NAME)
        selectedFragment = TrackFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, selectedFragment!!)
            .commit()
    }
    private fun setupFragments() {
        // Initialize fragments one time
        trackFragment = supportFragmentManager.findFragmentByTag("track") ?: TrackFragment()
        liveFragment = supportFragmentManager.findFragmentByTag("live") ?: LiveFragment()
        u911Fragment = supportFragmentManager.findFragmentByTag("u911") ?: U911Fragment()
        currentFragment = trackFragment

        // Initially show the TrackFragment
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, trackFragment!!, "track").commit()
    }
    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val nextFragment = when (item.itemId) {
            R.id.menu_track -> trackFragment
            R.id.menu_live -> liveFragment
            R.id.menu_911 -> u911Fragment
            else -> null
        }
        nextFragment?.let {
            switchFragment(it)
        }
        true
    }

    private fun switchFragment(nextFragment: Fragment) {
        if (currentFragment !== nextFragment) {
            supportFragmentManager.beginTransaction().apply {
                hide(currentFragment!!)
                if (!nextFragment.isAdded) {
                    add(R.id.fragment_container, nextFragment)
                } else {
                    show(nextFragment)
                }
                currentFragment = nextFragment
            }.commit()
        }
    }
}
