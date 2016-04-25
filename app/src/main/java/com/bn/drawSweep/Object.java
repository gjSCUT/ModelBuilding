package com.bn.drawSweep;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Object {
    //constant
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    //shader source code
    private static final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    //create glProgream
    static int mProgram;
    static int vertexShader;
    static int fragmentShader;
    final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    int vertexCount;
    int mMVPMatrixHandle;
    int mPositionHandle;
    int mColorHandle;
    FloatBuffer vertexBuffer;
    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    {
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    //shader compile function
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}

class Triangle extends Object {


    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        float[] vertex = {
                v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z
        };
        setPos(vertex);
    }

    public Triangle(float[] position) {
        setPos(position);
    }

    public void setPos(float[] position) {
        for (int i = 0; i < 9; i++)
            Log.d("position", "" + position[i]);
        vertexCount = position.length / COORDS_PER_VERTEX;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                position.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(position);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    public void setPos(Vertex v1, Vertex v2, Vertex v3) {
        float[] vertex = {
                v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z
        };
        setPos(vertex);
    }

    public void draw(float[] mTransMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        float[] mMVPMatrix = new float[16];
        for (int i = 0; i < 16; i++) {
            mMVPMatrix[i] = MyGLRender.mMVPMatrix[i];
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mTransMatrix, 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

class Vertex {
    public float x, y, z;

    Vertex() {
        x = y = z = 0;
    }

    Vertex(float _x, float _y, float _z) {
        x = _x;
        y = _y;
        z = _z;
    }
}

class Vertex2f {
    public float x, y;

    Vertex2f() {
        x = 0;
        y = 0;
    }

    Vertex2f(float _x, float _y) {
        x = _x;
        y = _y;
    }

}

class Square {
    // square have two triangle
    Triangle t1, t2;
    //transform matrix
    float[] mTransMatrix = new float[16];
    //vertex data
    private Vertex vertex[] = new Vertex[4];

    public Square() {
        for (int i = 0; i < 4; i++) {
            vertex[i] = new Vertex();
        }
        t1 = new Triangle(vertex[0], vertex[1], vertex[2]);
        t2 = new Triangle(vertex[2], vertex[3], vertex[0]);
        Matrix.setIdentityM(mTransMatrix, 0);
    }

    public void setPos(Vertex v, int num) {
        vertex[num] = v;
        t1.setPos(vertex[0], vertex[1], vertex[2]);
        t2.setPos(vertex[2], vertex[3], vertex[0]);
    }

    public void setPos(float _x, float _y, float _z, int num) {
        Vertex v = new Vertex(_x, _y, _z);
        vertex[num] = v;
        t1.setPos(vertex[0], vertex[1], vertex[2]);
        t2.setPos(vertex[2], vertex[3], vertex[0]);
    }

    public void draw() {
        t1.draw(mTransMatrix);
        t2.draw(mTransMatrix);
    }
}

class LoopLine extends Object {
    float[] mTransMatrix = new float[16];
    private ArrayList<Vertex2f> vertexList;

    LoopLine() {
        vertexList = new ArrayList<Vertex2f>();
        Matrix.setIdentityM(mTransMatrix, 0);
        convertToBuffer();
    }

    public void add(float x, float y) {
        vertexList.add(new Vertex2f(x, y));
        convertToBuffer();
    }

    private void convertToBuffer() {
        float position[] = new float[vertexList.size() * 3];
        int c = 0;
        for (Vertex2f v : vertexList) {
            position[c++] = v.x;
            position[c++] = v.y;
            position[c++] = 0;
        }
        vertexCount = position.length / COORDS_PER_VERTEX;
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                position.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(position);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }


    public void draw() {

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        float[] mMVPMatrix = new float[16];
        for (int i = 0; i < 16; i++) {
            mMVPMatrix[i] = MyGLRender.mMVPMatrix[i];
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mTransMatrix, 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glLineWidth(10);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

