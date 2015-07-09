package com.github.donkirkby.vograbulary;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.github.donkirkby.vograbulary.russian.LetterDisplay;

public class AndroidLetterDisplay extends LetterDisplay {
    private TextView textView;
    private boolean isInitialized;
    private int offsetX; // add to x to get leftMargin
    
    public AndroidLetterDisplay(TextView textView) {
        this.textView = textView;
        LayoutParams layoutParams = getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.insertImage);
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.spacer);
    }

    private LayoutParams getLayoutParams() {
        return (RelativeLayout.LayoutParams)textView.getLayoutParams();
    }

    private void checkOffset() {
        if ( ! isInitialized) {
            int[] location = new int[2];
            textView.getLocationOnScreen(location);
            offsetX = getLayoutParams().leftMargin - location[0];
            isInitialized = true;
        }
    }
    
    @Override
    public int getLeft() {
        checkOffset();
        return getLayoutParams().leftMargin - offsetX;
    }

    @Override
    public void setLeft(int left) {
        checkOffset();
        LayoutParams layoutParams = getLayoutParams();
        layoutParams.leftMargin = left + offsetX;
        textView.setLayoutParams(layoutParams);
    }

    @Override
    public int getWidth() {
        return textView.getWidth();
    }

    @Override
    public String getLetter() {
        return textView.getText().toString();
    }
    
    public TextView getTextView() {
        return textView;
    }
}