package br.com.encontredelivery.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import br.com.encontredelivery.R;

public class TintOnStateImageView extends ImageView
{
    private ColorStateList mColorStateList;

    public TintOnStateImageView(Context context) {
        super(context);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context, attrs, 0);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs, defStyleAttr);
    }

    private void initialise(Context context, AttributeSet attributeSet, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.TintOnStateImageView, defStyle, 0);
        mColorStateList = a.getColorStateList(R.styleable.TintOnStateImageView_colorStateList);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mColorStateList != null && mColorStateList.isStateful())
        {
            updateTintColor();
        }
    }

    private void updateTintColor()  {
        int color = mColorStateList.getColorForState(getDrawableState(),  getResources().getColor(R.color.nav_drawer_item_icon_normal));

        super.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
