package com.overseas.mtpay.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * 加强版的EditText,可以响应DrawableLeft 和 DrawableRight的点击事件
 * 要实现响应点击,先设置setDrawableListener
 * 
 * @author xing
 * @version 1.1
 */
public class XEditText extends EditText {

	private DrawableLeftListener mLeftListener;
	private DrawableRightListener mRightListener;

	final int DRAWABLE_LEFT = 0;
	final int DRAWABLE_TOP = 1;
	final int DRAWABLE_RIGHT = 2;
	final int DRAWABLE_BOTTOM = 3;

	@SuppressLint("NewApi")
	public XEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public XEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XEditText(Context context) {
		super(context);
	}

	public void setDrawableLeftListener(DrawableLeftListener listener) {
		this.mLeftListener = listener;
	}

	public void setDrawableRightListener(DrawableRightListener listener) {
		this.mRightListener = listener;
	}

	public interface DrawableLeftListener {
		public void onDrawableLeftClick(View view);
	}

	public interface DrawableRightListener {
		public void onDrawableRightClick(View view);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (mRightListener != null) {
				Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
				if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
					mRightListener.onDrawableRightClick(this);
					return true;
				}
			}

			if (mLeftListener != null) {
				Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT];
				if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
					mLeftListener.onDrawableLeftClick(this);
					return true;
				}
			}
			break;
		}

		return super.onTouchEvent(event);
	}
}