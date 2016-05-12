package com.bn.drawSweep;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.bn.Util.BezierUtil;
import com.bn.csgStruct.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class LoopLine {
    private List<Vector2f> vertexList;
    private List<Vector2f> besizerControls;
    private int besizerNum;
    public LoopLine() {
        vertexList = new ArrayList<>();
        besizerControls = new ArrayList<>();
    }

    public List<Vector2f> getVertexList() {
        return vertexList;
    }

    public void addLine(float x, float y) {
        besizerControls.clear();
        Vector2f vector2f = new Vector2f(x, y);
        vertexList.add(vector2f);

        if (vertexList.size() > 1) {
            besizerControls.add(vertexList.get(vertexList.size() - 2));
        }
        besizerControls.add(vector2f);
        besizerNum = besizerControls.size();
    }

    public void moveLine(float x, float y) {
        if (vertexList.size() > 1) {
            vertexList.remove(vertexList.size() - 1);
            besizerControls.remove(besizerControls.size() - 1);
        }
        vertexList.add(new Vector2f(x, y));
        besizerControls.add(new Vector2f(x, y));
        besizerNum = besizerControls.size();
    }

    public void addBesizer(float x, float y) {
        if (vertexList.size() < 2) return;

        for (int i = 0; i < besizerNum; i++)
            vertexList.remove(vertexList.size() - 1);

        besizerControls.add(besizerControls.size() - 1, new Vector2f(x, y));

        List<Vector2f> besizerLines = BezierUtil.getBezierData(besizerControls, 0.1f);
        besizerNum = besizerLines.size();

        for (Vector2f vector2f : besizerLines)
            vertexList.add(vector2f);
    }


    public void moveBesizer(float x, float y) {
        for (int i = 0; i < besizerNum; i++)
            vertexList.remove(vertexList.size() - 1);

        besizerControls.remove(besizerControls.size() - 2);
        besizerControls.add(besizerControls.size() - 1, new Vector2f(x, y));

        List<Vector2f> besizerLines = BezierUtil.getBezierData(besizerControls, 0.1f);
        besizerNum = besizerLines.size();

        for (Vector2f vector2f : besizerLines)
            vertexList.add(vector2f);
    }

}
