package com.sample_circlemenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.view.CircleMenuLayout;
import com.zhy.view.CircleMenuLayout.OnMenuItemClickListener;
import com.zhy.view.Model;
import com.zhy.view.OnScrollItemListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * <pre>
 * @author zhy
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleActivity extends Activity {

    private CircleMenuLayout mCircleMenuLayout;
    private int count = 7;
    private List<String> mShowImgUrl = new ArrayList<String>();
    private String[] mItemImg = new String[count];
    private String[] mGoneImg;
    String url = "http://www.upstudio.top/ringhelper/sceneController/getSceneInfoApp.do?userMobile=15833941513";
    private String none = "";
    private int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main02);

        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);


        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("http", "response   " + response.toString());
                        try {
                            Model model = new Gson().fromJson(response, Model.class);
                            if ("1".equals(model.getResultCode())) {
                                List<Model.ResultBean> result = model.getResult();

                                mShowImgUrl.clear();

                                for (int i = 0; i < result.size(); i++) {
                                    String imgsUrl = Contacts.URL_HEAD + result.get(i).getIcon();
                                    if (i <= (count - 2)) {
                                        mShowImgUrl.add(imgsUrl);
                                        mItemImg[i] = imgsUrl;
                                    } else if (i == (count - 1)) {
                                        mShowImgUrl.add("");
                                        mItemImg[i] = "";
                                    }
                                }
                                if (result.size() <= 6 && result.size() > 0) {
                                    mGoneImg = new String[result.size()];
                                    for (int i = result.size() - 1; i >= 0; i--) {
                                        mGoneImg[j++] = mShowImgUrl.get(i);
                                    }
                                } else {
                                    mGoneImg = new String[]{"", "", "", "", ""};
                                }

                                mCircleMenuLayout.setPlaceHolderImg(R.drawable.ic_launcher);
                                mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImg, mGoneImg);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mCircleMenuLayout.setOnScrollItemListener(new OnScrollItemListener() {
            @Override
            public void getItem(int position) {
                Toast.makeText(CircleActivity.this, "左端   " + position,
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
