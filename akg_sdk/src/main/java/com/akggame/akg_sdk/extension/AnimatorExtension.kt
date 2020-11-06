package com.akggame.akg_sdk.extension

import android.animation.Animator
import android.view.ViewPropertyAnimator

/** do something block codes after finish animation. */
internal fun ViewPropertyAnimator.doAfterAnimate(block: () -> Unit) {
    this.setListener(object : Animator.AnimatorListener {
        override fun onAnimationEnd(p0: Animator?) = block()
        override fun onAnimationRepeat(p0: Animator?) = Unit
        override fun onAnimationCancel(p0: Animator?) = Unit
        override fun onAnimationStart(p0: Animator?) = Unit
    })
}