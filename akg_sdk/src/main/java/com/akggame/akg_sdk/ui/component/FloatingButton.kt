package com.akggame.akg_sdk.ui.component

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akggame.akg_sdk.dao.api.model.FloatingItem
import com.akggame.akg_sdk.extension.*
import com.akggame.akg_sdk.extension.animateFade
import com.akggame.akg_sdk.extension.animateScale
import com.akggame.akg_sdk.extension.displaySize
import com.akggame.akg_sdk.extension.doAfterAnimate
import com.akggame.akg_sdk.extension.dp2Px
import com.akggame.akg_sdk.extension.updateLayoutParams
import com.akggame.akg_sdk.extension.visible
import com.akggame.akg_sdk.ui.adapter.FloatingAdapter
import com.akggame.akg_sdk.ui.adapter.FloatingAdapterListener
import com.akggame.android.sdk.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.abs

class FloatingButton : FrameLayout {


    enum class ORIENTATION {
        HORIZONTAL,
        VERTICAL
    }

    enum class CircleAnchor {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    enum class Animation {
        NONE,
        FADE,
        SCALE,
    }

    var isFloating = false
        private set
    var isNavigating = false
        private set
    var isCircle = false
        private set
    var isMove = false
        private set

    private lateinit var drawable: GradientDrawable
    private var orientation = ORIENTATION.HORIZONTAL
    private var circleAnchor = CircleAnchor.LEFT
    val circleIcon = CircleImageView(context)
    val recyclerView = RecyclerView(context)
    var autoNavigate = true
    var autoDip = true
    var duration = 350L
    var beforeExpandX = 0f;
    var beforeExpandY = 0f;

    var circleSize = 64f
        set(value) {
            field = value
            updateSubmarine()
        }
    var circleImage: Drawable? = null
        set(value) {
            field = value
            updateSubmarine()
        }
    var circlePadding = 4f
        set(value) {
            field = value
            updateSubmarine()
        }

    var radius = dp2Px(48).toFloat()
        set(value) {
            field = value
            updateSubmarine()
        }
    var color = ContextCompat.getColor(context,android.R.color.transparent)
        set(value) {
            field = value
            updateSubmarine()
        }

    var expandSize: Int = 0
    //        set(value) {
//            field = value
//            updateSubmarine()
//        }
    var floatingButtonAnimation = Animation.SCALE
        set(value) {
            field = value
            updateSubmarine()
        }

    private var adapter = FloatingAdapter()
    var floatingCircleClickListener: FloatingItemClickListener? = null
        set(value) {
            field = value
            circleIcon.setOnClickListener { value?.onCircleClick() }
        }

    var floatingAdapterListener: FloatingAdapterListener? = null
        set(value) {
            field = value
            adapter = FloatingAdapter(value)
            recyclerView.adapter = adapter
        }


    private fun getAttrs(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingButton)
        try {
            setTypeArray(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun getAttrs(attributeSet: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.FloatingButton,
            defStyleAttr,
            0
        )
        try {
            setTypeArray(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        getAttrs(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        getAttrs(attributeSet, defStyle)
    }

    init {
        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            expandSize = context.displaySize().y - dp2Px(30)
        } else {
            expandSize = context.displaySize().x - dp2Px(30)
        }
        updateSubmarine()

        this.setOnTouchListener(object : OnTouchListener {
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
                    newX = Math.max(
                        0f,
                        newX
                    ) // Don't allow the FAB past the left hand side of the parent

                    // Don't allow the FAB past the right hand side of the parent

                    var newY = motionEvent.rawY + dY
                    newY = Math.max(0f, newY) // Don't allow the FAB past the top of the parent
                    // Don't allow the FAB past the bottom of the parent

                    val upRawX = motionEvent.rawX
                    val upRawY = motionEvent.rawY

                    val upDX = upRawX - downRawX
                    val upDY = upRawY - downRawY

//                    if (Math.abs(upDX) > CLICK_DRAG_TOLERANCE && Math.abs(upDY) > CLICK_DRAG_TOLERANCE) {
//                       isMove = true
//                    }

                    if (!isCircle) {
                        newX = Math.min(
                            (parentWidth - viewWidth!!).toFloat(),
                            newX
                        )

                        newY = Math.min(
                            (parentHeight - viewHeight!!).toFloat(),
                            newY
                        )


                    } else {
                        newX = Math.min(
                            (parentWidth - circleIcon.width).toFloat(),
                            newX
                        )

                        newY = Math.min(
                            (parentHeight - circleIcon.height).toFloat(),
                            newY
                        )
                    }

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

                    return if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
//                performClick()
                        if (!isFloating) {
                            setFloat()
                        } else {
                            if (recyclerView.visibility == View.GONE) {
                                expandContainer()
                                isMove = false
                            } else {
                                shrinkContainer()
                            }
                        }
                        false
                    } else { // A drag
                        isMove = true
                        true // Consumed
                    }

                } else {
                    return view!!.onTouchEvent(motionEvent)
                }
            }

            private val CLICK_DRAG_TOLERANCE = 10f

        })
    }

    private fun setTypeArray(a: TypedArray) {
//        when (a.getInt(R.styleable.FloatingButton_floating_button_orientation, 0)) {
//            0 -> this.orientation = ORIENTATION.HORIZONTAL
//            1 -> this.orientation = ORIENTATION.VERTICAL
//        }
//        when (a.getInt(R.styleable.FloatingButton_floating_button_circleAnchor, 0)) {
//            0 -> this.circleAnchor = CircleAnchor.LEFT
//            1 -> this.circleAnchor = CircleAnchor.RIGHT
//            2 -> this.circleAnchor = CircleAnchor.TOP
//            3 -> this.circleAnchor = CircleAnchor.BOTTOM
//        }
//        when (a.getInt(R.styleable.FloatingButton_floating_button_animation, 0)) {
//            0 -> this.floatingButtonAnimation = Animation.NONE
//            1 -> this.floatingButtonAnimation = Animation.FADE
//            2 -> this.floatingButtonAnimation = Animation.SCALE
//        }
//        this.autoNavigate =
//            a.getBoolean(R.styleable.FloatingButton_floating_button_autoNavigate, this.autoNavigate)
//        this.autoDip =
//            a.getBoolean(R.styleable.FloatingButton_floating_button_autoDip, this.autoDip)
        this.duration =
            a.getInt(R.styleable.FloatingButton_floating_button_duration, this.duration.toInt())
                .toLong()
        this.circleSize =
            a.getDimension(R.styleable.FloatingButton_floating_button_circleSize, this.circleSize)
        this.circleImage = a.getDrawable(R.styleable.FloatingButton_floating_button_circleDrawable)
        this.circlePadding =
            a.getDimension(
                R.styleable.FloatingButton_floating_button_circlePadding,
                this.circlePadding
            )
        this.radius = a.getDimension(R.styleable.FloatingButton_floating_button_radius, this.radius)
        this.color = a.getColor(R.styleable.FloatingButton_floating_button_color, this.color)
//        this.expandSize =
//            a.getDimension(
//                R.styleable.FloatingButton_floating_button_expandSize,
//                this.expandSize.toFloat()
//            ).toInt()
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        updateSubmarine()
    }

    private fun updateSubmarine() {
        updateSize()
        updateBackground()
        updateCircleIcon()
        updateChildViews()
        updateAnimation()
    }

    private fun updateSize() {
        this.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                updateLayoutParams {
                    width = dp2Px(circleSize)
                    height = dp2Px(circleSize)

                }
            }
        })
    }

    private fun updateBackground() {
        drawable =
            ContextCompat.getDrawable(context, R.drawable.rectangle_layout) as GradientDrawable
        drawable.cornerRadius = radius
        drawable.setColor(color)
        background = drawable
    }

    private fun updateCircleIcon() {
        if (circleImage != null) {
            circleIcon.scaleType = ImageView.ScaleType.CENTER_CROP
            circleIcon.setImageDrawable(circleImage)
            circleIcon.setPadding(
                dp2Px(circlePadding),
                dp2Px(circlePadding),
                dp2Px(circlePadding),
                dp2Px(circlePadding)
            )
        }
    }

    private fun updateChildViews() {
        removeAllViews()
        if (orientation == ORIENTATION.HORIZONTAL) {
            if (circleAnchor == CircleAnchor.LEFT) {
                addCircleImageView()
                addHorizontalRecyclerView()
                recyclerView.x = getCircleViewAllocation().toFloat()
            } else {
                addHorizontalRecyclerView()
                addCircleImageView()
            }
        } else {
            if (circleAnchor == CircleAnchor.TOP) {
                addCircleImageView()
                addVerticalRecyclerView()
                recyclerView.y = getCircleViewAllocation().toFloat()
            } else {
                addVerticalRecyclerView()
                addCircleImageView()
            }
        }
        invalidate()
    }

    private fun updateAnimation() {
        when (floatingButtonAnimation) {
            Animation.SCALE -> {
                scaleX = 0f
                scaleY = 0f
                visible(false)
            }
            Animation.FADE -> {
                alpha = 0f
                visible(false)
            }
            Animation.NONE -> {
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
                visible(true)
            }
        }
    }

    private fun addCircleImageView() {
        addView(circleIcon, dp2Px(circleSize), dp2Px(circleSize))
    }

    private fun addHorizontalRecyclerView() {
        recyclerView.visible(false)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = adapter
        addView(recyclerView, getRecyclerViewSize(), dp2Px(circleSize))
    }

    private fun addVerticalRecyclerView() {
        recyclerView.visible(false)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        addView(recyclerView, dp2Px(circleSize), getRecyclerViewSize())
    }

    fun setFloat() {
        beforeExpandX = this.x
        beforeExpandY = this.y
        if (!isFloating) {
            isFloating = true
            visible(true)

            when (floatingButtonAnimation) {
                Animation.SCALE -> {
                    animateScale(1.0f, 1.0f, duration / 2)
                        .doAfterAnimate { autoNavigate() }
                }
                Animation.FADE -> {
                    animateFade(1.0f, duration)
                        .doAfterAnimate { autoNavigate() }
                }
                Animation.NONE -> autoNavigate()
            }
        }
    }

    fun navigate() {
        if (!isNavigating) {
            isNavigating = true
            recyclerView.visible(true)
            beginDelayedTransition(duration)
            if (orientation == ORIENTATION.HORIZONTAL) {
                updateWidthParams(expandSize, radius)
                if (circleAnchor == CircleAnchor.RIGHT) {
                    circleIcon.translateX(duration, getRecyclerViewSize().toFloat())
                }
            } else if (orientation == ORIENTATION.VERTICAL) {
                updateHeightParams(expandSize, radius)
                if (circleAnchor == CircleAnchor.BOTTOM) {
                    circleIcon.translateY(duration, getRecyclerViewSize().toFloat())
                }
            }
        }
    }

    fun retreat() {
        if (isNavigating) {
            isNavigating = false
            beginDelayedTransition(duration)

            if (orientation == ORIENTATION.HORIZONTAL) {
                updateWidthParams(dp2Px(circleSize), 1000f)

                circleIcon.translateX(duration, 0f)
                    .doAfterAnimate {
                        if (!isNavigating) {
                            recyclerView.visible(false)
                            autoDip()
                            circleIcon.visibility = View.VISIBLE
                        }
                    }
            } else if (orientation == ORIENTATION.VERTICAL) {
                updateHeightParams(dp2Px(circleSize), 1000f)
                circleIcon.translateY(duration, 0f)
                    .doAfterAnimate {
                        if (!isNavigating) {
                            recyclerView.visible(false)
                            autoDip()
                        }
                    }
            }
        }
    }

    /** dips the circle icon on the layer. */
    fun dip() {
        if (isFloating) {
            if (isNavigating && autoDip) {
                retreat()
            } else if (!isNavigating) {
                isFloating = false

                when (floatingButtonAnimation) {
                    Animation.SCALE -> {
                        animateScale(0f, 0f, duration / 2)
                            .doAfterAnimate {
                                scaleX = 0f
                                scaleY = 0f
                                visible(false)
                            }
                    }
                    Animation.FADE -> {
                        animateFade(.0f, duration)
                            .doAfterAnimate {
                                alpha = 0f
                                visible(false)
                            }
                    }
                    Animation.NONE -> visible(false)
                }
            }
        }
    }

    /** navigates if the [autoNavigate] attribute is ture. */
    private fun autoNavigate() {
        if (autoNavigate) {
            navigate()
        }
    }

    private fun autoDip() {
        if (autoDip) {
            dip()
        }
    }

    fun addItem(floatingItem: FloatingItem) {
        adapter.addItem(getWrapperItem(floatingItem))
    }

    fun addItems(floatingItems: List<FloatingItem>) {
        for (floattingItem in floatingItems) {
            adapter.addItem(getWrapperItem(floattingItem))
        }
    }

    fun removeItems(floatingItem: FloatingItem) {
        adapter.removeItem(getWrapperItem(floatingItem))
    }

    fun clearAllItems() {
        adapter.clearAllItems()
    }

    /** updates layout width params. */
    private fun updateWidthParams(value: Int, radius: Float) {
        updateLayoutParams {
            width = value
            drawable.cornerRadius = radius
        }
    }

    private fun updateParams(value: Int, radius: Float, newX: Float, newY: Float) {
        updateLayoutParams {
            width = value
            drawable.cornerRadius = radius
            x = newX
            y = newY
        }
    }

    /** updates layout width wrapContent. */
    private fun updateWidth(radius: Float) {
        updateLayoutParams {
            width = LayoutParams.WRAP_CONTENT
            drawable.cornerRadius = radius
        }
    }

    /** updates layout height params. */
    private fun updateHeightParams(value: Int, radius: Float) {
        updateLayoutParams {
            height = value
            drawable.cornerRadius = radius
        }
    }

    /** gets wrapped item [SubmarineItemWrapper]. */
    private fun getWrapperItem(floatingItem: FloatingItem): ItemWrapper {
        return ItemWrapper(floatingItem, 0, getRecyclerViewSize(), ORIENTATION.HORIZONTAL)
    }

    /** gets circle icon's size. */
    private fun getCircleViewAllocation(): Int {
        return dp2Px(circleSize)
    }

    /** gets recyclerView's size. */
    private fun getRecyclerViewSize(): Int {
        val params = layoutParams
        var width = expandSize - getCircleViewAllocation()
        if (params is MarginLayoutParams) {
            val margins = (params.leftMargin + params.rightMargin)
            val space = context.displaySize().x - expandSize - margins
            if (space < 0) width += space
        }
        return width
    }

    /** gets px size from the dp size. */
    private fun dp2Px(size: Int): Int {
        return context.dp2Px(size)
    }

    /** gets px size from the dp size. */
    private fun dp2Px(size: Float): Int {
        return context.dp2Px(size)
    }

    fun expandContainer() {
        beforeExpandX = this.x
        beforeExpandY = this.y
        isCircle = false
        this.recyclerView.visibility = View.VISIBLE
        animateScale(1.0f, 1.0f, duration / 2)
            .doAfterAnimate {
                updateWidthParams(expandSize, radius)
                val newX = Math.min(
                    ((this.parent as View).width - this.width).toFloat(),
                    this.x
                )
                if (newX != this.x && !isCircle) {
                    this.animate()
                        .x(newX)
                        .setDuration(duration)
                        .start()
                }
            }
    }

    fun shrinkContainer() {
        isCircle = true
        this.recyclerView.visibility = View.GONE
        if (!isMove) {
            this.animate()
                .x(beforeExpandX)
                .setDuration(duration)
                .start()
        }
    }
}