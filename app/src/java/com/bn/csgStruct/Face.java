package com.bn.csgStruct;


import com.bn.Main.Constant;
import com.bn.csgStruct.Struct.Result;


public class Face {
    public final static int UNKNOWN = 1;
    public final static int INSIDE = 2;
    public final static int OUTSIDE = 3;
    public final static int SAME = 4;
    public final static int OPPOSITE = 5;
    public final static int UP = 6;
    public final static int DOWN = 7;
    public final static int ON = 8;
    public final static int NONE = 9;
    Vertex v1;
    Vertex v2;
    Vertex v3;
    int status;


    public Face() {
        v1 = null;
        v2 = null;
        v3 = null;

        status = UNKNOWN;
    }

    public Face(Vertex v1i, Vertex v2i, Vertex v3i, int s) {
        v1 = v1i;
        v2 = v2i;
        v3 = v3i;

        status = s;
    }


    public Face(Vertex v1i, Vertex v2i, Vertex v3i) {
        v1 = v1i;
        v2 = v2i;
        v3 = v3i;

        status = UNKNOWN;
    }


    public void setFace(Face pFace) {
        v1 = pFace.v1;
        v2 = pFace.v2;
        v3 = pFace.v3;
        status = pFace.status;
    }

    public boolean match(Face pFace) {
        Face face = pFace;
        return  (v1 == face.v1 && v2 == face.v2 && v3 == face.v3);
    }

    public boolean equals(Face pFace) {
        Face face = pFace;
        boolean cond1 = v1.equals(face.v1) & v2.equals(face.v2) & v3.equals(face.v3);
        boolean cond2 = v1.equals(face.v2) & v2.equals(face.v3) & v3.equals(face.v1);
        boolean cond3 = v1.equals(face.v3) & v2.equals(face.v1) & v3.equals(face.v2);

        return cond1 || cond2 || cond3;
    }

    public Bound getBound() {
        return new Bound(v1.getPosition(), v2.getPosition(), v3.getPosition());
    }

    public Vector3f getNormal() {
        Vector3f p1 = v1.getPosition();
        Vector3f p2 = v2.getPosition();
        Vector3f p3 = v3.getPosition();

        Vector3f xy = p2.minus(p1);
        Vector3f xz = p3.minus(p1);


        Vector3f normal = xy.cross(xz);
        normal.normalize();

        return normal;
    }

    public int getStatus() {
        return status;
    }

    public float getArea() {
        Vector3f p1 = v1.getPosition();
        Vector3f p2 = v2.getPosition();
        Vector3f p3 = v3.getPosition();
        Vector3f xy = p2.minus(p1);
        Vector3f xz = p3.minus(p1);

        float a = xy.module();
        float c = xz.module();
        float B = 0.0f;
        {
            xy.normalize();
            xz.normalize();
            float fDot = xy.multiV(xz);
            float fAngle = (float) Math.acos(fDot);
            B = fAngle;
        }

        return (float) ((a * c * Math.sin(B)) / 2.0f);
    }

    public void invert() {
        Vertex vertexTemp = v2;
        v2 = v1;
        v1 = vertexTemp;
    }

    public boolean simpleClassify() {
        int status1 = v1.getStatus();
        int status2 = v2.getStatus();
        int status3 = v3.getStatus();

        if (status1 == Vertex.INSIDE) {
            status = INSIDE;
            return true;
        }

        if (status1 == Vertex.OUTSIDE) {
            status = OUTSIDE;
            return true;
        }

        if (status2 == Vertex.INSIDE) {
            status = INSIDE;
            return true;
        }

        if (status2 == Vertex.OUTSIDE) {
            status = OUTSIDE;
            return true;
        }

        if (status3 == Vertex.INSIDE) {
            status = INSIDE;
            return true;
        }

        if (status3 == Vertex.OUTSIDE) {
            status = OUTSIDE;
            return true;
        }

        return false;
    }


    public void rayTraceClassify(Object3D object) {
        //creating a ray starting starting at the face baricenter going to the normal direction
        float x0 = (v1.getPosition().x + v2.getPosition().x + v3.getPosition().x) / 3.0f;
        float y0 = (v1.getPosition().y + v2.getPosition().y + v3.getPosition().y) / 3.0f;
        float z0 = (v1.getPosition().z + v2.getPosition().z + v3.getPosition().z) / 3.0f;
        Vector3f p0 = new Vector3f(x0, y0, z0);
        Line ray = new Line(getNormal(), p0);

        boolean success;
        float dotProduct, distance;
        Face closestFace = null;
        float closestDistance;

        //float TOL = 0.0001f;

        do {
            success = true;
            closestDistance = 99999.9f;
            for (Face face : object.getFaces()) {
                dotProduct = face.getNormal().multiV(ray.getDirection()); // dot product

                Result result = ray.computePlaneIntersection(face.getNormal(), face.v1.getPosition());

                if (result.bIntersectResult) {
                    distance = ray.computePointToPointDistance(result.intersectionPoint);

                    if (Math.abs(distance) < Constant.TOL && Math.abs(dotProduct) < Constant.TOL) {
                        ray.perturbDirection();
                        success = false;
                        break;
                    }

                    if (Math.abs(distance) < Constant.TOL && Math.abs(dotProduct) > Constant.TOL) {
                        if (face.hasPoint(result.intersectionPoint)) {
                            closestFace = face;
                            closestDistance = 0;
                            break;
                        }
                    } else if (Math.abs(dotProduct) > Constant.TOL && distance > Constant.TOL) {
                        if (distance < closestDistance) {
                            if (face.hasPoint(result.intersectionPoint)) {
                                closestDistance = distance;
                                closestFace = face;
                            }
                        }
                    }
                }
            }
        } while (!success);

        if (closestFace == null) {
            status = OUTSIDE;
        } else {
            dotProduct = closestFace.getNormal().multiV(ray.getDirection());

            if (Math.abs(closestDistance) < Constant.TOL) {
                if (dotProduct > Constant.TOL) {
                    status = SAME;
                } else if (dotProduct < -Constant.TOL) {
                    status = OPPOSITE;
                }
            } else if (dotProduct > Constant.TOL) {
                status = INSIDE;
            } else if (dotProduct < -Constant.TOL) {
                status = OUTSIDE;
            }
        }
    }

    //------------------------------------PRIVATES----------------------------------//

    /**
     * Checks if the the face contains a point
     *
     * @param point to be tested
     */
    private boolean hasPoint(Vector3f point) {
        int result1, result2, result3;
        Vector3f normal = getNormal();

        //if x is constant...
        if (Math.abs(normal.x) > Constant.TOL) {
            //tests on the x plane
            result1 = linePositionInX(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInX(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInX(point, v3.getPosition(), v1.getPosition());
        }

        //if y is constant...
        else if (Math.abs(normal.y) > Constant.TOL) {
            //tests on the y plane
            result1 = linePositionInY(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInY(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInY(point, v3.getPosition(), v1.getPosition());
        } else {
            //tests on the z plane
            result1 = linePositionInZ(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInZ(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInZ(point, v3.getPosition(), v1.getPosition());
        }

        //if the point is up and down two lines...
        if (((result1 == UP) || (result2 == UP) || (result3 == UP)) && ((result1 == DOWN) || (result2 == DOWN) || (result3 == DOWN))) {
            return true;
        }
        //if the point is on of the lines...
        else if ((result1 == ON) || (result2 == ON) || (result3 == ON)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the position of a point relative to a line in the x plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */
    private int linePositionInX(Vector3f point, Vector3f pointLine1, Vector3f pointLine2) {
        float a, b, z;
        if ((Math.abs(pointLine1.y - pointLine2.y) > Constant.TOL) &&
                (((point.y >= pointLine1.y) && (point.y <= pointLine2.y)) || ((point.y <= pointLine1.y) && (point.y >= pointLine2.y)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.y - pointLine1.y);
            b = pointLine1.z - a * pointLine1.y;
            z = a * point.y + b;
            if (z > point.z + Constant.TOL) {
                return UP;
            } else if (z < point.z - Constant.TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the y plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */

    private int linePositionInY(Vector3f point, Vector3f pointLine1, Vector3f pointLine2) {
        float a, b, z;
        if ((Math.abs(pointLine1.x - pointLine2.x) > Constant.TOL) &&
                (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.x - pointLine1.x);
            b = pointLine1.z - a * pointLine1.x;
            z = a * point.x + b;
            if (z > point.z + Constant.TOL) {
                return UP;
            } else if (z < point.z - Constant.TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the z plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */

    private int linePositionInZ(Vector3f point, Vector3f pointLine1, Vector3f pointLine2) {
        float a, b, y;
        if ((Math.abs(pointLine1.x - pointLine2.x) > Constant.TOL)
                && (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.y - pointLine1.y) / (pointLine2.x - pointLine1.x);
            b = pointLine1.y - a * pointLine1.x;
            y = a * point.x + b;
            if (y > point.y + Constant.TOL) {
                return UP;
            } else if (y < point.y - Constant.TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }

}
