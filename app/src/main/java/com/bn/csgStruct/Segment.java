package com.bn.csgStruct;

import com.bn.Main.Constant;


public class Segment {
    public final static int VERTEX = 1;
    public final static int FACE = 2;
    public final static int EDGE = 3;
    /**
     * line resulting from the two planes intersection
     */
    Line line;
    /**
     * shows how many ends were already defined
     */
    int index;
    /**
     * distance from the segment starting point to the point defining the plane
     */
    float startDist;
    /**
     * distance from the segment ending point to the point defining the plane
     */
    float endDist;
    /**
     * starting point status relative to the face
     */
    int startType;
    /**
     * intermediate status relative to the face
     */
    int middleType;
    /**
     * ending point status relative to the face
     */
    int endType;
    /**
     * nearest vertex from the starting point
     */
    Vertex startVertex;
    /**
     * nearest vertex from the ending point
     */
    Vertex endVertex;
    /**
     * start of the intersection point
     */
    Vector3f startPos;
    /**
     * end of the intersection point
     */
    Vector3f endPos;


    Segment() {
        line = null;
        index = 0;

        startDist = 0;
        endDist = 0;

        startType = 0;
        middleType = 0;
        endType = 0;

        startVertex = null;
        endVertex = null;

        startPos = null;
        /** end of the intersection point */
        endPos = null;
    }

    public Segment(Line l, Face face, int sign1, int sign2, int sign3) {
        //TOL = 1e-10f;

        line = l;
        index = 0;

        if (sign1 == 0) {
            setVertex(face.v1);
            if (sign2 == sign3) {
                setVertex(face.v1);
            }
        }

        if (sign2 == 0) {
            setVertex(face.v2);
            if (sign1 == sign3) {
                setVertex(face.v2);
            }
        }

        if (sign3 == 0) {
            setVertex(face.v3);
            if (sign1 == sign2) {
                setVertex(face.v3);
            }
        }

        if (getNumEndsSet() != 2) {
            //EDGE is an end
            if ((sign1 == 1 && sign2 == -1) || (sign1 == -1 && sign2 == 1)) {
                setEdge(face.v1, face.v2);
            }
            //EDGE is an end
            if ((sign2 == 1 && sign3 == -1) || (sign2 == -1 && sign3 == 1)) {
                setEdge(face.v2, face.v3);
            }
            //EDGE is an end
            if ((sign3 == 1 && sign1 == -1) || (sign3 == -1 && sign1 == 1)) {
                setEdge(face.v3, face.v1);
            }
        }
    }

    //-------------------------------------GETS-------------------------------------//

    /**
     * Gets the start vertex
     *
     * @return start vertex
     */
    public Vertex getStartVertex() {
        return startVertex;
    }

    /**
     * Gets the end vertex
     *
     * @return end vertex
     */
    public Vertex getEndVertex() {
        return endVertex;
    }

    /**
     * Gets the distance from the origin until the starting point
     *
     * @return distance from the origin until the starting point
     */
    public float getStartDistance() {
        return startDist;
    }

    /**
     * Gets the distance from the origin until ending point
     *
     * @return distance from the origin until the ending point
     */
    public float getEndDistance() {
        return endDist;
    }

    /**
     * Gets the type of the starting point
     *
     * @return type of the starting point
     */
    public int getStartType() {
        return startType;
    }

    /**
     * Gets the type of the segment between the starting and ending points
     *
     * @return type of the segment between the starting and ending points
     */
    public int getIntermediateType() {
        return middleType;
    }

    /**
     * Gets the type of the ending point
     *
     * @return type of the ending point
     */
    public int getEndType() {
        return endType;
    }

    /**
     * Gets the number of ends already set
     *
     * @return number of ends already set
     */
    public int getNumEndsSet() {
        return index;
    }

    /**
     * Gets the starting position
     *
     * @return start position
     */
    public Vector3f getStartPosition() {
        return startPos;
    }

    /**
     * Gets the ending position
     *
     * @return ending position
     */
    public Vector3f getEndPosition() {
        return endPos;
    }

    //------------------------------------OTHERS------------------------------------//

    /**
     * Checks if two segments intersect
     *
     * @param segment the other segment to check the intesection
     * @return true if the segments intersect, false otherwise
     */
    public boolean intersect(Segment segment) {
        if (endDist < segment.startDist + Constant.TOL || segment.endDist < startDist + Constant.TOL) {
            return false;
        } else {
            return true;
        }
    }

    //---------------------------------PRIVATES-------------------------------------//

    /**
     * Sets an end as vertex (starting point if none end were defined, ending point otherwise)
     *
     * @param vertex the vertex that is an segment end
     * @return false if all the ends were already defined, true otherwise
     */
    public boolean setVertex(Vertex vertex) {
        //none end were defined - define starting point as VERTEX
        if (index == 0) {
            startVertex = vertex;
            startType = VERTEX;
            startDist = line.computePointToPointDistance(vertex.getPosition());
            startPos = startVertex.getPosition();
            index++;
            return true;
        }
        if (index == 1) {
            endVertex = vertex;
            endType = VERTEX;
            endDist = line.computePointToPointDistance(vertex.getPosition());
            endPos = endVertex.getPosition();
            index++;

            if (true) {
                middleType = VERTEX;
            }
            /*else if(startType==VERTEX)
			{
				middleType = EDGE;
			}
			*/
            if (startDist > endDist) {
                swapEnds();
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets an end as edge (starting point if none end were defined, ending point otherwise)
     *
     * @param vertex1 one of the vertices of the intercepted edge
     * @param vertex2 one of the vertices of the intercepted edge
     * @return false if all ends were already defined, true otherwise
     */
    public boolean setEdge(Vertex vertex1, Vertex vertex2) {
        Vector3f point1 = vertex1.getPosition();
        Vector3f point2 = vertex2.getPosition();

        Vector3f edgeDirection = new Vector3f(point2.x - point1.x, point2.y - point1.y, point2.z - point1.z);
        Line edgeLine = new Line(edgeDirection, point1);

        if (index == 0) {
            startVertex = vertex1;
            startType = EDGE;
            startPos = line.computeLineIntersection(edgeLine);
            startDist = line.computePointToPointDistance(startPos);
            middleType = FACE;
            index++;
            return true;
        } else if (index == 1) {
            endVertex = vertex1;
            endType = EDGE;
            endPos = line.computeLineIntersection(edgeLine);
            endDist = line.computePointToPointDistance(endPos);
            middleType = FACE;
            index++;

            //the ending point distance should be smaller than  starting point distance
            if (startDist > endDist) {
                swapEnds();
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Swaps the starting point and the ending point
     */
    void swapEnds() {
        float distTemp = startDist;
        startDist = endDist;
        endDist = distTemp;

        int typeTemp = startType;
        startType = endType;
        endType = typeTemp;

        Vertex vertexTemp = startVertex;
        startVertex = endVertex;
        endVertex = vertexTemp;

        Vector3f posTemp = startPos;
        startPos = endPos;
        endPos = posTemp;
    }
}
