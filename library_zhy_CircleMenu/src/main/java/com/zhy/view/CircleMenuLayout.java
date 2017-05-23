package com.zhy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
    private String[] mItemImgs;

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
    private String[] mGoneIds;
    /**
     * 保存每个位置的角度值
     */
    private List<Double> mDoubleList = new ArrayList<Double>();
    /**
     * 存储对应图标对应的下标值
     */
    private Map<String, Integer> integerMap = new HashMap();
    /**
     * 子view的个数
     */
    private int mChildCount = 0;
    /**
     * 记录是否第一次加载
     */
    private boolean isFirst = true;
    private String[] allItem;
    private String[] visiabllAllItem;
    private float mAngleDelay;
    private int mPosition = 0;
    private OnScrollItemListener onScrollItemListener;
    private int childCount;
    private LayoutInflater mInflater;
    private int listPosition = 0;
    //view中心点X轴
    private int mCenterX;
    //view中心点Y轴
    private int mCenterY;
    //是否顺时针
    private boolean isClockwise = false;
    private List<View> mImageList = new ArrayList<View>();
    private int mRealPostion = 7;
    private int mItemPosition = 0;
    private Context mContext;
    private int placeHolderImg;
    private double angle = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (!isFling) {
                angelPerSecond = endAngle - mStartAngle;
                angle = angelPerSecond / 50;
                Log.e("delayAngle", "endAngle  " + endAngle + "   mStartAngle   " + mStartAngle + "   angelPerSecond   " + angelPerSecond + "   angle  " + angle);
            }

            if (angelPerSecond > 0) {
                //顺时针
                if (mStartAngle >= endAngle) {
                    mStartAngle = endAngle;
                    requestLayout();
                    isFling = false;
                    mHandler.removeCallbacksAndMessages(null);
                    return;
                }

            } else {
                //逆时针
                if (mStartAngle <= endAngle) {
                    mStartAngle = endAngle;
                    requestLayout();
                    isFling = false;
                    mHandler.removeCallbacksAndMessages(null);
                    return;
                }
            }
            isFling = true;
            mStartAngle += angle;
            Log.e("delayAngle", "   mStartAngle   " + mStartAngle);
            mHandler.sendEmptyMessageDelayed(0, 10);
            // 重新布局
            requestLayout();
        }
    };

    /**
     * MenuItem的点击事件接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private double angelPerSecond;
    private double endAngle;


    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
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

    public int getPlaceHolderImg() {
        return placeHolderImg;
    }

    public void setPlaceHolderImg(int placeHolderImg) {
        this.placeHolderImg = placeHolderImg;
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

        if ((mChildCount - 1) == 0 && mChildCount != 0)
            return;
        // Laying out the child views
        if (isFirst) {
            childCount = mChildCount;
        } else {
            childCount = mChildCount - 1;
        }

        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // 根据menu item的个数，计算角度
        mAngleDelay = 360f / (float) (childCount - 1);
        Log.e("delayAngle", "(childCount - 1)   " + (childCount - 1));
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
//            Log.e("delayAngle", "mStartAngle" + i + "   " + +mStartAngle);
            if (isFirst && i <= mRealPostion)
                mDoubleList.add(mStartAngle);

//            Log.e("angle", "mStartAngle   " + mStartAngle);
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
            Log.e("delayAngle", "mStartAngle    " + i + "   " + +mStartAngle);
        }
        for (int i = 0; i < mDoubleList.size(); i++) {
//            Log.e("TAG", "" + mDoubleList.get(i));
            if (mDoubleList.get(i) == 180) {
//                Log.e("TAG", "" + i);
                if (onScrollItemListener != null) {
                    onScrollItemListener.getItem(mPosition);
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
//            Log.e("measure", "mCenterX  " + mCenterX + "   mCenterY  " + mCenterY);
            cView.layout(cl, cl, cr, cr);
        }

        if (isFirst) {
            isFirst = false;
            if (mInflater != null) {
                View parentView = mInflater.inflate(mMenuItemLayoutId, this, false);
                final ImageView iv = (ImageView) parentView
                        .findViewById(R.id.id_circle_menu_item_image);
                if (iv != null) {
                    iv.setVisibility(View.INVISIBLE);
//                    if ("".equals(mGoneIds[0]))
//                        Glide.with(mContext).load(mGoneIds[0]).placeholder(placeHolderImg).error(placeHolderImg).into(new SimpleTarget<GlideDrawable>() {
//                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                            @Override
//                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                                iv.setBackground(resource);
//                            }
//                        });
//                    else
//                        iv.setBackgroundResource(placeHolderImg);
//                    iv.setTag(mGoneIds[0]);
//                    iv.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            if (mOnMenuItemClickListener != null) {
//                                mOnMenuItemClickListener.itemClick(v, integerMap.get(iv.getTag()));
//                            }
//                        }
//                    });
                }
                mImageList.add(parentView);
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
                    mHandler.removeCallbacksAndMessages(null);
                    isFling = false;
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //防止过多layout
                if (Math.abs(x - mLastX) < 3 || Math.abs(y - mLastY) < 3)
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
//                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                    isClockwise = true;
//                    Log.e("move", "end - start = " + (end - start));
                } else if (getQuadrant(x, y) == 2 || getQuadrant(x, y) == 3)
                // 二、三象限，色角度值是付值
                {
//                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                    isClockwise = true;
//                    Log.e("move", "start - end = " + (start - end));
                }
                mLastX = x;
                mLastY = y;
//                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                if (!isClockwise) {
                    return super.dispatchTouchEvent(event);
                }

                isClockwise = false;
                if (mTmpAngle >= 0) {
                    //顺时针

                    mItemPosition++;
//                    Log.e("mItemPosition", "mItemPosition  " + mItemPosition);
                    if (mItemPosition >= allItem.length + 1) {
                        mItemPosition--;
                        if (onScrollItemListener != null) {
                            onScrollItemListener.toEnd();
                        }
                        return true;
                    }
                    mPosition--;
                    listPosition++;

                    if (listPosition == mRealPostion) {
                        listPosition = 0;
                    }

                    positiveExchagePosition();
                } else {
                    //逆时针

                    mItemPosition--;
//                    Log.e("mItemPosition", "mItemPosition  " + mItemPosition + "  (allItem.length - mMenuItemCount) " + (allItem.length - mMenuItemCount));
                    if (mItemPosition <= (mMenuItemCount - 2)) {
                        mItemPosition++;
                        if (onScrollItemListener != null) {
                            onScrollItemListener.toStart();
                        }
                        return true;
                    }
                    mPosition++;
                    listPosition--;

                    if (listPosition == -1) {
                        listPosition = mRealPostion - 1;
                    }

                    nigetiveExchagePosition();
                }
//                Log.e("TAG", "mDoubleList.get(listPosition)  " + mDoubleList.get(listPosition));
//                if (!isFling) {
//                    //线性动画
//                    post(mFlingRunnable = new AutoFlingRunnable(mDoubleList.get(listPosition)));
//                    return true;
//                }
                endAngle = mDoubleList.get(listPosition);
                mHandler.sendEmptyMessage(0);
//                if (mDoubleList.size() > 0)
//                    mStartAngle = mDoubleList.get(listPosition);
//                // 重新布局
//                requestLayout();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 逆时针change数组和集合
     */
    private void nigetiveExchagePosition() {
        int j = 0;

        if (visiabllAllItem == null)
            return;
        String ints = visiabllAllItem[0];
        for (int i = 0; i < visiabllAllItem.length; i++) {
            if (i != visiabllAllItem.length - 1) {
                visiabllAllItem[i] = visiabllAllItem[i + 1];
            } else {
                visiabllAllItem[visiabllAllItem.length - 1] = ints;
            }
        }

//        Log.e("TAG", "mImageList.size()  " + mImageList.size());
        for (int i = 0; i < mImageList.size(); i++) {
            View view = mImageList.get(i);
            final ImageView iv = (ImageView) view
                    .findViewById(R.id.id_circle_menu_item_image);
            if (i == 0) {
                iv.setVisibility(View.INVISIBLE);
            } else {
                iv.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(visiabllAllItem[j++]).placeholder(placeHolderImg).error(placeHolderImg).into(new SimpleTarget<GlideDrawable>() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Log.e("TAG", "" + (resource == null));
                        iv.setBackground(resource);
                    }
                });
                iv.setTag(visiabllAllItem[j]);
                iv.setOnClickListener(null);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, integerMap.get(iv.getTag()));
                        }
                    }
                });

            }
        }
        View view = mImageList.get(0);
        for (int i = 0; i < mImageList.size(); i++) {
            if (i != mImageList.size() - 1) {
                mImageList.set(i, mImageList.get(i + 1));
            } else {
                mImageList.set(mImageList.size() - 1, view);
            }
        }
    }

    /**
     * 顺时针change数组和集合
     */
    private void positiveExchagePosition() {
        int j = 0;
        if (visiabllAllItem == null)
            return;
        String ints = visiabllAllItem[visiabllAllItem.length - 1];
        for (int i = 1; i < visiabllAllItem.length; i++) {
            visiabllAllItem[visiabllAllItem.length - i] = visiabllAllItem[visiabllAllItem.length - i - 1];
        }
        visiabllAllItem[0] = ints;

        View views = mImageList.get(mImageList.size() - 1);
        for (int i = 1; i < mImageList.size(); i++) {
            mImageList.set(mImageList.size() - i, mImageList.get(mImageList.size() - i - 1));
        }
        mImageList.set(0, views);

        for (int i = 0; i < mImageList.size(); i++) {
            View view = mImageList.get(i);
            final ImageView iv = (ImageView) view
                    .findViewById(R.id.id_circle_menu_item_image);
            if (i == mImageList.size() - 1) {
                iv.setVisibility(View.INVISIBLE);
            } else {
                iv.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(visiabllAllItem[j++]).placeholder(placeHolderImg).error(placeHolderImg).into(new SimpleTarget<GlideDrawable>() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Log.e("TAG", "" + (resource == null));
                        iv.setBackground(resource);
                    }
                });
                iv.setTag(visiabllAllItem[j]);
                iv.setOnClickListener(null);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, integerMap.get(iv.getTag()));
                        }
                    }
                });
            }
        }
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
    public void setMenuItemIconsAndTexts(String[] resIds, String[] goneIds) {
        mItemImgs = resIds;
        mGoneIds = goneIds;

        //复制数组到新数组
        copyItemToAll();
        // 参数检查
        if (resIds == null) {
            throw new IllegalArgumentException("菜单项至少设置七个");
        }
        if (goneIds.length < 1) {
            throw new IllegalArgumentException("隐藏视图至少设置一个");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds.length;

        setItemPosition();
        addMenuItems();
    }

    /**
     * 复制数组到新数组
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void copyItemToAll() {
        int i = 0;
        allItem = new String[mItemImgs.length + mGoneIds.length - 1];
        for (; i < mItemImgs.length - 1; i++) {
            allItem[i] = mItemImgs[i];
        }
        for (int j = 0; j < mGoneIds.length; j++, i++) {
            allItem[i] = mGoneIds[j];
        }
        visiabllAllItem = Arrays.copyOf(allItem, allItem.length);
        Log.e("TAG", "" + visiabllAllItem.length);
        mItemPosition = allItem.length;
    }

    /**
     * 设置对应图片的对应下标值
     */
    private void setItemPosition() {
        integerMap.clear();
        for (int i = 0; i < visiabllAllItem.length; i++) {
            integerMap.put(visiabllAllItem[i], i);
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
        mImageList.clear();
        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View parentView = mInflater.inflate(mMenuItemLayoutId, this, false);
            final ImageView iv = (ImageView) parentView
                    .findViewById(R.id.id_circle_menu_item_image);
            //存储ImageView
            if (i != mMenuItemCount - 1)
                mImageList.add(parentView);
            if (iv != null) {
                iv.setVisibility(View.VISIBLE);

                iv.setTag(mItemImgs[i]);
                if (i == mMenuItemCount - 1) {
                    iv.setBackgroundResource(R.drawable.more);
                    iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (onScrollItemListener != null) {
                                onScrollItemListener.more();
                            }
                        }
                    });
                } else {
                    Glide.with(mContext).load(mItemImgs[i]).placeholder(placeHolderImg).into(new SimpleTarget<GlideDrawable>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            iv.setBackground(resource);
                        }
                    });
                    iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mOnMenuItemClickListener != null) {
                                mOnMenuItemClickListener.itemClick(v, integerMap.get(iv.getTag()));
                            }
                        }
                    });
                }
            }

            // 添加view到容器中
            addView(parentView);
        }
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
}
