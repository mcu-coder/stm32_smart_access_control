package com.example.bluetoothintelligentaccesscontrolsystem.utils

import android.view.View
import android.view.animation.*

/***
 * 图标，图片跳动动画
 */
class BeatingAnimation : HandlerAction {
    fun onAnimation(view: View) {
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(
            ScaleAnimation(
                1.0f, 1.25f, 1.0f, 1.25f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f
            )
        )
        animationSet.addAnimation(AlphaAnimation(1.0f, 0.4f))
        animationSet.duration = 600
        animationSet.interpolator = AccelerateInterpolator()
        animationSet.fillAfter = true
        animationSet.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    val animationSet = AnimationSet(true)
                    animationSet.addAnimation(
                        ScaleAnimation(
                            1.25f, 1.0f, 1.25f,
                            1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f
                        )
                    )
                    animationSet.addAnimation(AlphaAnimation(0.4f, 1.0f))
                    animationSet.duration = 400
                    animationSet.interpolator = DecelerateInterpolator()
                    animationSet.fillAfter = false
                    // 实现心跳的View
                    view.startAnimation(animationSet)
                    postDelayed({ onAnimation(view) }, 400)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        // 实现心跳的View
        view.startAnimation(animationSet)
    }

}