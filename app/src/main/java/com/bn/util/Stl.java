package com.bn.Util;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Stl {

    //�� intת��Ϊbyte[]
    public static byte[] intToByte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// ���λ
        targets[1] = (byte) ((res >> 8) & 0xff);// �ε�λ
        targets[2] = (byte) ((res >> 16) & 0xff);// �θ�λ
        targets[3] = (byte) (res >>> 24);// ���λ,�޷������ơ�
        return targets;
    }

    // ��floatת��Ϊbyte[]
    public static byte[] floatToByte(float f) {
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (i * 8));
        }
        return b;
    }

    //д��STL��ʽ�ļ�
    public static void writeInBinary(float[] dataPoint, FileOutputStream fos) throws IOException {
        //����������Ƭ��
        int triangles = dataPoint.length / 12;
        try {
            //д�������STL�ļ���ǰ80���ֽڵ��ļ���
            fos.write(new byte[80]);
            //д�������STL�ļ���4���ֽڵ�������Ƭ��
            fos.write(intToByte(triangles));
            //д�������STLw�ļ���ÿ��������Ƭ�ķ�����������������������������Ϣ���ܹ�50���ֽ�
            int num = 0;
            for (int i = 0; i < triangles; i++) {
                //д�������STLw�ļ���ÿ��������Ƭ��48���ֽ�
                float x, y, z;
                for (int j = 0; j < 4; j++) {
                    x = dataPoint[num++];
                    y = dataPoint[num++];
                    z = dataPoint[num++];
                    fos.write(floatToByte(x));
                    fos.write(floatToByte(y));
                    fos.write(floatToByte(z));
                }
                ////д�������STLw�ļ���ÿ��������Ƭ��ʣ�������ֽ���������������Ϣ
                fos.write(new byte[2]);
            }
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
