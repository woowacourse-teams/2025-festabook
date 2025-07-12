package com.daedan.festabook

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.daedan.festabook.databinding.ActivityMainBinding
import com.daedan.festabook.presentation.schedule.ScheduleFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val homeFragment by lazy {
        HomeFragment().newInstance()
    }

    private val scheduleFragment by lazy {
        ScheduleFragment().newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fcv_fragment_container, homeFragment, TAG_HOME_FRAGMENT)
            }
        }
        onClickBottomNavigationBarItem()
    }

    private fun onClickBottomNavigationBarItem() {
        binding.bnvMenu.setOnItemSelectedListener { icon ->

            if (binding.bnvMenu.selectedItemId == icon.itemId) {
                return@setOnItemSelectedListener false
            }
            when (icon.itemId) {
                R.id.item_menu_home -> switchFragment(homeFragment, TAG_HOME_FRAGMENT)
                R.id.item_menu_schedule -> switchFragment(scheduleFragment, TAG_SCHEDULE_FRAGMENT)
                R.id.item_menu_map -> {}
                R.id.item_menu_news -> {}
                R.id.item_menu_setting -> {}
            }
            true
        }
    }

    private fun switchFragment(
        fragment: Fragment,
        tag: String,
    ) {
        supportFragmentManager.commit {
            supportFragmentManager.fragments.forEach { fragment -> hide(fragment) }

            val existing = supportFragmentManager.findFragmentByTag(tag)
            if (existing != null) {
                show(existing)
            } else {
                add(R.id.fcv_fragment_container, fragment, tag)
            }
            setReorderingAllowed(true)
        }
    }

    companion object {
        private const val TAG_HOME_FRAGMENT = "homeFragment"
        private const val TAG_SCHEDULE_FRAGMENT = "scheduleFragment"

        fun Fragment.newInstance(): Fragment =
            this.apply {
                arguments = Bundle()
            }
    }
}
