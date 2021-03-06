package ch.modeso.mcompoundquestionnaire

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.animation.AnimatorCompatHelper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by Hazem on 7/28/2017.
 */
class MCompoundQuestionnaire : LinearLayout, CardInteractionCallbacks {

    var indicatorBackgroundColor: Int = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        set(value) {
            field = value
            questionnaireIndicator?.bgColor = value
        }

    var indicatorUpperColor: Int = ContextCompat.getColor(context, android.R.color.transparent)
        set(value) {
            field = value
            questionnaireIndicator?.upperColor = value
        }

    var indicatorLowerColor: Int = ContextCompat.getColor(context, android.R.color.transparent)
        set(value) {
            field = value
            questionnaireIndicator?.lowerColor = value
        }

    var indicatorIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_indicator)
        set(drawable) {
            field = drawable
            questionnaireIndicator?.indicator = drawable
        }

    var indicatorFraction: Float = 2.5f
        set(value) {
            field = value
            invalidate()
            questionnaireIndicator?.fraction = value
        }

    var cardTextColor: Int = ContextCompat.getColor(context, R.color.colorAccent)
        set(value) {
            field = value
            demoAdapter?.cardTextColor = value
        }
    var acceptColor: Int = ContextCompat.getColor(context, R.color.colorAccept)
        set(value) {
            field = value
            demoAdapter?.acceptColor = value
        }

    var cancelColor: Int = ContextCompat.getColor(context, R.color.colorCancel)
        set(value) {
            field = value
            demoAdapter?.cancelColor = value
        }

    var cardBackgroundDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.card_bg)
        set(value) {
            field = value
            demoAdapter?.cardBackgroundDrawable = value
        }

    var notApplicableColor: Int = ContextCompat.getColor(context, R.color.colorNotApplicable)
        set(value) {
            field = value
            demoAdapter?.notApplicableColor = value
        }

    var acceptDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_check)
        set(value) {
            field = value
            demoAdapter?.acceptDrawable = value
        }

    var cancelDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_close)
        set(value) {
            field = value
            demoAdapter?.cancelDrawable = value
        }

    var notApplicableDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_not_applicable)
        set(value) {
            field = value
            demoAdapter?.notApplicableDrawable = value
        }
    var notApplicableArrowDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_downward)
        set(value) {
            field = value
            invalidate()
        }

    var cardInteractionCallBacks: CardInteractionCallbacks? = null

    var questionnaireIndicator: QuestionnaireIndicator? = null
    var recyclerView: RecyclerView? = null
    var demoAdapter: DemoAdapter? = null
    val bottomFrame = FrameLayout(context)
    private lateinit var itemTouchHelper: CustomItemTouchHelper
    val tileManager = TileLayoutManager()
    var isUnDismiss = false
    val textView = TextView(this.context)
    val textPaint = TextPaint()
    var dismissNo = 0
    private var items: MutableList<BaseModel> = mutableListOf()

    private val density = context.resources.displayMetrics.density
    private val progressBarSize = (24 * density).toInt()

    private val topPadding = (30 * density).toInt()
    private val bottomView = (30 * density) + 20 * density

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr, defStyleRes)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        setWillNotDraw(false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MCompoundQuestionnaire, defStyleAttr, defStyleRes)
        indicatorIcon = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqIndicatorDrawableIcon) ?: indicatorIcon
        indicatorFraction = typedArray.getFloat(R.styleable.MCompoundQuestionnaire_mcqIndicatorSizeFraction, indicatorFraction)
        indicatorUpperColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqIndicatorUpperColor, indicatorUpperColor)
        indicatorLowerColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqIndicatorLowerColor, indicatorLowerColor)
        indicatorBackgroundColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqIndicatorBackgroundColor, indicatorBackgroundColor)
        cardTextColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqCardTextColor, cardTextColor)
        acceptColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqAcceptColor, acceptColor)
        cancelColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqCancelColor, cancelColor)
        notApplicableColor = typedArray.getColor(R.styleable.MCompoundQuestionnaire_mcqNotApplicableColor, notApplicableColor)
        cardBackgroundDrawable = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqCardBackgroundDrawable) ?: cardBackgroundDrawable
        acceptDrawable = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqAcceptDrawable) ?: acceptDrawable
        cancelDrawable = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqCancelDrawable) ?: cancelDrawable
        notApplicableDrawable = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqNotApplicableDrawable) ?: notApplicableDrawable
        notApplicableArrowDrawable = typedArray.getDrawable(R.styleable.MCompoundQuestionnaire_mcqNotApplicableArrowDrawable) ?: notApplicableArrowDrawable
        typedArray.recycle()

        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        if (questionnaireIndicator == null) {
            initProgressBar()
        }
        if (recyclerView == null) {
            initRecyclerView()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        textView.layoutParams = this.layoutParams
    }

    private fun initProgressBar() {
        questionnaireIndicator = QuestionnaireIndicator(context)
        questionnaireIndicator?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, progressBarSize)
        questionnaireIndicator?.fraction = indicatorFraction
        questionnaireIndicator?.lowerColor = indicatorLowerColor
        questionnaireIndicator?.upperColor = indicatorUpperColor
        questionnaireIndicator?.bgColor = indicatorBackgroundColor
        questionnaireIndicator?.indicator = indicatorIcon
        questionnaireIndicator?.colorListAddAll(items.map { getCardColor(it.status) })
        addView(questionnaireIndicator)
    }

    private fun initRecyclerView() {
        recyclerView = RecyclerView(context)
        recyclerView?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        recyclerView?.setPadding(0, topPadding, 0, 0)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        val demoList = mutableListOf<BaseModel>()
        demoList.addAll(items)
        demoAdapter = DemoAdapter(context, this, progressBarSize + topPadding + (bottomView * 1.5f), demoList, cardTextColor, acceptColor, cancelColor, notApplicableColor, cardBackgroundDrawable, acceptDrawable, cancelDrawable, notApplicableDrawable, bottomFrame)
        tileManager.attach(recyclerView, 0)
//        demoAdapter?.setOnItemClickListener(object : DemoAdapter.OnItemClickListener {
//            override fun onItemClick(view: View, position: Int) {
//                recyclerView?.smoothScrollToPosition(position)
//            }
//
//        })
        tileManager.setOnItemSelectedListener(object : TileLayoutManager.OnItemSelectedListener {
            override fun onItemSelected(recyclerView: RecyclerView, item: View, position: Int) {
                if (demoAdapter != null) {
                    val modelItem = demoAdapter!!.items[position]
                    val realItem = items.find { it.id.contentEquals(modelItem.id) }
                    if (realItem != null) {
                        val realIndex = items.indexOf(realItem)
                        if (realIndex > -1) {
                            questionnaireIndicator?.currentPosition = realIndex
                        }
                    }
                }
            }

        })
        itemTouchHelper = CustomItemTouchHelper(demoAdapter, bottomView, tileManager)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView?.adapter = demoAdapter
        val frameContainer: FrameLayout = FrameLayout(context)
        frameContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.BOTTOM
        bottomFrame.layoutParams = layoutParams
        bottomFrame.setPadding(0, topPadding, 0, 0)
        frameContainer.addView(recyclerView)
        frameContainer.addView(bottomFrame)
        addView(frameContainer)
    }

    override fun itemAcceptClick(itemId: String) {
        if (demoAdapter != null) {
            val realItem = items.find { it.id.contentEquals(itemId) }
            if (realItem != null) {
                val realIndex = items.indexOf(realItem)
                if (realIndex > -1) {
                    questionnaireIndicator?.changeColorAtPosition(realIndex, acceptColor)
                    items[realIndex].status = QuestionnaireCardView.CardStatus.ACCEPTED
                }
            }
            val adapterItem = demoAdapter!!.items.find { it.id.contentEquals(itemId) }
            if (adapterItem != null) {
                val adapterIndex = demoAdapter!!.items.indexOf(adapterItem)
                if (adapterIndex > -1) {
                    demoAdapter!!.items[adapterIndex].status = QuestionnaireCardView.CardStatus.ACCEPTED
                }
            }
        }
        cardInteractionCallBacks?.itemAcceptClick(itemId)
    }

    override fun itemCancelClick(itemId: String) {
        if (demoAdapter != null) {
            val realItem = items.find { it.id.contentEquals(itemId) }
            if (realItem != null) {
                val realIndex = items.indexOf(realItem)
                if (realIndex > -1) {
                    questionnaireIndicator?.changeColorAtPosition(realIndex, cancelColor)
                    items[realIndex].status = QuestionnaireCardView.CardStatus.CANCELED
                }
            }
            val adapterItem = demoAdapter!!.items.find { it.id.contentEquals(itemId) }
            if (adapterItem != null) {
                val adapterIndex = demoAdapter!!.items.indexOf(adapterItem)
                if (adapterIndex > -1) {
                    demoAdapter!!.items[adapterIndex].status = QuestionnaireCardView.CardStatus.CANCELED
                }
            }
        }
        cardInteractionCallBacks?.itemCancelClick(itemId)
    }

    override fun itemNone(itemId: String) {
        if (demoAdapter != null) {
            val realItem = items.find { it.id.contentEquals(itemId) }
            if (realItem != null) {
                val realIndex = items.indexOf(realItem)
                if (realIndex > -1) {
                    questionnaireIndicator?.changeColorAtPosition(realIndex, indicatorBackgroundColor)
                    items[realIndex].status = QuestionnaireCardView.CardStatus.NONE
                }
            }
            val adapterItem = demoAdapter!!.items.find { it.id.contentEquals(itemId) }
            if (adapterItem != null) {
                val adapterIndex = demoAdapter!!.items.indexOf(adapterItem)
                if (adapterIndex > -1) {
                    demoAdapter!!.items[adapterIndex].status = QuestionnaireCardView.CardStatus.NONE
                }
            }
        }
        cardInteractionCallBacks?.itemNone(itemId)
    }

    fun updateList(itemsList: MutableList<BaseModel>) {
        items = itemsList
        val demoList = mutableListOf<BaseModel>()
        demoList.addAll(items)
        demoAdapter?.items = demoList
        demoAdapter?.notifyDataSetChanged()
        questionnaireIndicator?.colorListAddAll(items.map { getCardColor(it.status) })
    }

    private fun getCardColor(cardStatus: QuestionnaireCardView.CardStatus): Int {
        when (cardStatus) {
            QuestionnaireCardView.CardStatus.NONE -> return indicatorBackgroundColor
            QuestionnaireCardView.CardStatus.ACCEPTED -> return acceptColor
            QuestionnaireCardView.CardStatus.CANCELED -> return cancelColor
            QuestionnaireCardView.CardStatus.NOT_APPLICABLE -> return notApplicableColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (dismissNo == 0) {
            notApplicableArrowDrawable.bounds.set(
                    (measuredWidth / 2 - 2 * progressBarSize / 3).toInt(),
                    (measuredHeight - bottomView - 3 * progressBarSize / 4).toInt(),
                    (measuredWidth / 2 + 2 * progressBarSize / 3).toInt(),
                    (measuredHeight - bottomView + progressBarSize / 4).toInt()
            )
            notApplicableArrowDrawable.draw(canvas)

            textView.isDrawingCacheEnabled = true
            textView.setTextColor(notApplicableColor)
            textView.textSize = 16f
            textView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            textView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY)
                    , View.MeasureSpec.makeMeasureSpec((bottomView).toInt(), View.MeasureSpec.AT_MOST))
            textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
            textView.text = context.getString(R.string.not_applicable)
            textView.typeface = Typeface.DEFAULT_BOLD
            if (textView.drawingCache != null) {
                canvas?.drawBitmap(textView.drawingCache, 0f, measuredHeight - bottomView, textPaint)
            }
            textView.isDrawingCacheEnabled = false
        }
    }

    override fun itemDismiss(itemId: String) {
        dismissNo++
        if (demoAdapter != null) {
            val realItem = items.find { it.id.contentEquals(itemId) }
            if (realItem != null) {
                val realIndex = items.indexOf(realItem)
                if (realIndex > -1) {
                    questionnaireIndicator?.changeColorAtPosition(realIndex, notApplicableColor)
                }
                realItem.status = QuestionnaireCardView.CardStatus.NOT_APPLICABLE
            }
        }
        invalidate()
        cardInteractionCallBacks?.itemDismiss(itemId)
    }

    fun onItemUnDismiss(view: View) {
        if (isUnDismiss) return
        isUnDismiss = true
        val id = view.tag as String
        val realItem = items.find { it.id == id }
        if (realItem != null && realItem.status == QuestionnaireCardView.CardStatus.NOT_APPLICABLE) {
            val realIndex = items.indexOf(realItem)
            if (realIndex > -1) {
                questionnaireIndicator?.changeColorAtPosition(realIndex, indicatorBackgroundColor)
            }
            realItem.status = QuestionnaireCardView.CardStatus.NONE
            (view.parent as ViewGroup).removeView(view)
            dismissNo--
            val position = items.filter { it.status != QuestionnaireCardView.CardStatus.NOT_APPLICABLE }.indexOf(realItem)
            if (tileManager.mCurSelectedPosition == position) {
                demoAdapter?.items?.add(position, realItem)
                isUnDismiss = false
                cardInteractionCallBacks?.itemNone(id)
                demoAdapter?.notifyItemInserted(position)
            } else {
                recyclerView?.smoothScrollToPosition(position)
                val scrollListener = ScrolledListener(position, realItem, demoAdapter) {
                    isUnDismiss = false
                    cardInteractionCallBacks?.itemNone(id)
                }
                recyclerView?.addOnScrollListener(scrollListener)
            }
            itemTouchHelper.dismissedNo--
            redrawDismissedChild()

        }
    }

    fun redrawDismissedChild(initialY: Float? = null, rotation: Float? = null) {
        for (i in 0..bottomFrame.childCount - 1) {
            val view = bottomFrame.getChildAt(i)
            val animatorCompat = AnimatorCompatHelper.emptyValueAnimator()
            animatorCompat.setDuration(300)
            val interpolator = DecelerateInterpolator()
            val widthRightPart = bottomView + Math.sqrt(Math.pow(view.measuredWidth - Math.sqrt(2 * Math.pow(bottomView.toDouble(), 2.0)), 2.0) / 2).toFloat()
            val widthLeftPart = bottomView + Math.sqrt(Math.pow(view.measuredHeight - Math.sqrt(2 * Math.pow(bottomView.toDouble(), 2.0)), 2.0) / 2).toFloat()
            val def = (widthRightPart + widthLeftPart - view.measuredWidth) / 2
            val deltaX = view.measuredWidth - (widthRightPart - def) - bottomView * (i + 1)
            val viewX = view.x
            val viewY = view.y
            val viewR = view.rotation
            animatorCompat.addUpdateListener { animation ->
                val fraction = interpolator.getInterpolation(animation.animatedFraction)
                val interpolatedValue = viewX - (viewX + deltaX) * fraction
                view.x = (interpolatedValue)
                if (initialY != null) {
                    view.y = (initialY - viewY) * fraction + viewY
                }
                if (rotation != null) {
                    view.rotation = (rotation - viewR) * fraction + viewR
                }
                if (view is QuestionnaireCardView) {
                    view.movingHorizontal = true
                    view.cardMoving = false
                }
            }
            animatorCompat.start()
        }
    }

    private class ScrolledListener(val position: Int, val realItem: BaseModel, val demoAdapter: DemoAdapter?, val done: () -> Unit) : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                demoAdapter?.items?.add(position, realItem)
                done()
                demoAdapter?.notifyItemInserted(position)
                recyclerView?.removeOnScrollListener(this)
            }
        }
    }
}