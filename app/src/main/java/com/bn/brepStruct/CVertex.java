package com.bn.brepStruct;


public class CVertex {
    private static int id = 0;

    public int vertex_id;
    public Point3f vcoord;
    public CVertex next;
    public CVertex prev;

    public CVertex() {
        vertex_id = id++;
        vcoord = new Point3f(0, 0, 0);
        next = null;
        prev = null;
    }


    public void SetCoord(Point3f point) {
        vcoord = point;
    }
}

