package com.bn.csgStruct;

import android.opengl.Matrix;

import com.bn.Main.Constant;
import com.bn.object.Solid;

import java.util.ArrayList;
import java.util.List;


class Object3D {
    private List<Vertex> vertices;
    private List<Face> faces;

    private Bound bound;

    Object3D() {
        vertices = null;
        faces = null;
        bound = null;
    }

    Object3D(Solid solid) {
        Vertex v1 = null;
        Vertex v2 = null;
        Vertex v3 = null;
        Vertex vertex = null;

        List<Vector3f> verticesPoints = new ArrayList<>();
        for (Vector3f v : solid.getVertices()) {
            float[] point = new float[]{v.x, v.y, v.z, 1};
            Matrix.multiplyMV(point, 0, solid.getM(), 0, point, 0);
            Vector3f newPoint = new Vector3f(point[0], point[1], point[2]);
            verticesPoints.add(newPoint);
            //verticesTemp.add(vertex);
        }
        List<Integer> indices = solid.getIndices();

        List<Vertex> verticesTemp = new ArrayList<>();

        vertices = new ArrayList<>();
        for (Vector3f v : verticesPoints) {
            vertex = addVertex(v, Vertex.UNKNOWN);
            verticesTemp.add(vertex);
        }

        faces = new ArrayList<>();
        for (int i = 0; i < indices.size(); i = i + 3) {
            v1 = verticesTemp.get(indices.get(i));
            v2 = verticesTemp.get(indices.get(i + 1));
            v3 = verticesTemp.get(indices.get(i + 2));
            addFace(v1, v2, v3);
        }


        //create bound
        bound = new Bound(verticesPoints);

    }

    //--------------------------------------SETS------------------------------------//

    public void setVertes(Vertex v) {
        for (Vertex vertex : vertices) {
            if (vertex.getPosition() == v.getPosition())
                vertex.setStatus(v.getStatus());
        }
    }


    //--------------------------------------GETS------------------------------------//


    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public int getNumFaces() {
        return faces.size();
    }

    public Face getFace(int index) {
        if (index < 0 || index >= faces.size()) {
            return null;
        } else {
            return faces.get(index);
        }
    }

    public Bound getBound() {
        return bound;
    }

    //------------------------------------ADDS----------------------------------------//

    public Face addFace(Vertex v1, Vertex v2, Vertex v3) {
        if (!(v1.equals(v2) || v1.equals(v3) || v2.equals(v3))) {
            Face face = new Face(v1, v2, v3);

            Face pAddedFace = null;
            if (faces.add(face))
                pAddedFace = faces.get(faces.size() - 1);
            return pAddedFace;

        } else {
            return null;
        }
    }

    public Vertex addVertex(Vector3f pos, int status) {
        int i;
        Vertex vertex = new Vertex(pos, status);

        for (i = 0; i < vertices.size(); i++) {
            if (vertex.equals(vertices.get(i)))
                break;
        }

        if (i == vertices.size()) {
            Vertex pVertexInList = null;

            if (vertices.add(vertex) == true)
                pVertexInList = vertices.get(vertices.size() - 1);
            return pVertexInList;

        } else {
            Vertex pVertexInList = null;

            pVertexInList = vertices.get(i);
            pVertexInList.setStatus(status);

            return pVertexInList;
        }

    }

    //-------------------------FACES_SPLITTING_METHODS------------------------------//

    public boolean splitFaces(Object3D object) {
        Line line;
        Face face1;

        Segment segment1, segment2;

        float distFace1Vert1, distFace1Vert2, distFace1Vert3, distFace2Vert1, distFace2Vert2, distFace2Vert3;
        int signFace1Vert1, signFace1Vert2, signFace1Vert3, signFace2Vert1, signFace2Vert2, signFace2Vert3;
        if (getBound().overlap(object.getBound())) {
            for (int i = 0; i < getNumFaces(); i++) {
                face1 = getFace(i);
                Face face1Orig = new Face();
                face1Orig.setFace(face1);
                if (face1.getBound().overlap(object.getBound())) {
                    for (Face face2 : object.getFaces()) {
                        Face face2Orig = new Face();
                        face2Orig.setFace(face2);

                        if (face1.getBound().overlap(face2.getBound())) {
                            distFace1Vert1 = computeDistance((face1.v1), face2);
                            distFace1Vert2 = computeDistance((face1.v2), face2);
                            distFace1Vert3 = computeDistance((face1.v3), face2);

                            signFace1Vert1 = (distFace1Vert1 > Constant.TOL ? 1 : (distFace1Vert1 < -Constant.TOL ? -1 : 0));
                            signFace1Vert2 = (distFace1Vert2 > Constant.TOL ? 1 : (distFace1Vert2 < -Constant.TOL ? -1 : 0));
                            signFace1Vert3 = (distFace1Vert3 > Constant.TOL ? 1 : (distFace1Vert3 < -Constant.TOL ? -1 : 0));

                            if (!(signFace1Vert1 == signFace1Vert2 && signFace1Vert2 == signFace1Vert3)) {
                                distFace2Vert1 = computeDistance((face2.v1), face1);
                                distFace2Vert2 = computeDistance((face2.v2), face1);
                                distFace2Vert3 = computeDistance((face2.v3), face1);

                                signFace2Vert1 = (distFace2Vert1 > Constant.TOL ? 1 : (distFace2Vert1 < -Constant.TOL ? -1 : 0));
                                signFace2Vert2 = (distFace2Vert2 > Constant.TOL ? 1 : (distFace2Vert2 < -Constant.TOL ? -1 : 0));
                                signFace2Vert3 = (distFace2Vert3 > Constant.TOL ? 1 : (distFace2Vert3 < -Constant.TOL ? -1 : 0));

                                if (!(signFace2Vert1 == signFace2Vert2 && signFace2Vert2 == signFace2Vert3)) {
                                    line = new Line(face1, face2);
                                    segment1 = new Segment(line, face1, signFace1Vert1, signFace1Vert2, signFace1Vert3);
                                    segment2 = new Segment(line, face2, signFace2Vert1, signFace2Vert2, signFace2Vert3);

                                    if (segment1.intersect(segment2)) {

                                        this.splitFace(i, segment1, segment2);

                                        face1 = getFace(i);

                                        if (!face1.match(face1Orig)) {

                                            if (face1Orig.equals(getFace(getNumFaces() - 1))) {
                                                if (i != (getNumFaces() - 1)) {
                                                    faces.remove(getNumFaces() - 1);
                                                    faces.add(i, face1Orig);
                                                }
                                            } else {
                                                i--;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }


    public float computeDistance(Vertex vertex, Face face) {
        Vector3f normal = face.getNormal();
        float a = normal.x;
        float b = normal.y;
        float c = normal.z;
        float d = -(a * face.v1.getPosition().x + b * face.v1.getPosition().y + c * face.v1.getPosition().z);
        return a * vertex.getPosition().x + b * vertex.getPosition().y + c * vertex.getPosition().z + d;
    }

    public void splitFace(int facePos, Segment segment1, Segment segment2) {
        Vector3f startPos, endPos;
        int startType, endType, middleType;
        float startDist, endDist;

        Face face = getFace(facePos);
        Vertex startVertex = segment1.getStartVertex();
        Vertex endVertex = segment1.getEndVertex();

        if (segment2.getStartDistance() > segment1.getStartDistance() + Constant.TOL) {
            startDist = segment2.getStartDistance();
            startType = segment1.getIntermediateType();
            startPos = segment2.getStartPosition();
        } else {
            startDist = segment1.getStartDistance();
            startType = segment1.getStartType();
            startPos = segment1.getStartPosition();
        }

        if (segment2.getEndDistance() < segment1.getEndDistance() - Constant.TOL) {
            endDist = segment2.getEndDistance();
            endType = segment1.getIntermediateType();
            endPos = segment2.getEndPosition();
        } else {
            endDist = segment1.getEndDistance();
            endType = segment1.getEndType();
            endPos = segment1.getEndPosition();
        }
        middleType = segment1.getIntermediateType();

        //set vertex to BOUNDARY if it is start type
        if (startType == Segment.VERTEX) {
            startVertex.setStatus(Vertex.BOUNDARY);
        }

        if (endType == Segment.VERTEX) {
            endVertex.setStatus(Vertex.BOUNDARY);
        }

        if (startType == Segment.VERTEX && endType == Segment.VERTEX) {
            return;
        } else if (middleType == Segment.EDGE) {
            int splitEdge;
            if ((startVertex == face.v1 && endVertex == face.v2) || (startVertex == face.v2 && endVertex == face.v1)) {
                splitEdge = 1;
            } else if ((startVertex == face.v2 && endVertex == face.v3) || (startVertex == face.v3 && endVertex == face.v2)) {
                splitEdge = 2;
            } else {
                splitEdge = 3;
            }

            if (startType == Segment.VERTEX) {
                breakFaceInTwo(facePos, endPos, splitEdge);
                return;
            } else if (endType == Segment.VERTEX) {
                breakFaceInTwo(facePos, startPos, splitEdge);
                return;
            } else if (startDist == endDist) {
                breakFaceInTwo(facePos, endPos, splitEdge);
            } else {
                if ((startVertex == face.v1 && endVertex == face.v2) ||
                        (startVertex == face.v2 && endVertex == face.v3) ||
                        (startVertex == face.v3 && endVertex == face.v1)) {
                    breakFaceInThree(facePos, startPos, endPos, splitEdge);
                } else {
                    breakFaceInThree(facePos, endPos, startPos, splitEdge);
                }
            }
            return;
        }

        //______-FACE-______

        //VERTEX-FACE-EDGE
        else if (startType == Segment.VERTEX && endType == Segment.EDGE) {
            breakFaceInTwo(facePos, endPos, endVertex);
        }
        //EDGE-FACE-VERTEX
        else if (startType == Segment.EDGE && endType == Segment.VERTEX) {
            breakFaceInTwo(facePos, startPos, startVertex);
        }
        //VERTEX-FACE-FACE
        else if (startType == Segment.VERTEX && endType == Segment.FACE) {
            breakFaceInThree(facePos, endPos, startVertex);
        }
        //FACE-FACE-VERTEX
        else if (startType == Segment.FACE && endType == Segment.VERTEX) {
            breakFaceInThree(facePos, startPos, endVertex);
        }
        //EDGE-FACE-EDGE
        else if (startType == Segment.EDGE && endType == Segment.EDGE) {
            breakFaceInThree(facePos, startPos, endPos, startVertex, endVertex);
        }
        //EDGE-FACE-FACE
        else if (startType == Segment.EDGE && endType == Segment.FACE) {
            breakFaceInFour(facePos, startPos, endPos, startVertex);
        }
        //FACE-FACE-EDGE
        else if (startType == Segment.FACE && endType == Segment.EDGE) {
            breakFaceInFour(facePos, endPos, startPos, endVertex);
        }
        //FACE-FACE-FACE
        else if (startType == Segment.FACE && endType == Segment.FACE) {
            Vector3f segmentVector = new Vector3f(startPos.x - endPos.x, startPos.y - endPos.y, startPos.z - endPos.z);

            //if the intersection segment is a point only...
            if (Math.abs(segmentVector.x) < Constant.TOL &&
                    Math.abs(segmentVector.y) < Constant.TOL &&
                    Math.abs(segmentVector.z) < Constant.TOL) {
                breakFaceInThree(facePos, startPos);
                return;
            }

            //gets the vertex more lined with the intersection segment
            int linedVertex;
            Vector3f linedVertexPos;
            Vector3f vertexVector = new Vector3f(endPos.x - face.v1.getPosition().x,
                    endPos.y - face.v1.getPosition().y,
                    endPos.z - face.v1.getPosition().z);
            vertexVector.normalize();
            //float dot1 = Math.abs(segmentVector.dot(vertexVector));
            float dot1 = Math.abs(segmentVector.multiV(vertexVector));
            vertexVector = new Vector3f(endPos.x - face.v2.getPosition().x,
                    endPos.y - face.v2.getPosition().y,
                    endPos.z - face.v2.getPosition().z);
            vertexVector.normalize();
            //float dot2 = Math.abs(segmentVector.dot(vertexVector));
            float dot2 = Math.abs(segmentVector.multiV(vertexVector));
            vertexVector = new Vector3f(endPos.x - face.v3.getPosition().x,
                    endPos.y - face.v3.getPosition().y,
                    endPos.z - face.v3.getPosition().z);
            vertexVector.normalize();
            //float dot3 = Math.abs(segmentVector.dot(vertexVector));
            float dot3 = Math.abs(segmentVector.multiV(vertexVector));
            if (dot1 > dot2 && dot1 > dot3) {
                linedVertex = 1;
                linedVertexPos = face.v1.getPosition();
            } else if (dot2 > dot3 && dot2 > dot1) {
                linedVertex = 2;
                linedVertexPos = face.v2.getPosition();
            } else {
                linedVertex = 3;
                linedVertexPos = face.v3.getPosition();
            }

            if ((linedVertexPos.minus(startPos)).module() > (linedVertexPos.minus(endPos)).module()) {
                breakFaceInFive(facePos, startPos, endPos, linedVertex);
            } else {
                breakFaceInFive(facePos, endPos, startPos, linedVertex);
            }
        }
    }

    public void breakFaceInTwo(int facePos, Vector3f newPos, int splitEdge) {
        Face face = faces.get(facePos);
        //faces.RemoveFace(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }

        faces.remove(facePos);
    }

    public void breakFaceInTwo(int facePos, Vector3f newPos, Vertex endVertex) {
        Face face = faces.get(facePos);
        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }

        faces.remove(facePos);
    }

    public void breakFaceInThree(int facePos, Vector3f newPos1, Vector3f newPos2, int splitEdge) {
        Face face = (faces.get(facePos));
        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex1, vertex2, face.v3);
            addFace(vertex2, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex1, vertex2, face.v1);
            addFace(vertex2, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex1, vertex2, face.v2);
            addFace(vertex2, face.v1, face.v2);
        }

        // Moved this
        faces.remove(facePos);
    }

    public void breakFaceInThree(int facePos, Vector3f newPos, Vertex endVertex) {
        Face face = (faces.get(facePos));
        //faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
        } else {
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
        }

        // Moved this
        faces.remove(facePos);
    }


    public void breakFaceInThree(int facePos, Vector3f newPos1, Vector3f newPos2, Vertex startVertex, Vertex endVertex) {
        Face face = (faces.get(facePos));
        // Moved remove to the end
        //faces.remove(facePos);

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (startVertex.equals(face.v1) && endVertex.equals(face.v2)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v1, vertex2, face.v3);
            addFace(vertex1, face.v2, vertex2);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v1)) {
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex2, face.v2, vertex1);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v3)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v2, vertex2, face.v1);
            addFace(vertex1, face.v3, vertex2);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v2)) {
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex2, face.v3, vertex1);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v1)) {
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v3, vertex2, face.v2);
            addFace(vertex1, face.v1, vertex2);
        } else {
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex2, face.v1, vertex1);
        }

        // Moved this
        faces.remove(facePos);
    }

    public void breakFaceInThree(int facePos, Vector3f newPos) {
        Face face = (faces.get(facePos));
        //faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        addFace(face.v1, face.v2, vertex);
        addFace(face.v2, face.v3, vertex);
        addFace(face.v3, face.v1, vertex);

        faces.remove(facePos);
    }

    public void breakFaceInFour(int facePos, Vector3f newPos1, Vector3f newPos2, Vertex endVertex) {
        Face face = (faces.get(facePos));

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(vertex1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(vertex1, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v3, vertex1, vertex2);
            addFace(vertex1, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
        }

        // Moved this
        faces.remove(facePos);
    }

    public void breakFaceInFive(int facePos, Vector3f newPos1, Vector3f newPos2, int linedVertex) {
        Face face = (faces.get(facePos));

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (linedVertex == 1) {
            addFace(face.v2, face.v3, vertex1);
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v2, vertex2, face.v1);
            addFace(face.v3, face.v1, vertex2);
        } else if (linedVertex == 2) {
            addFace(face.v3, face.v1, vertex1);
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v3, vertex2, face.v2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v1, face.v2, vertex1);
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v1, vertex2, face.v3);
            addFace(face.v2, face.v3, vertex2);
        }

        // Calling it at the end, because the java garbage collection won't destroy it until after the end of the function,
        // (when there is nothing referencing it anymore)
        faces.remove(facePos);
    }

    //-----------------------------------OTHERS-------------------------------------//

    public void classifyFaces(Object3D object) {
        for (Face face : getFaces()) {
            face.v1.addVertex(face.v2);
            face.v1.addVertex(face.v3);
            face.v2.addVertex(face.v1);
            face.v2.addVertex(face.v3);
            face.v3.addVertex(face.v1);
            face.v3.addVertex(face.v2);
        }

        int test = 0;
        for (Face face : getFaces()) {

            if (!face.simpleClassify()) {

                face.rayTraceClassify(object);
                test++;

                if (face.v1.getStatus() == Vertex.UNKNOWN) {
                    face.v1.mark(face.getStatus(), this);
                    Vertex.test = 0;
                }
                if (face.v2.getStatus() == Vertex.UNKNOWN) {
                    face.v2.mark(face.getStatus(), this);
                    Vertex.test = 0;
                }
                if (face.v3.getStatus() == Vertex.UNKNOWN) {
                    face.v3.mark(face.getStatus(), this);
                    Vertex.test = 0;
                }
            }
        }
    }

    void invertInsideFaces() {
        for (Face face : getFaces()) {
            if (face.getStatus() == Face.INSIDE) {
                face.invert();
            }
        }
    }

}
