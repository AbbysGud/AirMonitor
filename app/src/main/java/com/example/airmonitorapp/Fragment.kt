package com.example.airmonitorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.airmonitorapp.fragments.fExplore
import com.example.airmonitorapp.fragments.fHistory
import com.example.airmonitorapp.fragments.fHome
import com.example.airmonitorapp.fragments.fAbout
import com.google.android.material.bottomnavigation.BottomNavigationView

class Fragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        fun getMenuItemId(fragmentName: String): Int {
            return when (fragmentName) {
                "fHome" -> R.id.bot_menu_home
                "fExplore" -> R.id.bot_menu_search
                "fHistory" -> R.id.bot_menu_history
                "fProfile" -> R.id.bot_menu_profile
                else -> R.id.bot_menu_home
            }
        }

        fun loadFragment(fragmentName: String){
            val newFragment = when (fragmentName) {
                "fHome" -> fHome()
                "fExplore" -> fExplore()
                "fHistory" -> fHistory()
                "fProfile" -> fAbout()
                else -> fHome()
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, newFragment)
            transaction.commit()

            bottomNav.menu.findItem(getMenuItemId(fragmentName))?.isChecked = true
        }

        val targetFragmentName = intent.getStringExtra("TARGET_FRAGMENT")
        if (targetFragmentName != null) {
            loadFragment(targetFragmentName)
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragmentName = when (item.itemId) {
                R.id.bot_menu_home -> "fHome"
                R.id.bot_menu_search -> "fExplore"
                R.id.bot_menu_history -> "fHistory"
                R.id.bot_menu_profile -> "fProfile"
                else -> "fHome" // Default fragment
            }

            loadFragment(fragmentName)

            true
        }
    }
}