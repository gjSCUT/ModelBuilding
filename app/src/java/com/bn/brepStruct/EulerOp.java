package com.bn.brepStruct;


import com.bn.csgStruct.Point3f;

public class EulerOp {
    public CSolid solid;

    public EulerOp() {
        solid = null;
    }

    //////////////////////////////////////////////////////////////////////////
    // Make one solid, one face, one loop and one vertex
    // \param point: the coordinate of the new vertex
    // \return: the new solid
    //////////////////////////////////////////////////////////////////////////
    CSolid mvfs(Point3f point) {
        CSolid newSolid;
        CFace newFace;
        CLoop newLoop;
        CVertex newVert;
        newSolid = new CSolid();
        newFace = new CFace();
        newLoop = new CLoop();
        newVert = new CVertex();

        // Set the coordinate of the new vertex
        newVert.SetCoord(point);

        // Add the new face to the new solid
        newSolid.AddFace(newFace);

        // Add the new vertex to the new solid
        newSolid.AddVertex(newVert);

        // Add the new loop to the new face
        newFace.AddLoop(newLoop);

        return newSolid;
    }

    //////////////////////////////////////////////////////////////////////////
    // Make one solid, one face, one loop and one vertex
    // \param point: the coordinate of the new vertex
    // \return: boolean
    //////////////////////////////////////////////////////////////////////////
    public boolean kvsf(Point3f point) {
        if (point == null) {
            return false;
        }

        return false;
    }


    //////////////////////////////////////////////////////////////////////////
    // Make one edge, two halfedges and one vertex
    // \param point: the new vertex of the edge
    // \param v1: the start vertex of the edge
    // \param lp: the loop contain the edge
    // \return: one of the halfedges
    //////////////////////////////////////////////////////////////////////////
    CHalfEdge mev(Point3f point1, Point3f point2, CLoop lp) {
        CSolid pSolid = lp.host_f.host_s;
        CVertex v1, newVert;
        CEdge newEdge;
        CHalfEdge he1, he2;
        newVert = new CVertex();
        newEdge = new CEdge();
        he1 = new CHalfEdge();
        he2 = new CHalfEdge();

        // Find the start vertex of the edge
        v1 = pSolid.FindVertex(point1);
        if (v1 == null) {
            return null;
        }

        // Set the coordinate of the new vertex
        newVert.SetCoord(point2);

        // Set v1 as the start vertex of halfedge1
        he1.SetVertex(v1);

        // Set v2 as the start vertex of halfedge2
        he2.SetVertex(newVert);

        // Add two new halfedges to the loop
        lp.AddHalfEdge(he1, he2);

        // Set the new edge to new halfedges
        he1.edge = he2.edge = newEdge;
        newEdge.he1 = he1;
        newEdge.he2 = he2;

        // Add the new edge to the solid
        pSolid.AddEdge(newEdge);

        // Add the new vertex to the solid
        pSolid.AddVertex(newVert);

        return he1;
    }

    //////////////////////////////////////////////////////////////////////////
    // delete one edge, two halfedges and one vertex
    // \param point: the new vertex of the edge
    // \param v1: the start vertex of the edge
    // \param lp: the loop contain the edge
    // \return: one of the halfedges
    //////////////////////////////////////////////////////////////////////////
    CHalfEdge kev(Point3f point, CLoop lp) {
        CSolid pSolid = lp.host_f.host_s;
        CVertex v1, newVert;
        CEdge newEdge;
        CHalfEdge he1, he2;
        newVert = new CVertex();
        newEdge = new CEdge();
        he1 = new CHalfEdge();
        he2 = new CHalfEdge();

        // Find the start vertex of the edge
        v1 = pSolid.FindVertex(point);
        if (v1 == null) {
            return null;
        }

        // Set the coordinate of the new vertex
        newVert.SetCoord(point);

        // Set v1 as the start vertex of halfedge1
        he1.SetVertex(v1);

        // Set v2 as the start vertex of halfedge2
        he2.SetVertex(newVert);

        // Add two new halfedges to the loop
        lp.AddHalfEdge(he1, he2);

        // Set the new edge to new halfedges
        he1.edge = he2.edge = newEdge;
        newEdge.he1 = he1;
        newEdge.he2 = he2;

        // Add the new edge to the solid
        pSolid.AddEdge(newEdge);

        // Add the new vertex to the solid
        pSolid.AddVertex(newVert);

        return he1;
    }

    //////////////////////////////////////////////////////////////////////////
    // Make one edge, one face and one loop
    // \param point1: the latest vertex of the solid
    // \param point2: the old vertex of the edge
    // \param lp: the loop contain the edge
    // \return: the new loop
    //////////////////////////////////////////////////////////////////////////
    CLoop mef(Point3f point1, Point3f point2, CLoop lp) {
        CSolid pSolid = lp.host_f.host_s;
        CVertex v1, v2;
        // Find the two given vertex
        v1 = pSolid.FindVertex(point1);
        if (v1 == null) {
            return null;
        }
        v2 = pSolid.FindVertex(point2);
        if (v2 == null) {
            return null;
        }

        // two vertex must in the same loop
        if (!lp.IsVertexIn(v1) && !lp.IsVertexIn(v2))
            return null;

        CFace newFace;
        CLoop newLoop;
        CEdge newEdge;
        CHalfEdge he1, he2, temphe1, temphe2;
        newFace = new CFace();
        newLoop = new CLoop();
        newEdge = new CEdge();
        he1 = new CHalfEdge();
        he2 = new CHalfEdge();

        // Find two halfedges start with two vertexs
        temphe1 = lp.FindHostHalfEdge(v1);
        temphe2 = lp.FindHostHalfEdge(v2);

        // Change halfedges' host loop to new loop
        temphe1.prev.next = null;
        newLoop.AddHalfEdge(temphe1, null);
        while (temphe1 != temphe2)
            temphe1 = temphe1.next;
        temphe1.prev.next = he2;
        he2.prev = temphe1.prev;
        he2.next = newLoop.GetHalfEdgeHead();
        newLoop.GetHalfEdgeHead().prev = he2;
        temphe1.prev = null;

        // Add halfedge start with vertex one to the old loop % close this loop
        lp.AddHalfEdge(he1, null);
        he1.next = temphe2;
        temphe2.prev = he1;

        // Set two halfedges' start vertex and adjacent
        he1.SetVertex(v1);
        he2.SetVertex(v2);
        he1.adj = he2;
        he2.adj = he1;

        // Set the new edge and add to the solid
        newEdge.he1 = he1;
        newEdge.he2 = he2;
        pSolid.AddEdge(newEdge);

        // Add new face to the solid
        pSolid.AddFace(newFace);

        // Add new loop to the new face
        newFace.AddLoop(newLoop);

        return newLoop;
    }

    //////////////////////////////////////////////////////////////////////////
    // Kill one edge & make one loop
    // \param point1: one vertex of the edge need to be killed
    // \param point2: another vertex of the edge need to be killed
    // \param lp: the loop contain the edge
    // \return: the new loop
    //////////////////////////////////////////////////////////////////////////
    CLoop kemr(Point3f point1, Point3f point2, CLoop lp) {
        CSolid pSolid = lp.host_f.host_s;
        CVertex v1, v2;
        // Find the two given vertex
        v1 = pSolid.FindVertex(point1);
        if (v1 == null) {
            return null;
        }
        v2 = pSolid.FindVertex(point2);
        if (v2 == null) {
            return null;
        }
        // two vertex must in the same loop
        if (!lp.IsVertexIn(v1) && !lp.IsVertexIn(v2))
            return null;

        CLoop newLoop;
        CHalfEdge he1, he2;
        CEdge edge;
        newLoop = new CLoop();

        // Find two halfedges start with two vertexs
        he1 = lp.GetHalfEdgeHead();
        while (he1 != null) {
            if (he1.GetVertex() == v1 && he1.next.GetVertex() == v2)
                break;
            he1 = he1.next;
        }
        he2 = he1.adj;

        // Make a newloop and delete two halfedges
        newLoop.AddHalfEdge(he1.next, null);
        he2.prev.next = he1.next;
        he1.next.prev = he2.prev;
        he1.prev.next = he2.next;
        he2.next.prev = he1.prev;
        he1.next = null;
        he1.prev = null;
        he2.next = null;
        he2.prev = null;


        // Find the edge and delete it
        edge = pSolid.GetEdgeHead();
        while (edge != null) {
            if (edge.he1 == he1)
                break;
            edge = edge.next;
        }
        edge.prev.next = edge.next;
        edge.next.prev = edge.prev;
        edge.next = edge.prev = null;

        // Add new loop to the face
        lp.host_f.AddLoop(newLoop);

        return newLoop;
    }

    //////////////////////////////////////////////////////////////////////////
    // Kill one face & make one loop, so make a hole
    // \param outlp: the out loop of the face need to be added a loop
    // \param lp: the loop of the face need to be killed
    // \return: void
    //////////////////////////////////////////////////////////////////////////
    void kfmrh(CLoop outlp, CLoop lp) {
        if (outlp == null || lp == null) {
            return;
        }

        CSolid pSolid = lp.host_f.host_s;

        // Add the loop to the face
        outlp.host_f.AddLoop(lp);

        // Get the face need to be killed
        CFace pFace = pSolid.GetFaceEnd();

        // Delete it
        pFace.prev.next = null;

    }

    //////////////////////////////////////////////////////////////////////////
    // make one face & kill one loop, so kill a hole
    // \param outlp: the out loop of the face need to be added a loop
    // \param lp: the loop of the face need to be killed
    // \return: void
    //////////////////////////////////////////////////////////////////////////
    void mfkrh(CLoop outlp, CLoop lp) {
        if (outlp == null || lp == null) {
            return;
        }

        CSolid pSolid = lp.host_f.host_s;

        // Add the loop to the face
        outlp.host_f.AddLoop(lp);

        // Get the face need to be make
        CFace pFace = pSolid.GetFaceEnd();

        // make it
        pFace.prev.next = null;

    }

    //////////////////////////////////////////////////////////////////////////
    // Sweep a solid on the face
    // \param pFace: the face need to be sweeped
    // \param dx, dy, dz: the direction and length
    // \return: void
    //////////////////////////////////////////////////////////////////////////
    void sweep(CFace pFace, float dx, float dy, float dz) {
        CFace pEnd;
        CHalfEdge pHe, pHead;
        CLoop pLoop, newLoop, pOutLoop = new CLoop();
        Point3f pPoint;
        Point3f point1, point2 = new Point3f(), first = new Point3f(), last = new Point3f();
        boolean bOut = true; // Show that if is the outloop of the top face

        // Remember the last inside loop's face
        pEnd = pFace.host_s.GetFaceEnd();
        // Start with the second face, because the first face is for buttom
        pFace = pFace.next;
        while (pFace != null) {
            // Get the first point of the loop
            newLoop = pLoop = pFace.GetLoopHead();
            pHe = pHead = pLoop.GetHalfEdgeHead();
            pPoint = pHe.GetVertex().vcoord;
            point1 = pPoint;
            // first[] is used for close the top face
            // last[] is used for side face
            first.x = point1.x + dx;
            first.y = point1.y + dy;
            first.z = point1.z + dz;
            last.x = point1.x + dx;
            last.y = point1.y + dy;
            last.z = point1.z + dz;
            point2.x = point1.x + dx;
            point2.y = point1.y + dy;
            point2.z = point1.z + dz;
            // Make the new edge
            mev(point1, point2, pLoop);
            // Goto next halfedge
            pHe = pHe.next;
            while (pHe.GetVertex() != pHead.GetVertex()) {
                // Get the point
                pPoint = pHe.GetVertex().vcoord;
                point1.x = pPoint.x;
                point1.y = pPoint.y;
                point1.z = pPoint.z;
                point2.x = point1.x + dx;
                point2.y = point1.y + dy;
                point2.z = point1.z + dz;
                // Make the new edge
                mev(point1, point2, newLoop);
                // Make a new side face
                newLoop = mef(point2, last, newLoop);
                // Remember the lastest point
                last.x = point2.x;
                last.y = point2.y;
                last.z = point2.z;
                pHe = pHe.next;
            }
            // Close the top face
            newLoop = mef(first, last, newLoop);

            if (bOut) {
                pOutLoop = newLoop;
                bOut = false;
            } else
                kfmrh(pOutLoop, newLoop);

            if (pFace == pEnd)
                break;

            pFace = pFace.next;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    void CalcNormal(CSolid pSolid) {
        CFace pFace;
        CHalfEdge pHalfEdge;
        CVertex pVert1, pVert2, pVert3;
        Point3f sub1 = new Point3f(), sub2 = new Point3f(), normal = new Point3f();
        float sum_norm;

        pFace = pSolid.GetFaceHead();
        while (pFace != null) {
            pHalfEdge = pFace.GetLoopHead().GetHalfEdgeHead();
            pVert1 = pHalfEdge.GetVertex();
            pVert2 = pHalfEdge.next.GetVertex();
            pVert3 = pHalfEdge.next.next.GetVertex();
            sub1.x = pVert1.vcoord.x - pVert2.vcoord.x;
            sub1.y = pVert1.vcoord.y - pVert2.vcoord.y;
            sub1.z = pVert1.vcoord.z - pVert2.vcoord.z;
            sub2.x = pVert2.vcoord.x - pVert3.vcoord.x;
            sub2.y = pVert2.vcoord.y - pVert3.vcoord.y;
            sub2.z = pVert2.vcoord.z - pVert3.vcoord.z;
            normal.x = sub1.y * sub2.z - sub2.y * sub1.z;
            normal.y = sub1.z * sub2.x - sub2.z * sub1.x;
            normal.z = sub1.x * sub2.y - sub2.x * sub1.y;
            sum_norm = normal.x + normal.y + normal.z;
            sum_norm = Math.abs(sum_norm);
            normal.x = normal.x / sum_norm;
            normal.y = normal.y / sum_norm;
            normal.z = normal.z / sum_norm;
            pFace.feq.x = normal.x;
            pFace.feq.y = normal.y;
            pFace.feq.z = normal.z;
            pFace = pFace.next;
        }
    }

    /*
     * 	float[][] point = new float{ {0.0, 0.0, 0.0}, {1.0, 0.0, 0.0}, {1.0, 1.0, 0.0}, {0.0, 1.0, 0.0},
                                {0.0, 1.0, 1.0}, {1.0, 1.0, 1.0}, {1.0, 0.0, 1.0}, {0.0, 0.0, 1.0},
                                {0.2, 0.2, 1.0}, {0.8, 0.2, 1.0}, {0.8, 0.8, 1.0}, {0.2, 0.8, 1.0},
                                {0.2, 0.8, 0.0}, {0.8, 0.8, 0.0}, {0.8, 0.2, 0.0}, {0.2, 0.2, 0.0} };
     */
    //////////////////////////////////////////////////////////////////////////
    CSolid CreateBlock(float[] vertices, int TriangleNum) {
        CSolid pSolid, pTemp;
        CLoop pLoop;
        Point3f[] point = new Point3f[16];
        for (int i = 0; i < TriangleNum; i++) {
            point[i] = new Point3f(vertices[3 * i], vertices[3 * i + 1], vertices[3 * i + 2]);
        }

        // No-Sweeping
        pSolid = mvfs(point[0]);
        pLoop = pSolid.GetFaceHead().GetLoopHead();
        mev(point[0], point[1], pLoop);
        mev(point[1], point[2], pLoop);
        mev(point[2], point[3], pLoop);
        pLoop = mef(point[3], point[0], pLoop);
        mev(point[3], point[4], pLoop);
        mev(point[2], point[5], pLoop);
        pLoop = mef(point[5], point[4], pLoop);
        mev(point[1], point[6], pLoop);
        pLoop = mef(point[6], point[5], pLoop);
        mev(point[0], point[7], pLoop);
        pLoop = mef(point[7], point[6], pLoop);
        pLoop = mef(point[4], point[7], pLoop);

        mev(point[7], point[8], pLoop);
        mev(point[8], point[9], pLoop);
        mev(point[9], point[10], pLoop);
        mev(point[10], point[11], pLoop);
        pLoop = mef(point[11], point[8], pLoop);
        kemr(point[7], point[8], pLoop.host_f.prev.GetLoopHead());

        mev(point[11], point[12], pLoop);
        mev(point[10], point[13], pLoop);
        mev(point[9], point[14], pLoop);
        mev(point[8], point[15], pLoop);
        pLoop = mef(point[13], point[12], pLoop);
        pLoop = mef(point[14], point[13], pLoop);
        pLoop = mef(point[15], point[14], pLoop);
        pLoop = mef(point[12], point[15], pLoop);

        kfmrh(pSolid.GetFaceHead().GetLoopHead(), pLoop);
        CalcNormal(pSolid);

        //Log();

        // Insert the new solid
        if (solid == null)
            solid = pSolid;
        else {
            pTemp = solid;
            while (pTemp.next != null)
                pTemp = pTemp.next;
            pTemp.next = pSolid;
        }

        return pSolid;
    }

    CSolid HighSweep(Point3f point[], int[] pointnum, int loopnum, Point3f vector, float len) {
        CSolid pSolid, pTemp;
        CLoop pLoop, pHead;
        int i, j, pos;

        // Create the solid
        pSolid = mvfs(point[1]);
        pHead = pLoop = pSolid.GetFaceHead().GetLoopHead();

        // Create the out loop
        for (i = 1; i < pointnum[1] - 1; i++) {
            mev(point[i], point[i + 1], pLoop);
        }
        mef(point[i], point[1], pLoop);
        pos = i + 1;

        // Create the inside loops
        for (i = 2; i < loopnum; i++) {
            mev(point[1], point[pos + 1], pHead);
            for (j = 1; j < pointnum[i] - 1; j++) {
                mev(point[pos + j], point[pos + j + 1], pHead);
            }
            mef(point[pos + j], point[pos + 1], pHead);
            kemr(point[1], point[pos + 1], pHead);
            pos = pos + j + 1;
        }

        sweep(pSolid.GetFaceHead(), vector.x * len, vector.y * len, vector.z * len);
        CalcNormal(pSolid);

        // Insert the new solid
        if (solid == null)
            solid = pSolid;
        else {
            pTemp = solid;
            while (pTemp.next != null)
                pTemp = pTemp.next;
            pTemp.next = pSolid;
        }

        return pSolid;
    }

}
