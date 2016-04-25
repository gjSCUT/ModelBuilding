package com.bn.draw.drawop;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Operation {
    protected static Canvas canvas;
    protected static Bitmap bitmap;
    protected static OperationManage opManage;
    public Op type;

    public static void setPro(Canvas c, Bitmap b, OperationManage o) {
        canvas = c;
        bitmap = b;
        opManage = o;
    }

    /**
     * 得到类型
     *
     * @return type
     */
    public Op getType() {
        return type;
    }

    /**
     * undo操作
     */
    public abstract void Undo();

    /**
     * redo操作
     */
    public abstract void Redo();

    public enum Op {DRAW, FILL, TRANS, COPY, DELETE}

}
