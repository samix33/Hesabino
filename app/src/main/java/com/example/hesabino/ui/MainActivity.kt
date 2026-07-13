package com.example.hesabino.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hesabino.databinding.ActivityMainBinding
import com.example.hesabino.ui.home.home_Fragment
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.hesabino.R
import com.example.hesabino.di.FragmentNavigator


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val shared = getSharedPreferences("transaction", Context.MODE_PRIVATE)

        val isFirstRun = shared.getBoolean("first run", true)

        if (isFirstRun) {
            firstRun()

        }else
            FragmentNavigator.replace(
                supportFragmentManager,
                R.id.rootgragment,
                false,
                home_Fragment()
            )



        Log.v("rsagg","ddd")

        WindowCompat.setDecorFitsSystemWindows(window, true)

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // آیکن‌های StatusBar تیره/مشکی
        controller.isAppearanceLightStatusBars = true

        // آیکن‌های NavigationBar پایین هم تیره
        controller.isAppearanceLightNavigationBars = true

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun firstRun() {

        FragmentNavigator.replace(
            supportFragmentManager,
            R.id.rootgragment,
            false,
            onboarding_Fragment()
        )

    }

}
