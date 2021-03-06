package p1.p2;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

@SuppressWarnings("UnusedDeclaration")
public class MyView extends Button {
    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        int attribute = R.attr.newname;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        int answer = a.getInt(R.styleable.MyView_newname, 0);
        a.recycle();
    }
}
