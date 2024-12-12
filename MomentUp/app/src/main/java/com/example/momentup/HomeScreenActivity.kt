package com.example.momentup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.momentup.databinding.ActivityHomeScreenBinding
import com.google.firebase.messaging.FirebaseMessaging

class HomeScreenActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HomeScreenActivity"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityHomeScreenBinding
    private var isNavigationStateChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FCM 토큰 가져오기
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "토큰 가져오기 실패", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "FCM 토큰: $token")
                // TODO: 서버에 토큰 전송
            }

        // Android 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        // 알림 버튼 클릭 리스너 추가
        binding.btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        // 기본 프래그먼트 설정
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // BottomNavigationView 동작 설정
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (!isNavigationStateChange) {  // 상태 변경 중이 아닐 때만 처리
                when (item.itemId) {
                    R.id.nav_home -> {
                        replaceFragment(HomeFragment())
                    }
                    R.id.nav_calendar -> {
                        replaceFragment(CalendarFragment())
                    }
                    R.id.nav_camera -> {
                        replaceFragment(CameraFragment())
                    }
                    R.id.nav_challenges -> {
                        replaceFragment(ChallengesFragment())
                    }
                    R.id.nav_profile -> {
                        replaceFragment(ProfileFragment())
                    }
                }
            }
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // Fragment 교체 및 BottomNavigationView와 동기화
    private fun replaceFragment(fragment: Fragment) {
        isNavigationStateChange = true  // 상태 변경 시작

        // Fragment 교체
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // BottomNavigation 메뉴 동기화
        val menuItemId = when (fragment) {
            is HomeFragment -> R.id.nav_home
            is CalendarFragment -> R.id.nav_calendar
            is CameraFragment -> R.id.nav_camera
            is ChallengesFragment -> R.id.nav_challenges
            is ProfileFragment -> R.id.nav_profile
            else -> null
        }

        menuItemId?.let {
            binding.bottomNavigation.selectedItemId = it
        }

        isNavigationStateChange = false  // 상태 변경 완료
    }
}