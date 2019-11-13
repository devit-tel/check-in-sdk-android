package com.trueelogistics.checkin.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.replaceFragmentInActivity(frameId: Int, fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        replace(frameId, fragment, tag)
    }
}

fun AppCompatActivity.replaceFragmentInActivityAllowingStateLoss(frameId: Int, fragment: Fragment, tag: String) {
    supportFragmentManager.transactAllowingStateLoss {
        replace(frameId, fragment, tag)
    }
}

fun AppCompatActivity.replaceFragmentInActivityStateLoss(frameId: Int, fragment: Fragment, tag: String, addBackStack: Boolean) {
    if (addBackStack) {
        supportFragmentManager.transactAllowingStateLoss {
            replace(frameId, fragment, tag).addToBackStack(tag)
        }
    } else {
        supportFragmentManager.transactAllowingStateLoss {
            replace(frameId, fragment, tag)
        }
    }

}

fun AppCompatActivity.replaceFragmentInActivity(frameId: Int, fragment: Fragment, tag: String, addBackStack: Boolean) {
    if (addBackStack) {
        supportFragmentManager.transact {
            replace(frameId, fragment, tag).addToBackStack(tag)
        }
    } else {
        supportFragmentManager.transact {
            replace(frameId, fragment, tag)
        }
    }

}

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

private inline fun FragmentManager.transactAllowingStateLoss(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commitAllowingStateLoss()
}

fun Fragment.replaceFragmentInActivity(frameId: Int, fragment: Fragment, tag: String) {
    fragmentManager?.transact {
        addToBackStack(tag)
        replace(frameId, fragment, tag)
        addToBackStack(tag)
    }
}


fun Fragment.replaceFragmentInActivity(frameId: Int, fragment: Fragment, tag: String, addBackStack: Boolean) {
    if (addBackStack) {
        fragmentManager?.transact {
            replace(frameId, fragment, tag).addToBackStack(tag)
        }
    } else {
        fragmentManager?.transact {
            replace(frameId, fragment, tag)
        }
    }

}