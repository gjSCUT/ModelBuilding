package com.bn.Util;

import android.util.Log;

import com.bn.object.Body;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class Indesign {

    private Stack<List<Body>> redo;//����ջ  ջ��Ϊ��ǰ����
    private Stack<List<Body>> undo;//�ָ�ջ

    public Indesign() {
        redo = new Stack<>();
        undo = new Stack<>();
    }

    //�ѵ�ǰ������뵽����ջ��
    public void addRedoStack(List<Body> op) {
        Log.e("test", "push success=" + redo.size());
        if (redo.size() >= 1) {
            undo.push(redo.pop());
            redo.clear();
            redo.push(op);
            Log.e("test", "undo success=" + undo.size());
        } else redo.push(op);
    }

    //���ָ�ջ�Ƿ����Pop
    public boolean undoCheck() {
        return !undo.isEmpty();
    }

    //��鹤��ջ�Ƿ����Pop
    public boolean redoCheck() {
        if (redo.size() > 1)
            return true;
        else return false;
    }

    //Redo����ʵ��
    public List<Body> Redo() {
        undo.push(redo.pop());
        return redo.peek();
    }

    //Undo����ʵ��
    public List<Body> Undo() {
        List<Body> temp = undo.pop();
        Log.e("test", "undo success=" + undo.size());
        redo.push(temp);
        return temp;
    }

}
