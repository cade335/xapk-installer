package io.xapk.apkinstaller.ui.widget.button

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatButton
import io.xapk.apkinstaller.R

class StateButton : AppCompatButton {
    //text color
    private var mNormalTextColor = 0
    private var mPressedTextColor = 0
    private var mUnableTextColor = 0
    //animation duration
    private var mDuration = 0
    //radius
    private var mRadius = 0f
    private var mRound: Boolean = false
    //stroke
    private var mStrokeDashWidth = 0f
    private var mStrokeDashGap = 0f
    private var mNormalStrokeWidth = 0
    private var mPressedStrokeWidth = 0
    private var mUnableStrokeWidth = 0
    private var mNormalStrokeColor = 0
    private var mPressedStrokeColor = 0
    private var mUnableStrokeColor = 0
    private var mNormalBackgroundColor = 0
    private var mPressedBackgroundColor = 0
    private var mUnableBackgroundColor = 0
    private var mNormalBackground = GradientDrawable()
    private var mPressedBackground = GradientDrawable()
    private var mUnableBackground = GradientDrawable()
    private val states by lazy { arrayOfNulls<IntArray>(4) }
    private var mStateBackground: StateListDrawable? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, androidx.appcompat.R.attr.buttonStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val drawable = background
        mStateBackground = if (drawable is StateListDrawable) {
            drawable
        } else {
            StateListDrawable()
        }
        //pressed, focused, normal, unable
        states[0] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        states[1] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused)
        states[3] = intArrayOf(-android.R.attr.state_enabled)
        states[2] = intArrayOf(android.R.attr.state_enabled)
        val a = context.obtainStyledAttributes(attrs, R.styleable.StateButton)
        val mTextColorStateList = textColors
        val mDefaultNormalTextColor = mTextColorStateList.getColorForState(states[2], currentTextColor)
        val mDefaultPressedTextColor = mTextColorStateList.getColorForState(states[0], currentTextColor)
        val mDefaultUnableTextColor = mTextColorStateList.getColorForState(states[3], currentTextColor)
        mNormalTextColor = a.getColor(R.styleable.StateButton_normalTextColor, mDefaultNormalTextColor)
        mPressedTextColor = a.getColor(R.styleable.StateButton_pressedTextColor, mDefaultPressedTextColor)
        mUnableTextColor = a.getColor(R.styleable.StateButton_unableTextColor, mDefaultUnableTextColor)
        mDuration = a.getInteger(R.styleable.StateButton_animationDuration, mDuration)
        mNormalBackgroundColor = a.getColor(R.styleable.StateButton_normalBackgroundColor, 0)
        mPressedBackgroundColor = a.getColor(R.styleable.StateButton_pressedBackgroundColor, 0)
        mUnableBackgroundColor = a.getColor(R.styleable.StateButton_unableBackgroundColor, 0)
        mRadius = a.getDimensionPixelSize(R.styleable.StateButton_radius, 0).toFloat()
        mRound = a.getBoolean(R.styleable.StateButton_isRound, false)
        mStrokeDashWidth = a.getDimensionPixelSize(R.styleable.StateButton_strokeDashWidth, 0).toFloat()
        mStrokeDashGap = a.getDimensionPixelSize(R.styleable.StateButton_strokeDashWidth, 0).toFloat()
        mNormalStrokeWidth = a.getDimensionPixelSize(R.styleable.StateButton_normalStrokeWidth, 0)
        mPressedStrokeWidth = a.getDimensionPixelSize(R.styleable.StateButton_pressedStrokeWidth, 0)
        mUnableStrokeWidth = a.getDimensionPixelSize(R.styleable.StateButton_unableStrokeWidth, 0)
        mNormalStrokeColor = a.getColor(R.styleable.StateButton_normalStrokeColor, 0)
        mPressedStrokeColor = a.getColor(R.styleable.StateButton_pressedStrokeColor, 0)
        mUnableStrokeColor = a.getColor(R.styleable.StateButton_unableStrokeColor, 0)
        a.recycle()

        setTextColor()
        mStateBackground!!.setEnterFadeDuration(mDuration)
        mStateBackground!!.setExitFadeDuration(mDuration)
        mNormalBackground.setColor(mNormalBackgroundColor)
        mPressedBackground.setColor(mPressedBackgroundColor)
        mUnableBackground.setColor(mUnableBackgroundColor)
        mNormalBackground.cornerRadius = mRadius
        mPressedBackground.cornerRadius = mRadius
        mUnableBackground.cornerRadius = mRadius
        setStroke()
        //set background
        mStateBackground!!.addState(states[0], mPressedBackground)
        mStateBackground!!.addState(states[1], mPressedBackground)
        mStateBackground!!.addState(states[3], mUnableBackground)
        mStateBackground!!.addState(states[2], mNormalBackground)
        setBackgroundDrawable(mStateBackground!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setRound(mRound)
    }

    fun setNormalStrokeColor(@ColorInt normalStrokeColor: Int) {
        this.mNormalStrokeColor = normalStrokeColor
        setStroke(mNormalBackground, mNormalStrokeColor, mNormalStrokeWidth)
    }

    fun setPressedStrokeColor(@ColorInt pressedStrokeColor: Int) {
        this.mPressedStrokeColor = pressedStrokeColor
        setStroke(mPressedBackground, mPressedStrokeColor, mPressedStrokeWidth)
    }

    fun setUnableStrokeColor(@ColorInt unableStrokeColor: Int) {
        this.mUnableStrokeColor = unableStrokeColor
        setStroke(mUnableBackground, mUnableStrokeColor, mUnableStrokeWidth)
    }

    fun setStateStrokeColor(@ColorInt normal: Int, @ColorInt pressed: Int, @ColorInt unable: Int) {
        mNormalStrokeColor = normal
        mPressedStrokeColor = pressed
        mUnableStrokeColor = unable
        setStroke()
    }

    fun setNormalStrokeWidth(normalStrokeWidth: Int) {
        this.mNormalStrokeWidth = normalStrokeWidth
        setStroke(mNormalBackground, mNormalStrokeColor, mNormalStrokeWidth)
    }

    fun setPressedStrokeWidth(pressedStrokeWidth: Int) {
        this.mPressedStrokeWidth = pressedStrokeWidth
        setStroke(mPressedBackground, mPressedStrokeColor, mPressedStrokeWidth)
    }

    fun setUnableStrokeWidth(unableStrokeWidth: Int) {
        this.mUnableStrokeWidth = unableStrokeWidth
        setStroke(mUnableBackground, mUnableStrokeColor, mUnableStrokeWidth)
    }

    fun setStateStrokeWidth(normal: Int, pressed: Int, unable: Int) {
        mNormalStrokeWidth = normal
        mPressedStrokeWidth = pressed
        mUnableStrokeWidth = unable
        setStroke()
    }

    fun setStrokeDash(strokeDashWidth: Float, strokeDashGap: Float) {
        this.mStrokeDashWidth = strokeDashWidth
        this.mStrokeDashGap = strokeDashGap
        setStroke()
    }

    fun setRadius(radius: Float) {
        this.mRadius = radius
        mNormalBackground.cornerRadius = mRadius
        mPressedBackground.cornerRadius = mRadius
        mUnableBackground.cornerRadius = mRadius
    }

    fun setRound(round: Boolean) {
        this.mRound = round
        val height = measuredHeight
        if (mRound) {
            setRadius(height / 2f)
        }
    }

    fun setRadius(radii: FloatArray) {
        mNormalBackground.cornerRadii = radii
        mPressedBackground.cornerRadii = radii
        mUnableBackground.cornerRadii = radii
    }

    fun setStateBackgroundColor(@ColorInt normal: Int, @ColorInt pressed: Int, @ColorInt unable: Int) {
        mNormalBackgroundColor = normal
        mPressedBackgroundColor = pressed
        mUnableBackgroundColor = unable
        mNormalBackground.setColor(mNormalBackgroundColor)
        mPressedBackground.setColor(mPressedBackgroundColor)
        mUnableBackground.setColor(mUnableBackgroundColor)
    }

    fun setNormalBackgroundColor(@ColorInt normalBackgroundColor: Int) {
        this.mNormalBackgroundColor = normalBackgroundColor
        mNormalBackground.setColor(mNormalBackgroundColor)
    }

    fun setPressedBackgroundColor(@ColorInt pressedBackgroundColor: Int) {
        this.mPressedBackgroundColor = pressedBackgroundColor
        mPressedBackground.setColor(mPressedBackgroundColor)
    }

    fun setUnableBackgroundColor(@ColorInt unableBackgroundColor: Int) {
        this.mUnableBackgroundColor = unableBackgroundColor
        mUnableBackground.setColor(mUnableBackgroundColor)
    }

    fun setAnimationDuration(@IntRange(from = 0) duration: Int) {
        this.mDuration = duration
        mStateBackground!!.setEnterFadeDuration(mDuration)
    }

    fun setStateTextColor(@ColorInt normal: Int, @ColorInt pressed: Int, @ColorInt unable: Int) {
        this.mNormalTextColor = normal
        this.mPressedTextColor = pressed
        this.mUnableTextColor = unable
        setTextColor()
    }

    fun setNormalTextColor(@ColorInt normalTextColor: Int) {
        this.mNormalTextColor = normalTextColor
        setTextColor()

    }

    fun setPressedTextColor(@ColorInt pressedTextColor: Int) {
        this.mPressedTextColor = pressedTextColor
        setTextColor()
    }

    fun setUnableTextColor(@ColorInt unableTextColor: Int) {
        this.mUnableTextColor = unableTextColor
        setTextColor()
    }

    private fun setTextColor() {
        val colors = intArrayOf(mPressedTextColor, mPressedTextColor, mNormalTextColor, mUnableTextColor)
        setTextColor(ColorStateList(states, colors))
    }

    private fun setStroke() {
        setStroke(mNormalBackground, mNormalStrokeColor, mNormalStrokeWidth)
        setStroke(mPressedBackground, mPressedStrokeColor, mPressedStrokeWidth)
        setStroke(mUnableBackground, mUnableStrokeColor, mUnableStrokeWidth)
    }

    private fun setStroke(mBackground: GradientDrawable, mStrokeColor: Int, mStrokeWidth: Int) {
        mBackground.setStroke(mStrokeWidth, mStrokeColor, mStrokeDashWidth, mStrokeDashGap)
    }
}