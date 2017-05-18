package com.zhy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zhy.ccbCricleMenu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * @author zhy
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleMenuLayout extends ViewGroup {
    private int mRadius;
    /**
     * 该容器内child item的默认尺寸
     */
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    /**
     * 菜单的中心child的默认尺寸
     */
    private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private static final int FLINGABLE_VALUE = 300;

    /**
     * 如果移动角度达到该值，则屏蔽点击
     */
    private static final int NOCLICK_VALUE = 3;

    /**
     * 当每秒移动角度达到该值时，认为是快速移动
     */
    private int mFlingableValue = FLINGABLE_VALUE;
    /**
     * 该容器的内边距,无视padding属性，如需边距请用该变量
     */
    private float mPadding;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 180;
    /**
     * 菜单显示项的图标
     */
    private int[] mItemImgs;

    /**
     * 菜单的个数
     */
    private int mMenuItemCount;

    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;
    /**
     * 检测按下到抬起时使用的时间
     */
    private long mDownTime;

    /**
     * 判断是否正在自动滚动
     */
    private boolean isFling;

    private int mMenuItemLayoutId = R.layout.circle_menu_item;
    /**
     * 菜单隐藏项的图标
     */
    private int[] mGoneIds;
    /**
     * 保存每个位置的角度值
     */
    private List<Double> mDoubleList = new ArrayList<Double>();
    /**
     * 存储对应图标对应的下标值
     */
    private Map<Integer, Integer> integerMap = new HashMap();
    /**
     * 子view的个数
     */
    private int mChildCount;
    /**
     * 记录是否第一次加载
     */
    private boolean isFirst = true;
    private int[] allItem;
    private float mAngleDelay;
    private int mPosition;
    private OnScrollItemListener onScrollItemListener;
    private int childCount;
    private LayoutInflater mInflater;
    private int listPosition = 0;
    //view中心点X轴
    private int mCenterX;
    //view中心点Y轴
    private int mCenterY;
    //是否顺时针
    private boolean isClockwise = true;

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
    }

    public void setOnScrollItemListener(OnScrollItemListener onScrollItemListener) {
        this.onScrollItemListener = onScrollItemListener;
    }

    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽或者高的测量模式非精确值
         */
//        if (widthMode != MeasureSpec.EXACTLY
//                || heightMode != MeasureSpec.EXACTLY) {
//            // 主要设置为背景图的高度
//            resWidth = getSuggestedMinimumWidth();
//            // 如果未设置背景图片，则设置为屏幕宽高的默认值
//            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
//
//            resHeight = getSuggestedMinimumHeight();
//            // 如果未设置背景图片，则设置为屏幕宽高的默认值
//            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
//        } else {
        // 如果都设置为精确值，则直接取小值；
        resWidth = resHeight = Math.min(width, height);
//        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量
        mChildCount = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;

        // 迭代测量
        for (int i = 0; i < mChildCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
                        childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
                        childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }

        mPadding = RADIO_PADDING_LAYOUT * mRadius;

    }

    /**
     * MenuItem的点击事件接口
     *
     * @author zhy
     */
    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    /**
     * MenuItem的点击事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    /**
     * 设置MenuItem的点击事件接口
     *
     * @param mOnMenuItemClickListener
     */
    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;

        // Laying out the child views
        if (isFirst) {
            childCount = mChildCount;
            // 根据menu item的个数，计算角度
            mAngleDelay = 360 / (childCount - 1);
        } else {
            childCount = mChildCount - 1;
            // 根据menu item的个数，计算角度
            mAngleDelay = 360 / childCount;
        }


        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // 根据menu item的个数，计算角度
        mAngleDelay = 360 / (childCount - 1);

        if (isFirst)
            mDoubleList.clear();

        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getId() == R.id.id_circle_menu_item_center)
                continue;

            if (child.getVisibility() == GONE) {
                continue;
            }

            mStartAngle %= 360;
            if (isFirst)
                mDoubleList.add(mStartAngle);

            Log.e("angle", "mStartAngle   " + mStartAngle);
            // 计算，中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);
            // tmp sina 即menu item的纵坐标
            top = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);

            child.layout(left, top, left + cWidth, top + cWidth);
//            if (mStartAngle >= 180 - mAngleDelay && mStartAngle <= 180 - mAngleDelay / 2 && i != childCount - 1)
//                mStartAngle -= mAngleDelay * 2;
//            else
            mStartAngle += mAngleDelay;
        }
        for (int i = 0; i < mDoubleList.size(); i++) {
            if (mDoubleList.get(i) <= 180 + mAngleDelay / 2 && mDoubleList.get(i) >= 180 - mAngleDelay / 2) {
                if (mPosition != i) {
                    mPosition = i;
//                    Log.e("TAG", "" + mPosition);
                    if (onScrollItemListener != null) {
                        onScrollItemListener.getItem(mPosition);
                    }
                }
            }
        }
        //设置显示更多
//        View child = getChildAt(childCount - 1);
//        View moreView = findViewById(R.id.id_circle_menu_item_more);
//        moreView.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        // 找到中心的view，如果存在设置onclick事件
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null) {
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            // 设置center item位置
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            mCenterX = cr - cl;
            mCenterY = cr - cl;
            Log.e("measure", "mCenterX  " + mCenterX + "   mCenterY  " + mCenterY);
            cView.layout(cl, cl, cr, cr);
        }

        if (isFirst) {
            isFirst = false;
            if (mInflater != null) {
                View parentView = mInflater.inflate(mMenuItemLayoutId, this, false);
                ImageView iv = (ImageView) parentView
                        .findViewById(R.id.id_circle_menu_item_image);
                if (iv != null) {
                    iv.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(mItemImgs[0]);
                    iv.setTag(mItemImgs[0]);
                    iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mOnMenuItemClickListener != null) {
                                mOnMenuItemClickListener.itemClick(v, childCount - 1);
                            }
                        }
                    });
                }
                // 添加view到容器中
                addView(parentView, childCount - 1);
            }
        }
    }

    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;

    /**
     * 自动滚动的Runnable
     */
    private AutoFlingRunnable mFlingRunnable;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isFling) {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //防止过多layout
                if (Math.abs(x - mLastX) < 2 || Math.abs(y - mLastY) < 2)
                    return true;
                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);


                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                    isClockwise = true;
                    Log.e("move", "end - start = " + (end - start));
                } else if (getQuadrant(x, y) == 2 || getQuadrant(x, y) == 3)
                // 二、三象限，色角度值是付值
                {
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                    isClockwise = false;
                    Log.e("move", "start - end = " + (start - end));
                }
                // 重新布局
                requestLayout();
                float totalX = x - mLastX;
                float totalY = y - mLastY;

                mLastX = x;

                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                if (isClockwise) {
                    post(mFlingRunnable = new AutoFlingRunnable(mAngleDelay));
                } else {
                    post(mFlingRunnable = new AutoFlingRunnable(-mAngleDelay));
                }
//                // 如果达到该值认为是快速移动
//                if (!isFling) {
//                    listPosition = (++listPosition) % 6;
//                    // post一个任务，去自动滚动
//                    post(mFlingRunnable = new AutoFlingRunnable(mDoubleList.get(listPosition).floatValue()));
//
//                    return true;
//                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, int[] goneIds) {
        mItemImgs = resIds;
        mGoneIds = goneIds;
        //复制数组到新数组
        copyItemToAll();
        // 参数检查
        if (resIds == null) {
            throw new IllegalArgumentException("菜单项至少设置六个");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds.length;

        addMenuItems();
        setItemPosition();
    }

    /**
     * 复制数组到新数组
     */
    private void copyItemToAll() {
        int i = 0;
        allItem = new int[mItemImgs.length + mGoneIds.length];
        for (; i < mItemImgs.length; i++) {
            allItem[i] = mItemImgs[i];
        }
        for (int j = 0; j < mGoneIds.length; j++, i++) {
            allItem[i] = mGoneIds[j];
        }
    }

    /**
     * 设置对应图片的对应下标值
     */
    private void setItemPosition() {
        integerMap.clear();
        for (int i = 0; i < mItemImgs.length + mGoneIds.length; i++) {
            integerMap.put(allItem[i], i);
        }
    }

    /**
     * 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
     *
     * @param mMenuItemLayoutId
     */
    public void setMenuItemLayoutId(int mMenuItemLayoutId) {
        this.mMenuItemLayoutId = mMenuItemLayoutId;
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View parentView = mInflater.inflate(mMenuItemLayoutId, this, false);
            ImageView iv = (ImageView) parentView
                    .findViewById(R.id.id_circle_menu_item_image);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setBackgroundResource(mItemImgs[i]);
                iv.setTag(mItemImgs[i]);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }

            // 添加view到容器中
            addView(parentView);
        }

//		for (int i = 0; i < mGoneIds.length; i++) {
//			final int j = i;
//			View view = mInflater.inflate(mMenuItemLayoutId, this, false);
//			ImageView iv = (ImageView) view
//					.findViewById(R.id.id_circle_menu_item_image);
//
//			if (iv != null)
//			{
//				iv.setVisibility(View.GONE);
//				iv.setImageResource(mGoneIds[i]);
//				iv.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v)
//					{
//
//						if (mOnMenuItemClickListener != null)
//						{
//							mOnMenuItemClickListener.itemClick(v, j);
//						}
//					}
//				});
//			}
//
//			// 添加view到容器中
//			addView(view);
//		}
    }

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param mFlingableValue
     */
    public void setFlingableValue(int mFlingableValue) {
        this.mFlingableValue = mFlingableValue;
    }

    /**
     * 设置内边距的比例
     *
     * @param mPadding
     */
    public void setPadding(float mPadding) {
        this.mPadding = mPadding;
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 自动滚动的任务
     *
     * @author zhy
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            // 如果小于20,则停止

            if (listPosition == 0) {
                listPosition = childCount - 1;
            }
            Log.e("TAG", "(int) Math.abs(angelPerSecond) = " + (int) Math.abs(angelPerSecond) + " , mDoubleList.get(listPosition - 1) =" + mDoubleList.get(listPosition - 1) + " , mlistPosition =" + listPosition);
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
            mStartAngle += angelPerSecond;
            isFling = false;
            // 重新布局
            requestLayout();
        }
    }

}
