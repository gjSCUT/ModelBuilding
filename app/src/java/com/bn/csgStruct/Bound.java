package com.bn.csgStruct;


import android.opengl.Matrix;

import com.bn.Main.Constant;

import java.util.List;


public class Bound {
    public float xMax;
    public float xMin;
    public float yMax;
    public float yMin;
    public float zMax;
    public float zMin;


    public Bound(Vector3f p1, Vector3f p2, Vector3f p3) {
        xMax = xMin = p1.x;
        yMax = yMin = p1.y;
        zMax = zMin = p1.z;

        checkVertex(p2);
        checkVertex(p3);
    }

    public Bound(float x, float y, float z) {
        xMax = x;
        xMin = -x;
        yMax = y;
        yMin = -y;
        zMax = z;
        zMin = -z;
    }

    public Bound(List<Vector3f> vertices) {
        xMin = yMin = zMin = Float.POSITIVE_INFINITY;
        xMax = yMax = zMax = Float.NEGATIVE_INFINITY;

        for (int i = 1; i < vertices.size(); i++) {
            checkVertex(vertices.get(i));
        }
    }

    public Bound(float[] vertices) {
        xMin = yMin = zMin = Float.POSITIVE_INFINITY;
        xMax = yMax = zMax = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < vertices.length; i += 3) {
            this.add(vertices[i], vertices[i + 1], vertices[i + 2]);
        }
    }


    public void add(Vector3f p) {
        if (p.x < xMin) {
            xMin = p.x;
        }
        if (p.x > xMax) {
            xMax = p.x;
        }
        if (p.y < yMin) {
            yMin = p.y;
        }
        if (p.y > yMax) {
            yMax = p.y;
        }
        if (p.z < zMin) {
            zMin = p.z;
        }
        if (p.z > zMax) {
            zMax = p.z;
        }
    }

    public void add(float x, float y, float z) {
        if (x < xMin) {
            xMin = x;
        }
        if (x > xMax) {
            xMax = x;
        }
        if (y < yMin) {
            yMin = y;
        }
        if (y > yMax) {
            yMax = y;
        }
        if (z < zMin) {
            zMin = z;
        }
        if (z > zMax) {
            zMax = z;
        }
    }


    public Vector3f[] getAllCorners() {
        Vector3f[] result = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            result[i] = getCorner(i);
        }
        return result;
    }


    public Vector3f getCorner(int i) {
        if (i < 0 || i > 7) {
            return null;
        }
        return new Vector3f(
                ((i & 1) == 0) ? xMax : xMin,
                ((i & 2) == 0) ? yMax : yMin,
                ((i & 4) == 0) ? zMax : zMin
        );
    }


    public Bound setToTransformedBox(float[] m) {
        Vector3f[] va = this.getAllCorners();
        float[] transformedCorners = new float[24];
        float[] tmpResult = new float[4];
        int count = 0;
        for (int i = 0; i < va.length; i++) {
            float[] point = new float[]{va[i].x, va[i].y, va[i].z, 1};
            Matrix.multiplyMV(tmpResult, 0, m, 0, point, 0);
            transformedCorners[count++] = tmpResult[0];
            transformedCorners[count++] = tmpResult[1];
            transformedCorners[count++] = tmpResult[2];
        }

        return new Bound(transformedCorners);
    }

    public boolean overlap(Bound bound) {
        if ((xMin > bound.xMax + Constant.TOL) ||
                (xMax < bound.xMin - Constant.TOL) ||
                (yMin > bound.yMax + Constant.TOL) ||
                (yMax < bound.yMin - Constant.TOL) ||
                (zMin > bound.zMax + Constant.TOL) ||
                (zMax < bound.zMin - Constant.TOL)) {
            return false;
        } else {
            return true;
        }
    }

    void checkVertex(Vector3f vertex) {
        if (vertex.x > xMax) {
            xMax = vertex.x;
        } else if (vertex.x < xMin) {
            xMin = vertex.x;
        }

        if (vertex.y > yMax) {
            yMax = vertex.y;
        } else if (vertex.y < yMin) {
            yMin = vertex.y;
        }

        if (vertex.z > zMax) {
            zMax = vertex.z;
        } else if (vertex.z < zMin) {
            zMin = vertex.z;
        }
    }

    public float rayIntersect(
            Vector3f rayStart,
            Vector3f rayDir,
            Vector3f returnNormal,
            float[] faceVector
    ) {
        final float kNoIntersection = Float.POSITIVE_INFINITY;
        boolean inside = true;
        float xt, xn = 0.0f;
        float yt, yn = 0.0f;
        float zt, zn = 0.0f;
        if (rayStart.x < xMin) {
            xt = xMin - rayStart.x;
            if (xt > rayDir.x) {
                return kNoIntersection;
            }
            xt /= rayDir.x;
            inside = false;
            xn = -1.0f;
        } else if (rayStart.x > xMax) {
            xt = xMax - rayStart.x;
            if (xt < rayDir.x) {
                return kNoIntersection;
            }
            xt /= rayDir.x;
            inside = false;
            xn = 1.0f;
        } else {
            xt = -1.0f;
        }

        if (rayStart.y < yMin) {
            yt = yMin - rayStart.y;
            if (yt > rayDir.y) {
                return kNoIntersection;
            }
            yt /= rayDir.y;
            inside = false;
            yn = -1.0f;
        } else if (rayStart.y > yMax) {
            yt = yMax - rayStart.y;
            if (yt < rayDir.y) {
                return kNoIntersection;
            }
            yt /= rayDir.y;
            inside = false;
            yn = 1.0f;
        } else {
            yt = -1.0f;
        }

        if (rayStart.z < zMin) {
            zt = zMin - rayStart.z;
            if (zt > rayDir.z) {
                return kNoIntersection;
            }
            zt /= rayDir.z;
            inside = false;
            zn = -1.0f;
        } else if (rayStart.z > zMax) {
            zt = zMax - rayStart.z;
            if (zt < rayDir.z) {
                return kNoIntersection;
            }
            zt /= rayDir.z;
            inside = false;
            zn = 1.0f;
        } else {
            zt = -1.0f;
        }
        if (inside) {
            if (returnNormal != null) {
                returnNormal = rayDir.multiK(-1);
                returnNormal = returnNormal.normalize();
            }
            return 0.0f;
        }
        int which = 0;
        float t = xt;
        if (yt > t) {
            which = 1;
            t = yt;
        }
        if (zt > t) {
            which = 2;
            t = zt;
        }

        switch (which) {
            case 0: {
                faceVector[0] = 1;
                faceVector[1] = 0;
                faceVector[2] = 0;
                faceVector[3] = 1;
                float y = rayStart.y + rayDir.y * t;
                if (y < yMin || y > yMax) {
                    return kNoIntersection;
                }
                float z = rayStart.z + rayDir.z * t;
                if (z < zMin || z > zMax) {
                    return kNoIntersection;
                }
                if (returnNormal != null) {
                    returnNormal.x = xn;
                    returnNormal.y = 0.0f;
                    returnNormal.z = 0.0f;
                }
            }
            break;
            case 1: {
                faceVector[0] = 0;
                faceVector[1] = 1;
                faceVector[2] = 0;
                faceVector[3] = 1;
                float x = rayStart.x + rayDir.x * t;
                if (x < xMin || x > xMax) {
                    return kNoIntersection;
                }
                float z = rayStart.z + rayDir.z * t;
                if (z < zMin || z > zMax) {
                    return kNoIntersection;
                }
                if (returnNormal != null) {
                    returnNormal.x = 0.0f;
                    returnNormal.y = yn;
                    returnNormal.z = 0.0f;
                }
            }
            break;
            case 2: {
                faceVector[0] = 0;
                faceVector[1] = 0;
                faceVector[2] = 1;
                faceVector[3] = 1;
                float x = rayStart.x + rayDir.x * t;
                if (x < xMin || x > xMax) {
                    return kNoIntersection;
                }
                float y = rayStart.y + rayDir.y * t;
                if (y < yMin || y > yMax) {
                    return kNoIntersection;
                }
                if (returnNormal != null) {
                    returnNormal.x = 0.0f;
                    returnNormal.y = 0.0f;
                    returnNormal.z = zn;
                }
            }
            break;
        }
        return t;
    }
}
