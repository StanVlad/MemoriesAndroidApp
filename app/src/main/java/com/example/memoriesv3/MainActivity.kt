package com.example.memoriesv3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.webkit.WebView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.memoriesv3.Fragments.NewMemoryFragment
import com.example.memoriesv3.Fragments.PastMemoriesFragment
import com.example.memoriesv3.Fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val new_memory_fragment = NewMemoryFragment()
    private val past_memories_fragment = PastMemoriesFragment()
    private val profile_fragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swapFragment(past_memories_fragment)

        val nav_bar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        nav_bar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.new_memory_button -> swapFragment(new_memory_fragment)
                R.id.past_memories_button -> swapFragment(past_memories_fragment)
                R.id.profile_button -> swapFragment(profile_fragment)
            }
            true
        }
    }

    private fun swapFragment(fragment: Fragment?){
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}