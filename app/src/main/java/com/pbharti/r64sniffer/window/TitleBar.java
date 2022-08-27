package com.pbharti.r64sniffer.window;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.utils.ResTools;
import com.pbharti.r64sniffer.utils.ScreenUtils;

public class TitleBar extends LinearLayout {

    private final TextView mTitle;

    private final LinearLayout mRightCnt;


    public TitleBar(Context context) {
        super(context);

        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setMinimumHeight(ScreenUtils.dp2px(55));
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        this.setBackgroundResource(R.color.black);

        int textSize = (int) ResTools.getDimen(R.dimen.textsize1);
        mTitle = new TextView(getContext());
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTitle.setTextColor(ResTools.getColor(R.color.background));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
        this.addView(mTitle, lp);

        mRightCnt = new LinearLayout(context);
        mRightCnt.setOrientation(LinearLayout.HORIZONTAL);
        mRightCnt.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        mRightCnt.setBackgroundColor(Color.TRANSPARENT);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.RIGHT;
        lp.weight = 1;

        this.addView(mRightCnt, lp);

    }

    public View getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTitle.setText(resId);
    }

    public void addRight(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
        mRightCnt.addView(view, lp);
    }
}
