package com.bn.drawSweep;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bn.csgStruct.Vector2f;

import java.util.List;


public class DrawSurfaceView extends View {
    private Paint mPaint, linePaint;
    private int paintType, viewMode, featureMode;
    private int width, height;
    LoopLine loopLineLeft, loopLineRight;

    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loopLineLeft = new LoopLine();
        loopLineRight = new LoopLine();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(8);

        paintType = 0;
    }

    public void setPaintType(int paintType) {
        this.paintType = paintType;
    }

    public void setFeatureMode(int featureMode) {
        this.featureMode = featureMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (e.getX() < width / 2)
                    viewMode = 0;
                else
                    viewMode = 1;
                switch (paintType) {
                    case 0:
                        if (viewMode == 0)
                            loopLineLeft.addLine(x, y);
                        else
                            loopLineRight.addLine(x, y);
                        break;
                    case 1:
                        if (viewMode == 0)
                            loopLineLeft.addBesizer(e.getX(), e.getY());
                        else
                            loopLineRight.addBesizer(e.getX(), e.getY());
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (paintType) {
                    case 0:
                        if (viewMode == 0)
                            loopLineLeft.moveLine(x, y);
                        else
                            loopLineRight.moveLine(x, y);
                        break;
                    case 1:
                        if (viewMode == 0)
                            loopLineLeft.moveBesizer(e.getX(), e.getY());
                        else
                            loopLineRight.moveBesizer(e.getX(), e.getY());
                        break;
                }
                break;
        }
        this.invalidate();
        return true;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(width / 2, 0, width / 2, height, mPaint);
        drawLeft(canvas, loopLineLeft, featureMode);
        drawRight(canvas, loopLineRight, featureMode);
    }


    public void drawLeft(Canvas canvas, LoopLine loopLine, int mode) {
        List<Vector2f> vertexList = loopLine.getVertexList();
        if (vertexList.size() < 2) return;
        if (mode == 1) {
            for (int i = 0; i < vertexList.size() - 1; i++) {
                canvas.drawLine(vertexList.get(i).x, vertexList.get(i).y, vertexList.get(i + 1).x, vertexList.get(i + 1).y, linePaint);
            }
            canvas.drawLine(vertexList.get(vertexList.size() - 1).x, vertexList.get(vertexList.size() - 1).y, vertexList.get(0).x, vertexList.get(0).y, linePaint);
        } else if (mode == 2) {
            canvas.drawLine(width / 2, vertexList.get(0).y, vertexList.get(0).x, vertexList.get(0).y, linePaint);
            for (int i = 0; i < vertexList.size() - 1; i++) {
                canvas.drawLine(vertexList.get(i).x, vertexList.get(i).y, vertexList.get(i + 1).x, vertexList.get(i + 1).y, linePaint);
            }
            canvas.drawLine(vertexList.get(vertexList.size()-1).x, vertexList.get(vertexList.size()-1).y, width / 2, vertexList.get(vertexList.size()-1).y, linePaint);
        }
    }

    public void drawRight(Canvas canvas, LoopLine loopLine, int mode) {
        List<Vector2f> vertexList = loopLine.getVertexList();
        if (vertexList.size() < 2) return;
        if (mode == 1) {
            for (int i = 0; i < vertexList.size() - 1; i++) {
                canvas.drawLine(vertexList.get(i).x, vertexList.get(i).y, vertexList.get(i + 1).x, vertexList.get(i + 1).y, linePaint);
            }
        }
    }

    public List<Vector2f> getSweepFace() {
        List<Vector2f> vetexs = loopLineLeft.getVertexList();
        for (Vector2f vector2f : vetexs) {
            vector2f.x = (vector2f.x - width / 4) * 15 * 4 / width;
            vector2f.y = (vector2f.y - height / 2) * 15 * 4 / width;
        }
        return vetexs;
    }

    public List<Vector2f> getSweepLine() {
        List<Vector2f> vetexs = loopLineRight.getVertexList();
        for (Vector2f vector2f : vetexs) {
            vector2f.x = (vector2f.x - width * 3 / 4) * 15 * 4 / width;
            vector2f.y = -(vector2f.y - height / 2) * 15 * 4 / width;
        }
        return vetexs;
    }

    public List<Vector2f> getRevolveFace() {
        List<Vector2f> vetexs = loopLineLeft.getVertexList();
        for (Vector2f vector2f : vetexs) {
            vector2f.x = (vector2f.x - width / 2) * 20 * 2 / width;
            vector2f.y = -(vector2f.y - height / 2) * 20 * 2 / width;
        }
        return vetexs;
    }
}


