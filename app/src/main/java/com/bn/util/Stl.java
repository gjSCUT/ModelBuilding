package com.bn.Util;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Stl {

    //把 int转化为byte[]
    public static byte[] intToByte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    // 把float转换为byte[]
    public static byte[] floatToByte(float f) {
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (i * 8));
        }
        return b;
    }

    //写入STL格式文件
    public static void writeInBinary(float[] dataPoint, FileOutputStream fos) throws IOException {
        //计算三角面片数
        int triangles = dataPoint.length / 12;
        try {
            //写入二进制STL文件的前80个字节的文件名
            fos.write(new byte[80]);
            //写入二进制STL文件的4个字节的三角面片数
            fos.write(intToByte(triangles));
            //写入二进制STLw文件的每个三角面片的法向量坐标和三个顶点坐标和其他信息，总共50个字节
            int num = 0;
            for (int i = 0; i < triangles; i++) {
                //写入二进制STLw文件的每个三角面片的48个字节
                float x, y, z;
                for (int j = 0; j < 4; j++) {
                    x = dataPoint[num++];
                    y = dataPoint[num++];
                    z = dataPoint[num++];
                    fos.write(floatToByte(x));
                    fos.write(floatToByte(y));
                    fos.write(floatToByte(z));
                }
                ////写入二进制STLw文件的每个三角面片的剩下两个字节用来描述属性信息
                fos.write(new byte[2]);
            }
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
