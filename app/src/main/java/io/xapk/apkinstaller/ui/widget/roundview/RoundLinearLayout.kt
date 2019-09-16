package io.xapk.apkinstaller.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.utils.ScreenUtils

class RoundLinearLayout : LinearLayout {
    private val gdBackground = GradientDrawable()
    private val gdBackgroundPress = GradientDrawable()
    private var backgroundColor: Int = 0
    private var backgroundPressColor: Int = 0
    private var cornerRadius: Int = 0
    private var cornerRadiusTL: Int = 0
    private var cornerRadiusTR: Int = 0
    private var cornerRadiusBL: Int = 0
    private var cornerRadiusBR: Int = 0
    private var strokeWidth: Int = 0
    private var strokeColor: Int = 0
    private var strokePressColor: Int = 0
    private var isRadiusHalfHeight: Boolean = false
    private var isWidthHeightEqual: Boolean = false
    private var isRippleEnable: Boolean = false
    private val radiusArr = FloatArray(8)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        obtainAttributes(context, attrs)
    }

    private fun obtainAttributes(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLinearLayout)
        backgroundColor = ta.getColor(R.styleable.RoundLinearLayout_rv_backgroundColor, Color.TRANSPARENT)
        backgroundPressColor = ta.getColor(R.styleable.RoundLinearLayout_rv_backgroundPressColor, Integer.MAX_VALUE)
        cornerRadius = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_cornerRadius, 0)
        strokeWidth = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_strokeWidth, 0)
        strokeColor = ta.getColor(R.styleable.RoundLinearLayout_rv_strokeColor, Color.TRANSPARENT)
        strokePressColor = ta.getColor(R.styleable.RoundLinearLayout_rv_strokePressColor, Integer.MAX_VALUE)
        isRadiusHalfHeight = ta.getBoolean(R.styleable.RoundLinearLayout_rv_isRadiusHalfHeight, false)
        isWidthHeightEqual = ta.getBoolean(R.styleable.RoundLinearLayout_rv_isWidthHeightEqual, false)
        cornerRadiusTL = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_cornerRadius_TL, 0)
        cornerRadiusTR = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_cornerRadius_TR, 0)
        cornerRadiusBL = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_cornerRadius_BL, 0)
        cornerRadiusBR = ta.getDimensionPixelSize(R.styleable.RoundLinearLayout_rv_cornerRadius_BR, 0)
        isRippleEnable = ta.getBoolean(R.styleable.RoundLinearLayout_rv_isRippleEnable, true)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isWidthHeightEqual() && width > 0 && height > 0) {
            val max = Math.max(width, height)
            val measureSpec = View.MeasureSpec.makeMeasureSpec(max, View.MeasureSpec.EXACTLY)
            super.onMeasure(measureSpec, measureSpec)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isRadiusHalfHeight()) {
            setCornerRadius(height / 2)
        } else {
            setBgSelector()
        }
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        setBgSelector()
    }

    fun setBackgroundPressColor(backgroundPressColor: Int) {
        this.backgroundPressColor = backgroundPressColor
        setBgSelector()
    }

    fun setCornerRadius(cornerRadius: Int) {
        this.cornerRadius = ScreenUtils.dip2px(context, cornerRadius.toFloat())
        setBgSelector()
    }

    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = ScreenUtils.dip2px(context, strokeWidth.toFloat())
        setBgSelector()
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        setBgSelector()
    }

    fun setStrokePressColor(strokePressColor: Int) {
        this.strokePressColor = strokePressColor
        setBgSelector()
    }

    fun setIsRadiusHalfHeight(isRadiusHalfHeight: Boolean) {
        this.isRadiusHalfHeight = isRadiusHalfHeight
        setBgSelector()
    }

    fun setIsWidthHeightEqual(isWidthHeightEqual: Boolean) {
        this.isWidthHeightEqual = isWidthHeightEqual
        setBgSelector()
    }

    fun setCornerRadius_TL(cornerRadius_TL: Int) {
        this.cornerRadiusTL = cornerRadius_TL
        setBgSelector()
    }

    fun setCornerRadius_TR(cornerRadius_TR: Int) {
        this.cornerRadiusTR = cornerRadius_TR
        setBgSelector()
    }

    fun setCornerRadius_BL(cornerRadius_BL: Int) {
        this.cornerRadiusBL = cornerRadius_BL
        setBgSelector()
    }

    fun setCornerRadius_BR(cornerRadius_BR: Int) {
        this.cornerRadiusBR = cornerRadius_BR
        setBgSelector()
    }

    private fun getBackgroundColor(): Int {
        return backgroundColor
    }

    private fun getBackgroundPressColor(): Int {
        return backgroundPressColor
    }

    private fun getCornerRadius(): Int {
        return cornerRadius
    }

    private fun getStrokeWidth(): Int {
        return strokeWidth
    }

    private fun getStrokeColor(): Int {
        return strokeColor
    }

    private fun getStrokePressColor(): Int {
        return strokePressColor
    }

    private fun isRadiusHalfHeight(): Boolean {
        return isRadiusHalfHeight
    }

    private fun isWidthHeightEqual(): Boolean {
        return isWidthHeightEqual
    }

    private fun getCornerRadius_TL(): Int {
        return cornerRadiusTL
    }

    private fun getCornerRadius_TR(): Int {
        return cornerRadiusTR
    }

    private fun getCornerRadius_BL(): Int {
        return cornerRadiusBL
    }

    private fun getCornerRadius_BR(): Int {
        return cornerRadiusBR
    }

    private fun setDrawable(gd: GradientDrawable, color: Int, strokeColor: Int) {
        gd.setColor(color)
        if (cornerRadiusTL > 0 || cornerRadiusTR > 0 || cornerRadiusBR > 0 || cornerRadiusBL > 0) {
            radiusArr[0] = cornerRadiusTL.toFloat()
            radiusArr[1] = cornerRadiusTL.toFloat()
            radiusArr[2] = cornerRadiusTR.toFloat()
            radiusArr[3] = cornerRadiusTR.toFloat()
            radiusArr[4] = cornerRadiusBR.toFloat()
            radiusArr[5] = cornerRadiusBR.toFloat()
            radiusArr[6] = cornerRadiusBL.toFloat()
            radiusArr[7] = cornerRadiusBL.toFloat()
            gd.cornerRadii = radiusArr
        } else {
            gd.cornerRadius = cornerRadius.toFloat()
        }
        gd.setStroke(strokeWidth, strokeColor)
    }

    private fun setBgSelector() {
        val bg = StateListDrawable()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isRippleEnable) {
            setDrawable(gdBackground, backgroundColor, strokeColor)
            val rippleDrawable = RippleDrawable(
                    getPressedColorSelector(backgroundColor, backgroundPressColor), gdBackground, null)
            this.background = rippleDrawable
        } else {
            setDrawable(gdBackground, backgroundColor, strokeColor)
            bg.addState(intArrayOf(-android.R.attr.state_pressed), gdBackground)
            if (backgroundPressColor != Integer.MAX_VALUE || strokePressColor != Integer.MAX_VALUE) {
                setDrawable(gdBackgroundPress, if (backgroundPressColor == Integer.MAX_VALUE) backgroundColor else backgroundPressColor,
                        if (strokePressColor == Integer.MAX_VALUE) strokeColor else strokePressColor)
                bg.addState(intArrayOf(android.R.attr.state_pressed), gdBackgroundPress)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//16
                this.background = bg
            } else {
                this.setBackgroundDrawable(bg)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun getPressedColorSelector(normalColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf(android.R.attr.state_activated), intArrayOf()),
                intArrayOf(pressedColor, pressedColor, pressedColor, normalColor)
        )
    }
}