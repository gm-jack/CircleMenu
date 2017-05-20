package com.sample_circlemenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhy.view.CircleMenuLayout;
import com.zhy.view.CircleMenuLayout.OnMenuItemClickListener;
import com.zhy.view.OnScrollItemListener;

/**
 * <pre>
 * @author zhy
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleActivity extends Activity {

    private CircleMenuLayout mCircleMenuLayout;

    private String[] mItemTexts = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "信用卡"};
    private int[] mItemImgs = new int[]{R.drawable.postion1,
            R.drawable.postion2, R.drawable.postion3,
            R.drawable.postion4, R.drawable.postion5,
            R.drawable.postion6, R.drawable.more};
    private int[] mGoneImgs = new int[]{R.drawable.postion1,
            R.drawable.postion2, R.drawable.postion3,
            R.drawable.postion4, R.drawable.postion5,
            R.drawable.postion6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main02);

        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mGoneImgs);


        mCircleMenuLayout.setOnScrollItemListener(new OnScrollItemListener() {
            @Override
            public void getItem(int position) {
                Toast.makeText(CircleActivity.this, "左端    " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void toStart() {
                Toast.makeText(CircleActivity.this, "开始",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void toEnd() {
                Toast.makeText(CircleActivity.this, "结束",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void more() {
                Toast.makeText(CircleActivity.this, "more",
                        Toast.LENGTH_SHORT).show();
            }
        });
        mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CircleActivity.this, "点击   " + pos,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(CircleActivity.this,
                        "you can do something just like ccb  ",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

}
