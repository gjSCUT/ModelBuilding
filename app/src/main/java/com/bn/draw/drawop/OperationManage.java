package com.bn.draw.drawop;

import com.bn.draw.drawop.Operation.Op;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;


/**
 * 操作管理�?
 *
 * @author GuoJun
 */
public class OperationManage {

    /**
     * operation list
     */
    private LinkedList<Operation> listDraw;
    /**
     * operation stack
     */
    private Stack<Operation> stOperation;
    /**
     * undo operation recycle stack
     */
    private Stack<Operation> stRecycle;

    /**
     * now draw stack
     */
    private Stack<OpDraw> stNowDraw;
    /**
     * 绘图模式
     * RE：重绘画�?
     * ADD：增量绘�?
     * NOW：重绘当前路�?
     */
    private DrawMode mode;

    ;
    public OperationManage() {
        listDraw = new LinkedList<Operation>();
        stOperation = new Stack<Operation>();
        stRecycle = new Stack<Operation>();
        stNowDraw = new Stack<OpDraw>();
        mode = DrawMode.RE;
    }

    /**
     * push in list
     *
     * @param op
     */
    public void pushOp(Operation op) {
        stOperation.push(op);
    }

    /**
     * pop out list
     *
     * @param op
     * @return stOperation.Last
     */
    public Operation popOp() {
        Operation op = stOperation.pop();
        return op;
    }

    /**
     * push in listDraw
     *
     * @param listDraw
     */
    public void pushDraw(OpDraw op) {
        listDraw.add(op);
        stNowDraw.push(op);
    }

    /**
     * push in listDraw
     *
     * @param listDraw
     */
    public void pushNowDraw(OpDraw op) {
        stNowDraw.push(op);
    }

    /**
     * pop out listDraw
     *
     * @param op
     * @return stOperation.Last
     */
    public Operation popDraw() {
        Operation op = listDraw.getLast();
        listDraw.removeLast();
        stNowDraw.pop();
        return op;
    }

    public Operation popNowDraw() {
        return stNowDraw.pop();
    }

    public OpDraw getNowDraw() {
        if (stNowDraw.isEmpty())
            return null;
        else
            return stNowDraw.lastElement();
    }

    public Operation getDrawLast() {
        if (listDraw.isEmpty())
            return null;
        else
            return listDraw.getLast();
    }

    public Iterator<Operation> getDrawIterator() {
        return listDraw.iterator();
    }

    public DrawMode getMode() {
        return mode;
    }

    /**
     * 绘图模式
     */
    public void setMode(DrawMode m) {
        mode = m;
    }

    public void clear() {
        stOperation.clear();
        stRecycle.clear();
        stNowDraw.clear();
        listDraw.clear();

    }

    public int size() {
        return stOperation.size();

    }

    public void redo() {
        if (!stRecycle.isEmpty()) {
            Operation op = stRecycle.pop();
            if (op.getType() == Op.TRANS)
                ((OpTrans) op).setIsRedo(true);
            op.Redo();
            pushOp(op);
        }
    }

    public void undo() {
        if (!stOperation.isEmpty()) {
            Operation op = popOp();
            op.Undo();
            stRecycle.push(op);
        }
    }

    public enum DrawMode {RE, ADD, FILL}

}
