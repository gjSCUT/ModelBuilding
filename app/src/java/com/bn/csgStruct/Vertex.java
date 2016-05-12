package com.bn.csgStruct;

import com.bn.Main.Constant;

import java.util.Vector;


public class Vertex {
    public final static int UNKNOWN = 1;
    public final static int INSIDE = 2;
    public final static int OUTSIDE = 3;
    public final static int BOUNDARY = 4;
    public static int test = 0;
    private Vector<Vertex> adjacentVertices;
    private Vector3f position;
    private int status;


    public Vertex() {
        position = null;
        adjacentVertices = new Vector<Vertex>();
        status = UNKNOWN;
    }


    public Vertex(Vector3f p, int eStatus) {
        position = p;
        adjacentVertices = new Vector<Vertex>();
        status = eStatus;
    }

    public Vertex(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        adjacentVertices = new Vector<Vertex>();
        status = UNKNOWN;
    }

    public Vertex(float x, float y, float z, int eStatus) {
        position = new Vector3f(x, y, z);
        adjacentVertices = new Vector<Vertex>();
        this.status = eStatus;
    }


    public boolean equals(Vertex vertex) {
        boolean bPositionMatch =
                (Math.abs(position.x - vertex.position.x) < Constant.TOL &&
                        Math.abs(position.y - vertex.position.y) < Constant.TOL &&
                        Math.abs(position.z - vertex.position.z) < Constant.TOL);
        return bPositionMatch;
    }


    public Vector3f getPosition() {
        return position;
    }

    public Vector<Vertex> getVertices() {
        return adjacentVertices;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int eStatus) {
        if (status >= UNKNOWN && status <= BOUNDARY) {
            this.status = eStatus;
        }
    }

    public void addVertex(Vertex adjacentVertex) {
        boolean bAlready = false;
        for (int i = 0; i < adjacentVertices.size(); i++) {
            Vertex pVertexI = adjacentVertices.get(i);
            if (pVertexI == adjacentVertex) {
                bAlready = true;
            }
        }

        if (bAlready == false) {
            adjacentVertices.add(adjacentVertex);
        }
    }

    public void mark(int eStatus, Object3D object) {
        status = eStatus;
        object.setVertes(this);
        test++;
        for (int i = 0; i < adjacentVertices.size(); i++) {
            Vertex pVertexI = adjacentVertices.get(i);

            if (pVertexI.getStatus() == UNKNOWN) {
                pVertexI.mark(status, object);
            }
        }
    }

    public Vertex setVertex(Vertex v) {
        adjacentVertices = v.adjacentVertices;
        status = v.status;
        position = v.position;
        return this;
    }
}
