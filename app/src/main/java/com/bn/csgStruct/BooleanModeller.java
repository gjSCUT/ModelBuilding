package com.bn.csgStruct;

import android.opengl.Matrix;

import com.bn.Main.MatrixState;
import com.bn.Main.MySurfaceView;
import com.bn.object.Body;
import com.bn.object.Solid;
import com.bn.Util.MathUtil;

import java.util.ArrayList;
import java.util.List;


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

    public Solid getUnion() {
        return composeSolid(Face.OUTSIDE, Face.SAME, Face.OUTSIDE);
    }

    public Solid getIntersection() {
        return composeSolid(Face.INSIDE, Face.SAME, Face.INSIDE);
    }

    public Solid getDifference() {
        m_pObject2.invertInsideFaces();
        return composeSolid(Face.OUTSIDE, Face.OPPOSITE, Face.INSIDE);
    }

    public Solid composeSolid(int faceStatus1, int faceStatus2, int faceStatus3) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        groupObjectComponents(m_pObject1, vertices, indices, faceStatus1, faceStatus2);
        groupObjectComponents(m_pObject2, vertices, indices, faceStatus3, faceStatus3);


        MatrixState.pushMatrix();
        b.setBody();
        float[] MMatrix = MatrixState.getMMatrix();
        float[] inverseMatrix = new float[16];
        Matrix.invertM(inverseMatrix, 0, MMatrix, 0);
        MatrixState.popMatrix();

        for (Vector3f v : vertices) {
            float[] temp = new float[]{v.x, v.y, v.z, 1};
            Matrix.multiplyMV(temp, 0, inverseMatrix, 0, temp, 0);
            v.x = temp[0];
            v.y = temp[1];
            v.z = temp[2];
        }
        Solid result = new Solid(vertices, indices, 2);
        result.copyBody(b);

        return result;
    }

    public void groupObjectComponents(
            Object3D object,
            List<Vector3f> vertices,
            List<Integer> indices,
            int faceStatus1,
            int faceStatus2) {
        for(Vertex vertex : object.getVertices()){
            if(!vertices.contains(vertex.getPosition()))
                vertices.add(vertex.getPosition());
        }
        for (int i = 0; i < object.getNumFaces(); i++) {
            Face face = object.getFace(i);
            if (face.getStatus() == faceStatus1 || face.getStatus() == faceStatus2) {

                indices.add(vertices.indexOf(face.v1.getPosition()));
                indices.add(vertices.indexOf(face.v2.getPosition()));
                indices.add(vertices.indexOf(face.v3.getPosition()));

            }
        }


    }

}
