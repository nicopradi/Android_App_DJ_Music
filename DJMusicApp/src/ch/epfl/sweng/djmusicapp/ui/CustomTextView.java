package ch.epfl.sweng.djmusicapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import ch.epfl.sweng.djmusicapp.utils.FontUtils;

/**
 * Custom TextView. Sets the custom font to the TextViews.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);

        init(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    /**
     * Every constructor of this class calls this method
     * 
     * @param context
     */
    private void init(Context context) {

        setTypeface(FontUtils.robotoLight(context));
    }

    public void setTypeFaceRobotoRegular(Context context) {
        setTypeface(FontUtils.robotoRegular(context));
    }

    public void setTypeFaceRobotoLight(Context context) {
        setTypeface(FontUtils.robotoLight(context));
    }
}
