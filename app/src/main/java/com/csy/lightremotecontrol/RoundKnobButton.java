package com.csy.lightremotecontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;


public class RoundKnobButton extends RelativeLayout implements OnGestureListener {

    private GestureDetector gestureDetector;
    private float mAngleDown, mAngleUp;
    private ImageView ivRotor;
    private Bitmap bmpRotorOn, bmpRotorOff;
    private boolean mState = false;
    private int m_nWidth = 0, m_nHeight = 0;

    private float buttonAngle = 0;//记录按钮的角度
    private float lastTouchAngle;//记录手指上一次角度

    interface RoundKnobButtonListener {
        public void onStateChange(boolean newstate);

        public void onRotate(int percentage);
    }

    private RoundKnobButtonListener m_listener;

    public void SetListener(RoundKnobButtonListener l) {
        m_listener = l;
    }

    public void SetState(boolean state) {
        mState = state;
        ivRotor.setImageBitmap(state ? bmpRotorOn : bmpRotorOff);
    }

    public RoundKnobButton(Context context, int back, int rotoron, int rotoroff, final int w, final int h) {
        super(context);
        // we won't wait for our size to be calculated, we'll just store out fixed size
        m_nWidth = w;
        m_nHeight = h;
        // create stator
        ImageView ivBack = new ImageView(context);
        ivBack.setImageResource(back);
        LayoutParams lp_ivBack = new LayoutParams(
                w, h);
        lp_ivBack.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivBack, lp_ivBack);
        // load rotor images
        Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotoron);
        Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(), rotoroff);
        float scaleWidth = ((float) w) / srcon.getWidth();
        float scaleHeight = ((float) h) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        bmpRotorOn = Bitmap.createBitmap(srcon, 0, 0, srcon.getWidth(), srcon.getHeight(), matrix, true);
        bmpRotorOff = Bitmap.createBitmap(srcoff, 0, 0, srcoff.getWidth(), srcoff.getHeight(), matrix, true);
        // create rotor
        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOn);
        LayoutParams lp_ivKnob = new LayoutParams(w, h);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivRotor, lp_ivKnob);
        // set initial state
        SetState(mState);
        // enable gesture detector
        gestureDetector = new GestureDetector(getContext(), this);
    }

    /**
     * math..
     *
     * @param x
     * @param y
     * @return
     */
    private float cartesianToPolar(float y, float x) {
        //负号，配合外面的参数，将正上方设为0度，左边为负，右边为正。
        float angle = (float) -Math.toDegrees(Math.atan2(y - 0.5f, x - 0.5f));
        return angle;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) return true;
        else return super.onTouchEvent(event);
    }

    //按下时
    public boolean onDown(MotionEvent event) {
        float x = event.getX() / ((float) getWidth());
        float y = event.getY() / ((float) getHeight());
        mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
        lastTouchAngle = mAngleDown;
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("onSingleTapUp", "onSingleTapUp");
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());
        // 1-将x轴向左，y向上。而参数x在前，y在后，又交换了x，y轴。最后cartesianToPolar函数将actan反号，最终将正上方设为0度，左边为负，右边为正。
        mAngleUp = cartesianToPolar(1 - x, 1 - y);


        // 如果角度小于10，只当做是按下
        if (!Float.isNaN(mAngleDown) && !Float.isNaN(mAngleUp) && Math.abs(mAngleUp - mAngleDown) < 10) {
            SetState(!mState);
            if (m_listener != null) m_listener.onStateChange(mState);
        }
        return true;
    }

    public void setRotorPosAngle(float deg) {

        Matrix matrix = new Matrix();
        ivRotor.setScaleType(ScaleType.MATRIX);
        matrix.postRotate((float) deg, getWidth() / 2, getHeight() / 2);//getWidth()/2, getHeight()/2);
        ivRotor.setImageMatrix(matrix);
    }

    public void setRotorPercentage(int percentage) {
        int posDegree = (int) (percentage * 2.88 - 144);
        setRotorPosAngle(posDegree);
    }

    //旋转时
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());
        // 1-将x轴向左，y向上。而参数x在前，y在后，又交换了x，y轴。最后cartesianToPolar函数将actan反号，最终将正上方设为0度，左边为负，右边为正。
        //手指的现在角度
        float currentTouchAngle = cartesianToPolar(1 - x, 1 - y);
        if (!Float.isNaN(currentTouchAngle)) {
            float posDegrees = currentTouchAngle - lastTouchAngle;
            if (posDegrees > 90 || posDegrees < -90)
                return false;
            lastTouchAngle = currentTouchAngle;
            buttonAngle = buttonAngle + posDegrees;
            if (buttonAngle > 144) buttonAngle = 144;
            if (buttonAngle < -144) buttonAngle = -144;

            // rotate our imageview
            setRotorPosAngle(buttonAngle);
            // get a linear scale
            float scaleDegrees = buttonAngle + 144; // given the current parameters, we go from 0 to 300
            // get position percent 0~100
            int percent = (int) (scaleDegrees / 2.88);
            if (m_listener != null) m_listener.onRotate(percent);

            return true; //consumed
        } else
            return false; // not consumed
    }

    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }


}
