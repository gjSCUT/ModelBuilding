package com.bn.util;

import android.content.res.Resources;
import android.util.Log;

import com.bn.main.MySurfaceView;
import com.bn.csgStruct.Normal;
import com.bn.csgStruct.Struct.Vector3f;
import com.bn.object.Solid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class LoadUtil {

    //�����������Ĳ��
    public static float[] getCrossProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
        //�������ʸ�����ʸ����XYZ��ķ���ABC
        float A = y1 * z2 - y2 * z1;
        float B = z1 * x2 - z2 * x1;
        float C = x1 * y2 - x2 * y1;

        return new float[]{A, B, C};
    }

    //�������
    public static float[] vectorNormal(float[] vector) {
        //��������ģ
        float module = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new float[]{vector[0] / module, vector[1] / module, vector[2] / module};
    }

    //��obj�ļ��м���Я��������Ϣ�����壬���Զ�����ÿ�������ƽ��������
    public static Solid loadFromFileVertexOnlyAverage(String fname, Resources r, MySurfaceView mv, float Scale) {
        //���غ����������
        Solid lo = null;
        //ԭʼ���������б�--��˳���obj�ļ��м��ص�
        Vector<Vector3f> vSet = new Vector<Vector3f>();
        //������������б� --���������������֯�õ�
        Vector<Integer> iSet = new Vector<Integer>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //ɨ���ļ������������͵Ĳ�ִͬ�в�ͬ�Ĵ����߼�
            while ((temps = br.readLine()) != null) {
                //�ÿո�ָ����еĸ�����ɲ���
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {//����Ϊ��������
                    //��Ϊ��������������ȡ���˶����XYZ������ӵ�ԭʼ���������б���
                    vSet.add(new Vector3f(
                            Float.parseFloat(tempsa[1]),
                            Float.parseFloat(tempsa[2]) - 5,
                            Float.parseFloat(tempsa[3])).multiK(Scale));
                } else if (tempsa[0].trim().equals("f")) {//����Ϊ��������
                      /*
		      		 *��Ϊ��������������� �����Ķ����������ԭʼ���������б���
		      		 *��ȡ��Ӧ�Ķ�������ֵ��ӵ�������������б��У�ͬʱ��������
		      		 *�����������������ķ���������ӵ�ƽ��ǰ����������Ӧ�ĵ�
		      		 *�ķ�����������ɵ�Map��
		      		*/

                    int[] index = new int[3];//������������ֵ������

                    //�����0�����������������ȡ�˶����XYZ��������
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    iSet.add(index[0]);


                    //�����1�����������������ȡ�˶����XYZ��������
                    index[1] = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    iSet.add(index[1]);


                    //�����2�����������������ȡ�˶����XYZ��������
                    index[2] = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    iSet.add(index[2]);

                }
            }

            //����3D�������
            lo = new Solid(mv, vSet, iSet, 1);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }


    //��obj�ļ��м��ؽ�Я��������Ϣ������
    //���ȼ��ض�����Ϣ���ٸ��ݶ�������������������Զ������ÿ����ķ�����
    //Ȼ�������ķ����������������ϵĶ���
    public static Solid loadFromFileVertexOnlyFace(String fname, Resources r, MySurfaceView mv, float Scale) {
        //���غ�3D���������
        Solid lo = null;
        //ԭʼ���������б�--��˳���obj�ļ��м��ص�
        Vector<Vector3f> vSet = new Vector<Vector3f>();
        //������������б� --���������������֯�õ�
        Vector<Integer> iSet = new Vector<Integer>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //ѭ�����ϴ��ļ��ж�ȡ�У����������͵Ĳ�ִͬ��
            //��ͬ�Ĵ����߼�
            while ((temps = br.readLine()) != null) {
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {
                    //����Ϊ��������
                    vSet.add(new Vector3f(
                            Float.parseFloat(tempsa[1]),
                            Float.parseFloat(tempsa[2]) - 5,
                            Float.parseFloat(tempsa[3])).multiK(Scale));
                } else if (tempsa[0].trim().equals("f")) {//����Ϊ��������
		      		/*
		      		 *��Ϊ��������������� �����Ķ����������ԭʼ���������б���
		      		 *��ȡ��Ӧ�Ķ�������ֵ��ӵ�������������б��У�ͬʱ��������
		      		 *�����������������������ӵ�����������б���
		      		*/

                    //��ȡ�����ε�һ�����������
                    int index = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    iSet.add(index);

                    //��ȡ�����εڶ������������
                    index = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    iSet.add(index);

                    //��ȡ�����ε��������������
                    index = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    iSet.add(index);
                }
            }


            //����3D����
            lo = new Solid(mv, vSet, iSet, 2);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }

    public static float[] getNolmalsOnlyAverage(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
        //��HashMap��keyΪ��������� valueΪ�����ڵĸ�����ķ������ļ���
        HashMap<Integer, HashSet<Normal>> hmn = new HashMap<Integer, HashSet<Normal>>();
        Iterator<Integer> iter = iSet.iterator();
        for (int i = 0; i < iSet.size(); i += 3) {
            int[] index = new int[]{(Integer) iter.next(), (Integer) iter.next(), (Integer) iter.next()};
            float x0 = vSet.get(index[0]).x;
            float y0 = vSet.get(index[0]).y;
            float z0 = vSet.get(index[0]).z;
            float x1 = vSet.get(index[1]).x;
            float y1 = vSet.get(index[1]).y;
            float z1 = vSet.get(index[1]).z;
            float x2 = vSet.get(index[2]).x;
            float y2 = vSet.get(index[2]).y;
            float z2 = vSet.get(index[2]).z;
            //ͨ��������������������0-1��0-2�����õ�����ķ�����
            //��0�ŵ㵽1�ŵ������
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //��0�ŵ㵽2�ŵ������
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //ͨ�������������Ĳ�����㷨����
            float[] vNormal = vectorNormal(getCrossProduct
                    (
                            vxa, vya, vza, vxb, vyb, vzb
                    ));

            for (int tempInxex : index) {//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
                //��ȡ��ǰ������Ӧ��ķ���������
                HashSet<Normal> hsn = hmn.get(tempInxex);
                if (hsn == null) {//�����ϲ������򴴽�
                    hsn = new HashSet<Normal>();
                }
                //���˵�ķ�������ӵ�������
                //����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
                //��Ӧ�ķ�����������
                hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
                //�����ϷŽ�HsahMap��
                hmn.put(tempInxex, hsn);
            }
        }
        //���ɷ���������
        float[] nXYZ = new float[iSet.size() * 3];
        int c = 0;
        for (Integer i : iSet) {
            //���ݵ�ǰ���������Map��ȡ��һ���������ļ���
            HashSet<Normal> hsn = hmn.get(i);
            //���ƽ��������
            float[] tn = Normal.getAverage(hsn);
            //���������ƽ����������ŵ�������������
            nXYZ[c++] = tn[0];
            nXYZ[c++] = tn[1];
            nXYZ[c++] = tn[2];
        }

        return nXYZ;
    }

    public static float[] getVerticesOnlyAverage(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //������������б�--������֯��
        ArrayList<Float> alvResult = new ArrayList<Float>();
        for (int i : iSet) {
            float x0 = vSet.get(i).x;
            float y0 = vSet.get(i).y;
            float z0 = vSet.get(i).z;
            alvResult.add(x0);
            alvResult.add(y0);
            alvResult.add(z0);
        }

        //���ɶ�������
        int size = alvResult.size();
        float[] vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = alvResult.get(i);
        }

        return vXYZ;
    }

    public static float[] getNolmalsOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //����������б�--���������������֯�õ�
        ArrayList<Float> alnResult = new ArrayList<Float>();
        //ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
        Iterator<Integer> iter = iSet.iterator();
        int num = 0;
        for (int n = 0; n < iSet.size(); n += 3) {
            num++;
            int[] index = new int[]{(Integer) iter.next(), (Integer) iter.next(), (Integer) iter.next()};
            float x0 = vSet.get(index[0]).x;
            float y0 = vSet.get(index[0]).y;
            float z0 = vSet.get(index[0]).z;
            float x1 = vSet.get(index[1]).x;
            float y1 = vSet.get(index[1]).y;
            float z1 = vSet.get(index[1]).z;
            float x2 = vSet.get(index[2]).x;
            float y2 = vSet.get(index[2]).y;
            float z2 = vSet.get(index[2]).z;
            //ͨ��������������������0-1��0-2�����õ�����ķ�����
            //��0�ŵ㵽1�ŵ������
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //��0�ŵ㵽2�ŵ������
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //ͨ�������������Ĳ�����㷨����
            float[] vNormal = vectorNormal
                    (
                            getCrossProduct
                                    (
                                            vxa, vya, vza, vxb, vyb, vzb
                                    )
                    );
            //��������ķ�������ӵ�����������б���
            for (int i = 0; i < 3; i++) {
                alnResult.add(vNormal[0]);
                alnResult.add(vNormal[1]);
                alnResult.add(vNormal[2]);
            }
        }

        //���ɷ���������
        int size = alnResult.size();
        float[] nXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            nXYZ[i] = alnResult.get(i);
        }

        return nXYZ;
    }

    public static float[] getVerticesOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //������������б�--������֯��
        ArrayList<Float> alvResult = new ArrayList<Float>();

        for (int i : iSet) {

            float x0 = (float) vSet.get(i).x;
            float y0 = (float) vSet.get(i).y;
            float z0 = (float) vSet.get(i).z;
            alvResult.add(x0);
            alvResult.add(y0);
            alvResult.add(z0);
        }

        //���ɶ�������
        int size = alvResult.size();
        float[] vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = alvResult.get(i);
        }


        return vXYZ;
    }

    public static float[] getStlNolmalsOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //����������б�--���������������֯�õ�
        ArrayList<Float> alnResult = new ArrayList<Float>();
        //ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
        Iterator<Integer> iter = iSet.iterator();
        int num = 0;
        for (int n = 0; n < iSet.size(); n += 3) {
            num++;
            int[] index = new int[]{(Integer) iter.next(), (Integer) iter.next(), (Integer) iter.next()};
            float x0 = vSet.get(index[0]).x;
            float y0 = vSet.get(index[0]).y;
            float z0 = vSet.get(index[0]).z;
            float x1 = vSet.get(index[1]).x;
            float y1 = vSet.get(index[1]).y;
            float z1 = vSet.get(index[1]).z;
            float x2 = vSet.get(index[2]).x;
            float y2 = vSet.get(index[2]).y;
            float z2 = vSet.get(index[2]).z;
            //ͨ��������������������0-1��0-2�����õ�����ķ�����
            //��0�ŵ㵽1�ŵ������
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //��0�ŵ㵽2�ŵ������
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //ͨ�������������Ĳ�����㷨����
            float[] vNormal = vectorNormal
                    (
                            getCrossProduct
                                    (
                                            vxa, vya, vza, vxb, vyb, vzb
                                    )
                    );
            //��������ķ�������ӵ�����������б���
            alnResult.add(vNormal[0]);
            alnResult.add(vNormal[1]);
            alnResult.add(vNormal[2]);
        }

        //���ɷ���������
        int size = alnResult.size();
        float[] nXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            nXYZ[i] = alnResult.get(i);
        }

        return nXYZ;
    }

}
