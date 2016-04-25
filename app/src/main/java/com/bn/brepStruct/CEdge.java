package com.bn.brepStruct;


public class CEdge {
    public CHalfEdge he1;
    public CHalfEdge he2;
    public CEdge next;
    public CEdge prev;

    public CEdge() {
        he1 = null;
        he2 = null;
        next = null;
        prev = null;
    }
}
