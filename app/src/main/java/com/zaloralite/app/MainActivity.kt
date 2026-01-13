package com.zaloralite.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zaloralite.app.fragment.AkunFragment
import com.zaloralite.app.fragment.HomeFragment
import com.zaloralite.app.fragment.TasFragment
import com.zaloralite.app.fragment.WishlistFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_wishlist -> selectedFragment = WishlistFragment()
                R.id.nav_tas -> selectedFragment = TasFragment()
                R.id.nav_akun -> selectedFragment = AkunFragment()
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    // Fungsi untuk mengganti fragment
    private fun loadFragment(fragment: Fragment) {
        if (!isFinishing && !isDestroyed) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss() // Lebih aman untuk menghindari crash transaksi
        }
    }
}