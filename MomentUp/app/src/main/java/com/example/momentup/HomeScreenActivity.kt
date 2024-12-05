package com.example.momentup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.momentup.databinding.ActivityHomeScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/*class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 프래그먼트 설정
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }

        // BottomNavigationView 동작 설정
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_calendar -> {
                    replaceFragment(CalendarFragment())
                    true
                }
                R.id.nav_camera -> {
                    replaceFragment(CameraFragment())
                    true
                }
                R.id.nav_challenges -> {
                    replaceFragment(ChallengesFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Fragment 교체 및 BottomNavigationView와 동기화
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // 해당 Fragment에 맞는 BottomNavigation 메뉴를 선택 상태로 설정
        when (fragment) {
            is HomeFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_home
            is CalendarFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_calendar
            is CameraFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_camera
            is ChallengesFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_challenges
            is ProfileFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_profile
        }
    }
}*/
