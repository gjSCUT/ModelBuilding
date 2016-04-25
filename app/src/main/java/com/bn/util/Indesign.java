package com.bn.util;

import android.util.Log;

import com.bn.object.Body;

import java.util.Stack;
import java.util.Vector;

public class Indesign {

    private Stack<Vector<Body>> redo;//工作栈  栈顶为当前画面
    private Stack<Vector<Body>> undo;//恢复栈

    public Indesign() {
        redo = new Stack<Vector<Body>>();
        undo = new Stack<Vector<Body>>();
    }

    //把当前画面加入到工作栈中
    public void addRedoStack(Vector<Body> op) {
        Log.e("test", "push success=" + redo.size());
        if (redo.size() >= 1) {
            undo.push(redo.pop());
            redo.clear();
            redo.push(op);
            Log.e("test", "undo success=" + undo.size());
        } else redo.push(op);
    }

    //检查恢复栈是否可以Pop
    public boolean undoCheck() {
        return !undo.isEmpty();
    }

    //检查工作栈是否可以Pop
    public boolean redoCheck() {
        if (redo.size() > 1)
            return true;
        else return false;
    }

    //Redo功能实现
    public Vector<Body> Redo() {
        undo.push(redo.pop());
        return redo.peek();
    }

    //Undo功能实现
    public Vector<Body> Undo() {
        Vector<Body> temp = undo.pop();
        Log.e("test", "undo success=" + undo.size());
        redo.push(temp);
        return temp;
    }

}
