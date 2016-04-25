package com.bn.csgStruct;

import android.opengl.Matrix;
import android.util.Log;

import com.bn.main.MatrixState;
import com.bn.main.MySurfaceView;
import com.bn.csgStruct.Struct.Vector3f;
import com.bn.object.Body;
import com.bn.object.Solid;
import com.bn.util.MathUtil;

import java.util.Vector;


public class BooleanModeller {
    private Object3D m_pObject1, m_pObject2;
    private Body b;

    public BooleanModeller(Solid solid1, Solid solid2) {
        m_pObject1 = new Object3D(solid1);
        m_pObject2 = new Object3D(solid2);
        b = solid1;

    }

    public boolean BooleanOp() {
        if (m_pObject1.splitFaces(m_pObject2) && m_pObject2.splitFaces(m_pObject1)) {
            m_pObject1.classifyFaces(m_pObject2);

            m_pObject2.classifyFaces(m_pObject1);
            return true;
        } else
            return false;
    }

    public Solid getUnion(MySurfaceView mv) {
        return composeSolid(mv, Face.OUTSIDE, Face.SAME, Face.OUTSIDE);
    }

    public Solid getIntersection(MySurfaceView mv) {
        return composeSolid(mv, Face.INSIDE, Face.SAME, Face.INSIDE);
    }

    public Solid getDifference(MySurfaceView mv) {
        m_pObject2.invertInsideFaces();
        return composeSolid(mv, Face.OUTSIDE, Face.OPPOSITE, Face.INSIDE);
    }

    public Solid composeSolid(MySurfaceView mv, int faceStatus1, int faceStatus2, int faceStatus3) {
        Vector<Vertex> vertices = new Vector<Vertex>();
        Vector<Integer> indices = new Vector<Integer>();

        groupObjectComponents(m_pObject1, vertices, indices, faceStatus1, faceStatus2);
        groupObjectComponents(m_pObject2, vertices, indices, faceStatus3, faceStatus3);

        Vector<Vector3f> vectors = new Vector<Vector3f>();

        for (int i = 0; i < vertices.size(); i++) {
            Vertex pVertex = vertices.get(i);

            vectors.add(pVertex.getPosition());
        }

        float[] tempMarix = new float[16];
        float[] mMarix = new float[16];
        MatrixState.pushMatrix();

        b.setBody();
        tempMarix = MatrixState.getMMatrix();
        mMarix = MathUtil.InverseMatrix(tempMarix);

        Vector<Vector3f> hasVisited = new Vector<Vector3f>();
        for (Vector3f v : vectors) {
            if (hasVisited.indexOf(v) == -1) {
                hasVisited.add(v);
                float[] temp = new float[]{v.x, v.y, v.z, 1};
                Matrix.multiplyMV(temp, 0, mMarix, 0, temp, 0);
                v.x = temp[0];
                v.y = temp[1];
                v.z = temp[2];
            }
        }

        Solid result = new Solid(mv, vectors, indices, 1);
        result.copyBody(b);

        MatrixState.popMatrix();

        return result;
    }

    public void groupObjectComponents(
            Object3D object,
            Vector<Vertex> vertices,
            Vector<Integer> indices,
            int faceStatus1,
            int faceStatus2) {
        int test1 = 0;
        for (int i = 0; i < object.getNumFaces(); i++) {
            Face face = object.getFace(i);

            if (face.getStatus() == faceStatus1 || face.getStatus() == faceStatus2) {
                test1++;

                Vector<Vertex> faceVerts = new Vector<Vertex>();
                faceVerts.add(face.v1);
                faceVerts.add(face.v2);
                faceVerts.add(face.v3);

                for (int j = 0; j < faceVerts.size(); j++) {
                    indices.add(vertices.size());
                    vertices.add(faceVerts.get(j));
                }
            }
        }
        Log.i("group1", test1 + "");
    }

}
