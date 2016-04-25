package com.bn.draw;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DrawSurfaceView extends GLSurfaceView {
    private MyGLRender mRender;


    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2); //…Ë÷√ π”√OPENGL ES2.0
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRender = new MyGLRender();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRender);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mRender.l.add(e.getX(), mRender.sHeight - e.getY());
        }
        return true;
    }

}


class MyGLRender implements GLSurfaceView.Renderer {
    public static float[] mMVPMatrix = new float[16];
    public static float[] mProjectionMatrix = new float[16];
    public static float[] mViewMatrix = new float[16];
    public int sHeight = 0;
    public LoopLine l;
    Triangle t1;
    float n = 3;

    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 unused,
                                 javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float triangleCoords1[] = {   // in counterclockwise order:
                100, 100, 0.0f, // top
                -100, 100, 0.0f, // bottom left
                -100, -100, 0.0f  // bottom right
        };
        t1 = new Triangle(triangleCoords1);
        l = new LoopLine();
    }

    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)

        n += 0.1;
        // Matrix.setIdentityM(mViewMatrix,0);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        float[] m = new float[16];
        Matrix.setIdentityM(m, 0);
        //t1.draw(m);
        l.draw();
    }

    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, 3, 7);
        Matrix.setLookAtM(mViewMatrix, 0, width / 2, height / 2, 3, width / 2, height / 2, 0f, 0f, 1.0f, 0.0f);
        sHeight = height;
        //Matrix.setIdentityM(mProjectionMatrix,0);
    }


}