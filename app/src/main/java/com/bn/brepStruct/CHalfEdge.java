package com.bn.brepStruct;


public class CHalfEdge {
    public CLoop host_lp;
    public CEdge edge;
    public CHalfEdge next;
    public CHalfEdge prev;
    public CHalfEdge adj;
    private CVertex vtx;

    public CHalfEdge() {
        edge = null;
        vtx = null;
        host_lp = null;
        next = null;
        prev = null;
        adj = null;
    }

    public CVertex GetVertex() {
        return vtx;
    }

    public boolean SetVertex(CVertex pvert) {
        if (pvert == null) {
            return false;
        }

        vtx = pvert;

        return true;
    }
}