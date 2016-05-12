package com.bn.util;

import android.content.res.Resources;
import android.util.Log;

import com.bn.main.MySurfaceView;
import com.bn.object.Solid;
import com.bn.csgStruct.Normal;
import com.bn.csgStruct.Struct.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class LoadUtil {

    //求两个向量的叉积
    public static float[] getCrossProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
        //求出两个矢量叉积矢量在XYZ轴的分量ABC
        float A = y1 * z2 - y2 * z1;
        float B = z1 * x2 - z2 * x1;
        float C = x1 * y2 - x2 * y1;

        return new float[]{A, B, C};
    }

    //向量规格化
    public static float[] vectorNormal(float[] vector) {
        //求向量的模
        float module = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new float[]{vector[0] / module, vector[1] / module, vector[2] / module};
    }

    //从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
    public static Solid loadFromFileVertexOnlyAverage(String fname, Resources r, MySurfaceView mv, float Scale) {
        //加载后物体的引用
        Solid lo = null;
        //原始顶点坐标列表--按顺序从obj文件中加载的
        Vector<Vector3f> vSet = new Vector<Vector3f>();
        //结果顶点坐标列表 --根据组成面的情况组织好的
        Vector<Integer> iSet = new Vector<Integer>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //扫面文件，根据行类型的不同执行不同的处理逻辑
            while ((temps = br.readLine()) != null) {
                //用空格分割行中的各个组成部分
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {//此行为顶点坐标
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    vSet.add(new Vector3f(
                            Float.parseFloat(tempsa[1]),
                            Float.parseFloat(tempsa[2]) - 5,
                            Float.parseFloat(tempsa[3])).multiK(Scale));
                } else if (tempsa[0].trim().equals("f")) {//此行为三角形面
                      /*
		      		 *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
		      		 *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
		      		 *顶点的坐标计算出此面的法向量并添加到平均前各个索引对应的点
		      		 *的法向量集合组成的Map中
		      		*/

                    int[] index = new int[3];//三个顶点索引值的数组

                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    iSet.add(index[0]);


                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[1] = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    iSet.add(index[1]);


                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[2] = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    iSet.add(index[2]);

                }
            }

            //创建3D物体对象
            lo = new Solid(mv, vSet, iSet, 1);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }


    //从obj文件中加载仅携带顶点信息的物体
    //首先加载顶点信息，再根据顶点组成三角形面的情况自动计算出每个面的法向量
    //然后将这个面的法向量分配给这个面上的顶点
    public static Solid loadFromFileVertexOnlyFace(String fname, Resources r, MySurfaceView mv, float Scale) {
        //加载后3D对象的引用
        Solid lo = null;
        //原始顶点坐标列表--按顺序从obj文件中加载的
        Vector<Vector3f> vSet = new Vector<Vector3f>();
        //结果顶点坐标列表 --根据组成面的情况组织好的
        Vector<Integer> iSet = new Vector<Integer>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //循环不断从文件中读取行，根据行类型的不同执行
            //不同的处理逻辑
            while ((temps = br.readLine()) != null) {
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {
                    //此行为顶点坐标
                    vSet.add(new Vector3f(
                            Float.parseFloat(tempsa[1]),
                            Float.parseFloat(tempsa[2]) - 5,
                            Float.parseFloat(tempsa[3])).multiK(Scale));
                } else if (tempsa[0].trim().equals("f")) {//此行为三角形面
		      		/*
		      		 *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
		      		 *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
		      		 *顶点的坐标计算出法向量并添加到结果法向量列表中
		      		*/

                    //提取三角形第一个顶点的坐标
                    int index = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    iSet.add(index);

                    //提取三角形第二个顶点的坐标
                    index = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    iSet.add(index);

                    //提取三角形第三个顶点的坐标
                    index = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    iSet.add(index);
                }
            }


            //创建3D对象
            lo = new Solid(mv, vSet, iSet, 2);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }

    public static float[] getNolmalsOnlyAverage(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //平均前各个索引对应的点的法向量集合Map
        //此HashMap的key为点的索引， value为点所在的各个面的法向量的集合
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
            //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
            //求0号点到1号点的向量
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //求0号点到2号点的向量
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //通过求两个向量的叉积计算法向量
            float[] vNormal = vectorNormal(getCrossProduct
                    (
                            vxa, vya, vza, vxb, vyb, vzb
                    ));

            for (int tempInxex : index) {//记录每个索引点的法向量到平均前各个索引对应的点的法向量集合组成的Map中
                //获取当前索引对应点的法向量集合
                HashSet<Normal> hsn = hmn.get(tempInxex);
                if (hsn == null) {//若集合不存在则创建
                    hsn = new HashSet<Normal>();
                }
                //将此点的法向量添加到集合中
                //由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
                //对应的法向量集合中
                hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
                //将集合放进HsahMap中
                hmn.put(tempInxex, hsn);
            }
        }
        //生成法向量数组
        float[] nXYZ = new float[iSet.size() * 3];
        int c = 0;
        for (Integer i : iSet) {
            //根据当前点的索引从Map中取出一个法向量的集合
            HashSet<Normal> hsn = hmn.get(i);
            //求出平均法向量
            float[] tn = Normal.getAverage(hsn);
            //将计算出的平均法向量存放到法向量数组中
            nXYZ[c++] = tn[0];
            nXYZ[c++] = tn[1];
            nXYZ[c++] = tn[2];
        }

        return nXYZ;
    }

    public static float[] getVerticesOnlyAverage(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //结果顶点坐标列表--按面组织好
        ArrayList<Float> alvResult = new ArrayList<Float>();
        for (int i : iSet) {
            float x0 = vSet.get(i).x;
            float y0 = vSet.get(i).y;
            float z0 = vSet.get(i).z;
            alvResult.add(x0);
            alvResult.add(y0);
            alvResult.add(z0);
        }

        //生成顶点数组
        int size = alvResult.size();
        float[] vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = alvResult.get(i);
        }

        return vXYZ;
    }

    public static float[] getNolmalsOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //结果法向量列表--根据组成面的情况组织好的
        ArrayList<Float> alnResult = new ArrayList<Float>();
        //平均前各个索引对应的点的法向量集合Map
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
            //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
            //求0号点到1号点的向量
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //求0号点到2号点的向量
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //通过求两个向量的叉积计算法向量
            float[] vNormal = vectorNormal
                    (
                            getCrossProduct
                                    (
                                            vxa, vya, vza, vxb, vyb, vzb
                                    )
                    );
            //将计算出的法向量添加到结果法向量列表中
            for (int i = 0; i < 3; i++) {
                alnResult.add(vNormal[0]);
                alnResult.add(vNormal[1]);
                alnResult.add(vNormal[2]);
            }
        }

        //生成法向量数组
        int size = alnResult.size();
        float[] nXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            nXYZ[i] = alnResult.get(i);
        }

        return nXYZ;
    }

    public static float[] getVerticesOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //结果顶点坐标列表--按面组织好
        ArrayList<Float> alvResult = new ArrayList<Float>();

        for (int i : iSet) {

            float x0 = (float) vSet.get(i).x;
            float y0 = (float) vSet.get(i).y;
            float z0 = (float) vSet.get(i).z;
            alvResult.add(x0);
            alvResult.add(y0);
            alvResult.add(z0);
        }

        //生成顶点数组
        int size = alvResult.size();
        float[] vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = alvResult.get(i);
        }


        return vXYZ;
    }

    public static float[] getStlNolmalsOnlyFace(Vector<Vector3f> vSet, Vector<Integer> iSet) {
        //结果法向量列表--根据组成面的情况组织好的
        ArrayList<Float> alnResult = new ArrayList<Float>();
        //平均前各个索引对应的点的法向量集合Map
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
            //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
            //求0号点到1号点的向量
            float vxa = x1 - x0;
            float vya = y1 - y0;
            float vza = z1 - z0;
            //求0号点到2号点的向量
            float vxb = x2 - x0;
            float vyb = y2 - y0;
            float vzb = z2 - z0;
            //通过求两个向量的叉积计算法向量
            float[] vNormal = vectorNormal
                    (
                            getCrossProduct
                                    (
                                            vxa, vya, vza, vxb, vyb, vzb
                                    )
                    );
            //将计算出的法向量添加到结果法向量列表中
            alnResult.add(vNormal[0]);
            alnResult.add(vNormal[1]);
            alnResult.add(vNormal[2]);
        }

        //生成法向量数组
        int size = alnResult.size();
        float[] nXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            nXYZ[i] = alnResult.get(i);
        }

        return nXYZ;
    }

}
