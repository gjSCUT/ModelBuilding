package com.bn.csgStruct;


import com.bn.Main.Constant;
import com.bn.csgStruct.Struct.Result;


public class Line {
    private Vector3f point;
    private Vector3f direction;


    public Line() {
        point = new Vector3f(0, 0, 0);
        direction = new Vector3f(1, 0, 0);
    }

    public Line(Face face1, Face face2) {
        point = new Vector3f(0, 0, 0);
        direction = new Vector3f(1, 0, 0);
        Vector3f normalFace1 = face1.getNormal();
        Vector3f normalFace2 = face2.getNormal();


        direction = normalFace1.cross(normalFace2);

        if (!(direction.module() < Constant.TOL)) {
            float d1 = -normalFace1.multiV(face1.v1.getPosition());
            float d2 = -normalFace2.multiV(face2.v1.getPosition());
            if (Math.abs(direction.x) > Constant.TOL) {
                point.x = 0;
                point.y = (d2 * normalFace1.z - d1 * normalFace2.z) / direction.x;
                point.z = (d1 * normalFace2.y - d2 * normalFace1.y) / direction.x;
            } else if (Math.abs(direction.y) > Constant.TOL) {
                point.x = (d1 * normalFace2.z - d2 * normalFace1.z) / direction.y;
                point.y = 0;
                point.z = (d2 * normalFace1.x - d1 * normalFace2.x) / direction.y;
            } else {
                point.x = (d2 * normalFace1.y - d1 * normalFace2.y) / direction.z;
                point.y = (d1 * normalFace2.x - d2 * normalFace1.x) / direction.z;
                point.z = 0;
            }
        }

        direction.normalize();
    }

    public Line(Vector3f directioni, Vector3f pointi) {
        direction = directioni;
        point = pointi;
        direction.normalize();
    }

    public Vector3f getPoint() {
        return point;
    }

    public void setPoint(Vector3f pointi) {
        point = pointi;
    }

    public Vector3f getDirection() {
        return direction;
    }

    void setDirection(Vector3f directioni) {
        this.direction = directioni;
    }

    public float computePointToPointDistance(Vector3f otherPoint) {
        float distance = (point.minus(otherPoint)).module();
        Vector3f vec = new Vector3f(otherPoint.x - point.x, otherPoint.y - point.y, otherPoint.z - point.z);
        vec.normalize();
        if (vec.multiV(direction) < 0) {
            return -distance;
        } else {
            return distance;
        }
    }

    public Vector3f computeLineIntersection(Line otherLine) {

        Vector3f linePoint = otherLine.getPoint();
        Vector3f lineDirection = otherLine.getDirection();


        float t;
        if (Math.abs(direction.y * lineDirection.x - direction.x * lineDirection.y) > Constant.TOL) {
            t = (-point.y * lineDirection.x + linePoint.y * lineDirection.x + lineDirection.y * point.x - lineDirection.y * linePoint.x) / (direction.y * lineDirection.x - direction.x * lineDirection.y);
        } else if (Math.abs(-direction.x * lineDirection.z + direction.z * lineDirection.x) > Constant.TOL) {
            t = -(-lineDirection.z * point.x + lineDirection.z * linePoint.x + lineDirection.x * point.z - lineDirection.x * linePoint.z) / (-direction.x * lineDirection.z + direction.z * lineDirection.x);
        } else if (Math.abs(-direction.z * lineDirection.y + direction.y * lineDirection.z) > Constant.TOL) {
            t = (point.z * lineDirection.y - linePoint.z * lineDirection.y - lineDirection.z * point.y + lineDirection.z * linePoint.y) / (-direction.z * lineDirection.y + direction.y * lineDirection.z);
        } else {
            return new Vector3f(0, 0, 0);
        }

        Vector3f vResult = point.add(direction.multiK(t));

        return vResult;
    }

    public Result computePlaneIntersection(Vector3f normal, Vector3f planePoint) {
        Result result = new Result();
        result.bIntersectResult = true;

        float D = (normal.multiV(planePoint)) * -1.0f;

        float numerator = (normal.multiV(point)) + D;
        float denominator = (normal.multiV(direction));

        if (Math.abs(denominator) < Constant.TOL) {
            if (Math.abs(numerator) < Constant.TOL) {
                result.intersectionPoint = point;
                return result;
            } else {
                result.bIntersectResult = false;
                result.intersectionPoint = new Vector3f(0, 0, 0);
                return result;
            }
        } else {
            float t = -numerator / denominator;
            Vector3f resultPoint = point.add(direction.multiK(t));

            result.intersectionPoint = resultPoint;
            return result;
        }
    }

    public float LineRandomNum() {
        int nRand = (int) (Math.random() % 10000);
        float fRand = (float) nRand;
        fRand *= 0.0001;
        fRand *= 2.0f;
        fRand -= 1.0f;
        return fRand;
    }

    public void perturbDirection() {

        direction.x += LineRandomNum() * 0.001f;
        direction.y += LineRandomNum() * 0.001f;
        direction.z += LineRandomNum() * 0.001f;

    }
}
