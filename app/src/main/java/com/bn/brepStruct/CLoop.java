package com.bn.brepStruct;


public class CLoop {
    public CFace host_f;
    public CLoop next;
    public CLoop prev;
    private CHalfEdge halfedge_h;

    public CLoop() {
        halfedge_h = null;
        host_f = null;
        next = prev = null;
    }


    public boolean AddHalfEdge(CHalfEdge phe1, CHalfEdge phe2) {
        if (phe1 == null && phe2 == null) {
            return false;
        }

        CHalfEdge ptemp = halfedge_h;

        if (phe2 == null) {
            if (ptemp == null)
                halfedge_h = phe1;
            else {
                while (ptemp.next != null)
                    ptemp = ptemp.next;
                ptemp.next = phe1;
                phe1.prev = ptemp;
            }
            phe1.host_lp = this;
        } else {
            phe1.next = phe1.adj = phe2;
            phe2.prev = phe2.adj = phe1;
            if (ptemp == null) {
                halfedge_h = phe1;
                phe1.prev = phe2;
                phe2.next = phe1;
            } else {
                while (ptemp.next.GetVertex() != phe1.GetVertex())
                    ptemp = ptemp.next;
                phe2.next = ptemp.next;
                ptemp.next.prev = phe2;
                ptemp.next = phe1;
                phe1.prev = ptemp;
            }
            phe1.host_lp = phe2.host_lp = this;
        }

        return true;
    }

    public CHalfEdge GetHalfEdgeHead() {
        return halfedge_h;
    }

    public boolean IsVertexIn(CVertex pvert) {
        if (pvert == null)
            return false;

        CHalfEdge he = halfedge_h;

        while (he != null) {
            if (pvert == he.GetVertex())
                return true;
            he = he.next;
            if (he == halfedge_h)
                return false;
        }

        return false;
    }

    public CHalfEdge FindHostHalfEdge(CVertex pvert) {
        if (pvert == null)
            return null;

        CHalfEdge rethe = null, he = halfedge_h;

        while (he != null) {
            if (he.GetVertex() == pvert)
                rethe = he;
            he = he.next;
            if (he == halfedge_h)
                return rethe;
        }

        return rethe;
    }

}
