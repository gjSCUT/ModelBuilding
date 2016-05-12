package com.bn.brepStruct;


import com.bn.csgStruct.Point3f;

public class CSolid {
    private static int id = 0;
    public int solid_id;
    public CSolid next;
    public CSolid prev;
    private CFace face_h;
    private CEdge edge_h;
    private CVertex vert_h;

    public CSolid() {
        solid_id = id++;
        face_h = null;
        edge_h = null;
        vert_h = null;
        next = null;
        prev = null;
    }

    public CFace GetFaceHead() {
        return face_h;
    }

    public CEdge GetEdgeHead() {
        return edge_h;
    }

    public CVertex GetVertexHead() {
        return vert_h;
    }

    public boolean AddFace(CFace pface) {
        if (pface == null) {
            return false;
        }

        CFace ptemp = face_h;

        if (ptemp == null)
            face_h = pface;
        else {
            while (ptemp.next != null) {
                ptemp = ptemp.next;
            }
            ptemp.next = pface;
            pface.prev = ptemp;
        }
        pface.host_s = this;

        return true;
    }

    public boolean DelFace(CFace pface) {
        if (pface == null) {
            return false;
        }

        CFace ptemp = face_h;

        if (ptemp == null)
            face_h = pface;
        else {
            while (ptemp.next != null) {
                ptemp = ptemp.next;
            }
            ptemp.next = pface;
            pface.prev = ptemp;
        }
        pface.host_s = this;

        return true;
    }

    public boolean AddEdge(CEdge pedge) {
        if (pedge == null) {
            return false;
        }

        CEdge ptemp = edge_h;

        if (ptemp == null)
            edge_h = pedge;
        else {
            while (ptemp.next != null) {
                ptemp = ptemp.next;
            }
            ptemp.next = pedge;
            pedge.prev = ptemp;
        }

        return true;
    }

    public boolean AddVertex(CVertex pvert) {
        if (pvert == null) {
            return false;
        }

        CVertex ptemp = vert_h;

        if (ptemp == null)
            vert_h = pvert;
        else {
            while (ptemp.next != null) {
                ptemp = ptemp.next;
            }
            ptemp.next = pvert;
            pvert.prev = ptemp;
        }

        return true;
    }

    public CVertex GetVertexEnd() {
        CVertex v = vert_h;

        while (v.next != null) {
            v = v.next;
        }

        return v;
    }

    public CVertex FindVertex(Point3f point) {
        CVertex v = vert_h;

        while (v != null) {
            if (v.vcoord.x == point.x &&
                    v.vcoord.y == point.y &&
                    v.vcoord.z == point.z)
                return v;
            v = v.next;
        }

        return null;
    }

    public CFace GetFaceEnd() {
        CFace ptemp = face_h;

        while (ptemp.next != null)
            ptemp = ptemp.next;

        return ptemp;
    }
}
