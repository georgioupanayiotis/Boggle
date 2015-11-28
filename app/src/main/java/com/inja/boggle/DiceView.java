package com.inja.boggle;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A Custom TextView that adds identification for dice number.
 */
public class DiceView extends TextView {

    private int diceNumber;

    public DiceView(Context context) {
        super(context);
    }

    public DiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiceView);
        diceNumber = a.getInteger(R.styleable.DiceView_diceNumber,-1);
        a.recycle();
    }

    public DiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getDiceNumber()
    {
        return diceNumber;
    }

}
