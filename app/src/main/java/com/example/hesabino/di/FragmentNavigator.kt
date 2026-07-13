package com.example.hesabino.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.hesabino.R

object FragmentNavigator {

    fun replace(
        fragmentManager: FragmentManager,
        containerId: Int,
        addToBackStack: Boolean = true,
        fragment: Fragment,
        tag: String? = fragment::class.java.simpleName,
        withAnimation: Boolean = true
    ) {
        val transaction = fragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.replace(containerId, fragment, tag)


        if (addToBackStack) {

            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun add(
        fragmentManager: FragmentManager,
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true,
        tag: String? = fragment::class.java.simpleName,
        withAnimation: Boolean = true
    ) {
        val transaction = fragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.add(containerId, fragment, tag)

        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }

        transaction.commit()
    }

    fun replaceWithoutBackStack(
        fragmentManager: FragmentManager,
        containerId: Int,
        fragment: Fragment,
        tag: String? = fragment::class.java.simpleName
    ) {
        fragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
            .commit()
    }

    fun clearBackStack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    fun popBack(fragmentManager: FragmentManager) {
        fragmentManager.popBackStack()
    }
}