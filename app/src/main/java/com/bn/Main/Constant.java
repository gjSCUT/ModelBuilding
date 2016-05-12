package com.bn.Main;

public class Constant {
    public static float DATA_RATIO=0.025f;//数据点的缩放比
    //单位尺寸
    public static final float UNIT_SIZE = 1.0f;
    //摄像机旋转速度移动阀值
    public static final float CAMERASPEED = 0.01F;
    //移动系数
    public static final float MOVESPEED = 0.01f;
    //旋转系数
    public static final float ANGLESPEED = 0.5f;
    //放缩系数
    public static final float SCALESPEED = 0.5f;
    //移动阀值
    public static final float MOVETHRESHOLD = 1.0F;
    //物体颜色
    public static final float[] COLOR = new float[]{0, 0.64f, 0.91f, 1};
    //物体数量
    public static final int OBJECT_NUMBER = 20;
    //布尔运算点的最小差值
    public static final double TOL = 1e-5f;
    //计算GLSurfaceView的宽高比
    public static float RATIO;
    //计算GLSurfaceView的宽
    public static int WIDTH;
    //计算GLSurfaceView的高
    public static int HEIGHT;
}
