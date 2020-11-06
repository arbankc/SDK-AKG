package com.akggame.akg_sdk.ui.component

import android.view.MotionEvent
import android.view.View

class onTouchUtil() : View.OnTouchListener {

    private var downRawX: Float = 0.toFloat()
    private var downRawY: Float = 0.toFloat()
    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()
    private lateinit var mView: View

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val action = motionEvent?.action
        if (action == MotionEvent.ACTION_DOWN) {

            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view!!.x - downRawX
            dY = view.y - downRawY

            return true // Consumed

        } else if (action == MotionEvent.ACTION_MOVE) {

            val viewWidth = view?.width
            val viewHeight = view?.height

            val viewParent = view?.parent as View
            val parentWidth = viewParent.width
            val parentHeight = viewParent.height

            var newX = motionEvent.rawX + dX
            newX = Math.max(0f, newX) // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(
                (parentWidth - viewWidth!!).toFloat(),
                newX
            ) // Don't allow the FAB past the right hand side of the parent

            var newY = motionEvent.rawY + dY
            newY = Math.max(0f, newY) // Don't allow the FAB past the top of the parent
            newY = Math.min(
                (parentHeight - viewHeight!!).toFloat(),
                newY
            ) // Don't allow the FAB past the bottom of the parent

            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            return true // Consumed

        } else if (action == MotionEvent.ACTION_UP) {

            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY

            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            return if (Math.abs(upDX) < onTouchUtil.CLICK_DRAG_TOLERANCE && Math.abs(upDY) < onTouchUtil.CLICK_DRAG_TOLERANCE) { // A click
//                performClick()
                false
            } else { // A drag
                true // Consumed
            }

        } else {
            return view!!.onTouchEvent(motionEvent)
        }
    }

    companion object {

        private val CLICK_DRAG_TOLERANCE = 10f
    }
}