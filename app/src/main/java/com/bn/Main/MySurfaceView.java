package com.bn.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bn.Util.Indesign;
import com.bn.Util.LoadUtil;
import com.bn.Util.VectorUtil;
import com.bn.csgStruct.Vector2f;
import com.bn.csgStruct.Vector3f;
import com.bn.csgStruct.BooleanModeller;
import com.bn.csgStruct.Bound;
import com.bn.object.Body;
import com.bn.object.Revolve;
import com.bn.object.Solid;
import com.bn.object.Sweep;
import com.bn.object.TextureRect;
import com.bn.object.TextureRectDouble;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MySurfaceView extends GLSurfaceView {
    public static boolean isFill = true; //是否使用填充还是线型
    /**
     * 手指触控数
     */
    public static int mode = 0;
    /**
     * 双指操作模式
     */
    public static int modeP2 = 0;
    /**
     * 三指操作模式
     */
    public static int modeP3 = 0;
    public static int isAxle = 0;   //觉得XYZ匹配轴后颜色的变化
    private static float[] stlPrint;//STL格式打印的数据
    public SceneRenderer mRenderer;//场景渲染器
    public Body curBody, curBody2;
    public boolean isShowBeginFace = false;//判断单手指操作是否被锁定
    public boolean isShowEndFace = false;//判断双手指操作是否被锁定
    public boolean isBool = false; //是否使用填充还是线型
    public boolean isObject = false; //放大视角还是物体
    public boolean isCreateNew = false;//是否创建一个新的
    public boolean isCreateNormal = false;//是否新建
    public boolean isCreateBool = false;//是否新建
    /**
     * 单指操作模式
     * 含义：
     * 1.控制相机
     * 2.拾取等待
     * 3.面贴合
     */
    public int modeP1 = 0;
    public int isMove = 1;  //判断平移的一个变量
    /**
     * 当前选中物体
     */
    //////////////
    //面贴和有关的变量
    public int createType = 0;
    public int boolMode;
    /**
     * 被贴合物体
     */
    Body fitTargetBody;
    //////////////////////////
    Vector<Body> BodyAll;
    Vector<Body> BodyPick;
    Solid cube;
    Solid sphere;
    Solid cone;
    Solid cylinder;
    Solid pm;
    TextureRect texRect;    //纹理矩形对象的引用
    TextureRectDouble beginFace;    //要贴合面
    TextureRectDouble endFace;    //被贴合面
    Indesign indesign;//redo和undo功能
    private Camera camera;
    /**
     * 记录每次touch时间触发开始时的坐标
     */
    private float locationYBeginP1, locationXBeginP1;
    /**
     * 单指时touch时间结束位置
     */
    private float locationXEndP1, locationYEndP1;
    /**
     * 记录每次touch时间触发down时的坐标
     */
    private float locationYDownP1, locationXDownP1;
    /**
     * 记录双指touch时间触发开始时的坐标
     */
    private float locationY0BeginP2, locationX0BeginP2, locationY1BeginP2, locationX1BeginP2;
    /**
     * 记录双指时touch时间触发结束位置
     */
    private float locationX0EndP2, locationY0EndP2, locationX1EndP2, locationY1EndP2;
    /**
     * //记录双指Down触发时的坐标
     */
    private float locationX0DownP2, locationY0DownP2, locationX1DownP2, locationY1DownP2;
    /**
     * //记录三指touch时间触发开始时的坐标
     */
    private float locationY0BeginP3, locationX0BeginP3, locationY1BeginP3, locationX1BeginP3, locationY2BeginP3, locationX2BeginP3;
    /**
     * //记录三指时touch时间结束位置
     */
    private float locationX0EndP3, locationY0EndP3, locationX1EndP3, locationY1EndP3, locationX2EndP3, locationY2EndP3;
    /**
     * //记录三指时touch手指松开位置
     */
    private float locationX0DownP3, locationY0DownP3, locationX1DownP3, locationY1DownP3, locationX2DownP3, locationY2DownP3;
    /**
     * //三手down时两指向量
     */
    private Vector2f Vector2fP01DownP3, Vector2fP02DownP3, Vector2fP12DownP3;
    /**
     * //双指Down时两指间距
     */
    private float lengthPDownP2 = 1f;
    /**
     * //双指down时两指向量
     */
    private Vector2f Vector2fPDownP2;
    /**
     * 记录三指Down触发时的坐标
     */
    private Vector2f copyDirection;
    /**
     * 0手指出发法向量，1手指停止法向量，2被贴合物体接触点坐标
     */
    private Vector2f[] fitFingerDirection = new Vector2f[4];
    /**
     * 变量含义：
     * 0.未开始拾取
     * 1.拾取贴合物体的离开点中
     * 2.拾取被贴合物体的进入点中
     * 3.拾取被贴合物体的停止点中
     * 4.拾取完毕，等待抬起手指
     */
    private int fitMode = 0;
    private boolean isPush = false;//判断是否进行入栈
    private boolean hasLoad = false;//是否初始化完成
    private boolean isP1Lock = false;//判断单手指操作是否被锁定
    private boolean isP2Lock = false;//判断双手指操作是否被锁定
    private boolean isCopy = true;//是否初次MOVE
    //当前纹理的id
    private int axisTexId, redTexId;
    //当前纹理的图片
    private Bitmap axisBm, redBm;
    List<Vector2f> face = new Vector<>();
    List<Vector2f> line = new Vector<>();


    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
        //创建摄像机
        camera = new Camera();
        //初始化光源
        MatrixState.setLightLocation(10, 30, 10);

        //初始化工作栈和恢复栈
        indesign = new Indesign();
    }

    public static float[] getStlPrint() {
        return stlPrint;
    }

    /**
     * 3d拾取
     *
     * @param locationX
     * @param locationY
     * @return
     */
    public static Body pickupObject(float locationX, float locationY, Vector<Body> objectList) {
        float[] AB = MatrixState.getUnProject(
                locationX,
                locationY);
        //射线AB
        Vector3f start = new Vector3f(AB[0], AB[1], AB[2]);//起点
        Vector3f end = new Vector3f(AB[3], AB[4], AB[5]);//终点
        Vector3f dir = end.minus(start);//长度和方向

        Body temp = null;
        ;//标记为没有选中任何物体
        float minTime = 1;//记录列表中所有物体与AB相交的最短时间
        float[] Face = new float[4];
        for (Body b : objectList)//遍历列表中的物体
        {
            Bound box = b.getCurrBox(); //获得物体AABB包围盒
            float time = box.rayIntersect(start, dir, null, Face);//计算相交时间
            if (time <= minTime) {
                minTime = time;//记录最小值
                temp = b;
            }
        }
        if (minTime != 1) {
            return temp;
        }
        return null;
    }


    public void setLine(List<Vector2f> line) {
        this.line = line;
    }

    public void setFace(List<Vector2f> face) {
        this.face = face;
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        locationXBeginP1 = e.getX();    //记录每次touch时间触发开始时的Y坐标
        locationYBeginP1 = e.getY();    //记录每次touch时间触发开始时的X坐标

        if (mode == 2) {
            locationX0BeginP2 = e.getX(0);    //记录每次touch时间触发开始时的Y0坐标
            locationY0BeginP2 = e.getY(0);    //记录每次touch时间触发开始时的X0坐标
            locationX1BeginP2 = e.getX(1);    //记录每次touch时间触发开始时的Y1坐标
            locationY1BeginP2 = e.getY(1);    //记录每次touch时间触发开始时的X1坐标
        } else if (mode == 3) {
            locationX0BeginP3 = e.getX(0);    //记录每次touch时间触发开始时的Y0坐标
            locationY0BeginP3 = e.getY(0);    //记录每次touch时间触发开始时的X0坐标
            locationX1BeginP3 = e.getX(1);    //记录每次touch时间触发开始时的Y1坐标
            locationY1BeginP3 = e.getY(1);    //记录每次touch时间触发开始时的X1坐标
            locationX2BeginP3 = e.getX(2);    //记录每次touch时间触发开始时的Y2坐标
            locationY2BeginP3 = e.getY(2);    //记录每次touch时间触发开始时的X2坐标
        }
        switch (e.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mode = 1;//设置为单点模式
                locationXDownP1 = e.getX(0);    //记录单指Down触发时的X坐标
                locationYDownP1 = e.getY(0);    //记录单指Down触发时的Y坐标

                //选择点为空，移动相机
                if (pickupObject(locationXDownP1, locationYDownP1, BodyAll) == null) {
                    modeP1 = 1;
                }
                //选择物体为当前物体，面贴合
                else if (pickupObject(locationXDownP1, locationYDownP1, BodyAll) == curBody) {
                    modeP1 = 2;
                    fitMode = 1;
                }
                //拾取等待
                else {
                    modeP1 = 3;
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }

                            if (modeP1 == 3 && !isP1Lock) {
                                if (!isBool) {
                                    Body temp = pickupObject(locationXDownP1, locationYDownP1, BodyAll);
                                    if (temp != null) {
                                        curBody = temp;
                                        for (Body b : BodyAll) {
                                            if (b.isChoosed)
                                                b.isChoosed = false;
                                        }
                                        curBody.isChoosed = true;
                                        isPush = true;
                                    }
                                } else {
                                    Body temp = pickupObject(locationXDownP1, locationYDownP1, BodyAll);
                                    if (temp != null && temp != curBody) {
                                        curBody2 = temp;
                                        curBody2.isChoosed = true;
                                        isBool = false;
                                        isCreateBool = true;
                                    }
                                }
                            }
                            modeP1 = 1;
                        }
                    }).start();
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;//设置为双点模式
                if (mode == 2 && BodyAll.size() != 0) {
                    isP1Lock = true;

                    locationX0DownP2 = e.getX(0);    //记录双指Down触发时的X1坐标
                    locationY0DownP2 = Constant.HEIGHT - e.getY(0);    //记录双指Down触发时的Y1坐标
                    locationX1DownP2 = e.getX(1);    //记录双指Down触发时的X1坐标
                    locationY1DownP2 = Constant.HEIGHT - e.getY(1);    //记录双指Down触发时的Y1坐标

                    Vector2fPDownP2 = new Vector2f(locationX1DownP2 - locationX0DownP2, locationY1DownP2 - locationY0DownP2);//手指初始向量
                    lengthPDownP2 = Vector2fPDownP2.module();//记录双指down事两指间距

                    //判断轴
                    curBody.switchAxis(Vector2fPDownP2);

                } else if (mode == 3) {
                    isP2Lock = true;

                    isAxle = 0;//设置坐标轴不变色

                    locationX0DownP3 = e.getX(0);    //记录三指Down触发时的X1坐标
                    locationY0DownP3 = e.getY(0);    //记录三指Down触发时的Y1坐标
                    locationX1DownP3 = e.getX(1);    //记录三指Down触发时的X2坐标
                    locationY1DownP3 = e.getY(1);    //记录三指Down触发时的Y2坐标
                    locationX2DownP3 = e.getX(2);    //记录三指Down触发时的X3坐标
                    locationY2DownP3 = e.getY(2);    //记录三指Down触发时的Y3坐标

                    Vector2fP01DownP3 = new Vector2f(locationX0DownP3 - locationX1DownP3, locationY0DownP3 - locationY1DownP3);//手指12初始向量
                    Vector2fP02DownP3 = new Vector2f(locationX2DownP3 - locationX0DownP3, locationY2DownP3 - locationY0DownP3);//手指13初始向量
                    Vector2fP12DownP3 = new Vector2f(locationX2DownP3 - locationX1DownP3, locationY2DownP3 - locationY1DownP3);//手指23初始向量
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == 1 && !isP1Lock) {
                    float locationXMoveP1 = e.getX(0);    //记录Move触发时的X坐标
                    float locationYMoveP1 = e.getY(0);    //记录Move触发时的Y坐标

                    //摄像机移动与拾取
                    if (modeP1 == 1) {
                        float distanceXMoveP1 = locationXBeginP1 - locationXEndP1; //记录单指Move触发时X轴移动的距离
                        float distanceYMoveP1 = locationYBeginP1 - locationYEndP1;    //记录单指Move触发时Y轴移动的距离
                        //设置摄像机绕原点旋转
                        camera.angelA -= distanceXMoveP1 * Constant.CAMERASPEED;
                        camera.angelB += distanceYMoveP1 * Constant.CAMERASPEED;
                        camera.angelB = Math.max(camera.angelB, 0);
                        camera.angelB = Math.min(camera.angelB, (float) (Math.PI / 2));


                    }
                    //面贴合
                    else if (modeP1 == 2 && BodyAll.size() != 0) {
                        if (fitMode == 1) {//拾取贴合物体离开点中
                            if (pickupObject(locationXMoveP1, locationYMoveP1, BodyAll) != curBody) {
                                fitMode = 2;
                                fitFingerDirection[0] = new Vector2f(locationXMoveP1 - locationXDownP1, -(locationYMoveP1 - locationYDownP1));
                                //贴合面
                                beginFace.match(curBody, curBody.getFitTargetFace(fitFingerDirection[0], 1));
                                isShowBeginFace = true;
                            }
                        } else if (fitMode == 2) {//拾取被贴合物体进入点中
                            fitTargetBody = pickupObject(locationXMoveP1, locationYMoveP1, BodyAll);
                            if (fitTargetBody != null) {
                                fitMode = 3;
                                fitFingerDirection[2] = new Vector2f(locationXMoveP1, locationYMoveP1);
                            }
                        } else if (fitMode == 3) {//拾取被贴合物体停止点中，离开被贴合物体回到状态2
                            if (pickupObject(locationXMoveP1, locationYMoveP1, BodyAll) != fitTargetBody) {
                                fitMode = 2;
                                isShowEndFace = false;
                            } else {
                                fitFingerDirection[1] = new Vector2f(locationXMoveP1 - fitFingerDirection[2].x, -(locationYMoveP1 - fitFingerDirection[2].y));
                                //被贴合面
                                endFace.match(fitTargetBody, fitTargetBody.getFitTargetFace(fitFingerDirection[1], -1));
                                isShowEndFace = true;
                            }
                        }
                    } else if (modeP1 == 3) { //拾取等待
                        //移动距离超过阀值，停止拾取
                        if (Math.sqrt((locationXMoveP1 - locationXDownP1) * (locationXMoveP1 - locationXDownP1) +
                                (locationYMoveP1 - locationYDownP1) * (locationYMoveP1 - locationYDownP1)) < 1000) {
                            modeP1 = 1;
                        }
                    }
                } else if (mode == 2 && !isP2Lock && BodyAll.size() != 0) {
                    Vector2f Vector2fP1MoveP2 = new Vector2f(e.getX(0) - locationX0DownP2, Constant.HEIGHT - e.getY(0) - locationY0DownP2);//第一个手指向量
                    Vector2f Vector2fP2MoveP2 = new Vector2f(e.getX(1) - locationX1DownP2, Constant.HEIGHT - e.getY(1) - locationY1DownP2);//第二个手指向量
                    isPush = true;

                    //双指平移
                    if (VectorUtil.Product(Vector2fP1MoveP2, Vector2fP2MoveP2) > 0 &&
                            !VectorUtil.isVertical(Vector2fP1MoveP2, Vector2fPDownP2) &&
                            !VectorUtil.isVertical(Vector2fP2MoveP2, Vector2fPDownP2)) {
                        float lengthMove = Constant.MOVESPEED * (
                                VectorUtil.Length(locationX0BeginP2, locationY0BeginP2, locationX0EndP2, locationY0EndP2) +
                                        VectorUtil.Length(locationX1BeginP2, locationY1BeginP2, locationX1EndP2, locationY1EndP2)) / 2;
                        if (lengthMove > 1.0f) lengthMove = 1.0f;
                        if (modeP2 == 1) {
                            if (VectorUtil.getDegree(Vector2fP1MoveP2, curBody.vx) > 0 &&
                                    VectorUtil.getDegree(Vector2fP2MoveP2, curBody.vx) > 0)
                                isMove = 1;
                            else isMove = -1;

                            float[] direction = new float[]{isMove, 0, 0, 1};
                            if ((curBody.xLength < 10.0f) || (curBody.xLength > -10.0f)) {
                                curBody.Translate(direction, lengthMove);
                            }
                        } else if (modeP2 == 2) {
                            if (VectorUtil.getDegree(Vector2fP1MoveP2, curBody.vy) > 0 &&
                                    VectorUtil.getDegree(Vector2fP2MoveP2, curBody.vy) > 0)
                                isMove = 1;
                            else isMove = -1;

                            float[] direction = new float[]{0, isMove, 0, 1};
                            if ((curBody.yLength > -8.0f) || (curBody.yLength < 3.0f)) {
                                curBody.Translate(direction, lengthMove);
                            }
                        } else if (modeP2 == 3) {
                            if (VectorUtil.getDegree(Vector2fP1MoveP2, curBody.vz) > 0 &&
                                    VectorUtil.getDegree(Vector2fP2MoveP2, curBody.vz) > 0)
                                isMove = 1;
                            else isMove = -1;

                            float[] direction = new float[]{0, 0, isMove, 1};
                            if ((curBody.zLength < 10.0f) || (curBody.zLength > -10.0f)) {
                                curBody.Translate(direction, lengthMove);
                            }
                        }

                    }
                    //双指旋转
                    else if (VectorUtil.Product(Vector2fP1MoveP2, Vector2fP2MoveP2) > 0 &&
                            VectorUtil.isVertical(Vector2fP1MoveP2, Vector2fPDownP2) &&
                            VectorUtil.isVertical(Vector2fP2MoveP2, Vector2fPDownP2)) {
                        float angle = Constant.ANGLESPEED * (VectorUtil.Length(locationX0BeginP2, locationY0BeginP2, locationX0EndP2, locationY0EndP2) +
                                VectorUtil.Length(locationX1BeginP2, locationY1BeginP2, locationX1EndP2, locationY1EndP2)) / 2;
                        if (modeP2 == 1) {
                            if ((curBody.vx.x * Vector2fP1MoveP2.y - curBody.vx.y * Vector2fP1MoveP2.x) > 0 &&
                                    (curBody.vx.x * Vector2fP2MoveP2.y - curBody.vx.y * Vector2fP2MoveP2.x) > 0)
                                isMove = 1;
                            else
                                isMove = -1;


                            curBody.Rotate(1, 0, 0, isMove * angle);
                        } else if (modeP2 == 2) {
                            if ((curBody.vy.x * Vector2fP1MoveP2.y - curBody.vy.y * Vector2fP1MoveP2.x) > 0 &&
                                    (curBody.vy.x * Vector2fP2MoveP2.y - curBody.vy.y * Vector2fP2MoveP2.x) > 0)
                                isMove = 1;
                            else
                                isMove = -1;

                            curBody.Rotate(0, -1, 0, isMove * angle);

                        } else if (modeP2 == 3) {
                            if ((curBody.vz.x * Vector2fP1MoveP2.y - curBody.vz.y * Vector2fP1MoveP2.x) > 0 &&
                                    (curBody.vz.x * Vector2fP2MoveP2.y - curBody.vz.y * Vector2fP2MoveP2.x) > 0)
                                isMove = 1;
                            else
                                isMove = -1;

                            curBody.Rotate(0, 0, 1, isMove * angle);

                        }
                    }
                    //双指放缩
                    else if (VectorUtil.Product(Vector2fP1MoveP2, Vector2fP2MoveP2) < 0 &&
                            !VectorUtil.isVertical(Vector2fP1MoveP2, Vector2fPDownP2) &&
                            !VectorUtil.isVertical(Vector2fP2MoveP2, Vector2fPDownP2)) {
                        float lengthPMoveP2 = VectorUtil.Length(e.getX(0), e.getY(0), e.getX(1), e.getY(1));
                        if (modeP2 == 1) {
                            if (lengthPMoveP2 > 10f) {
                                float scale = lengthPMoveP2 / lengthPDownP2;
                                curBody.Scale(scale, 1, 1);
                            }
                            lengthPDownP2 = lengthPMoveP2;
                        } else if (modeP2 == 2) {
                            if (lengthPMoveP2 > 10f) {
                                float scale = lengthPMoveP2 / lengthPDownP2;
                                curBody.Scale(1, scale, 1);
                            }
                            lengthPDownP2 = lengthPMoveP2;
                        } else if (modeP2 == 3) {
                            if (lengthPMoveP2 > 10f) {
                                float scale = lengthPMoveP2 / lengthPDownP2;
                                curBody.Scale(1, 1, scale);
                            }
                            lengthPDownP2 = lengthPMoveP2;
                        }

                    }

                } else if (mode == 3) {
                    Vector2f Vector2fP0 = new Vector2f(e.getX(0) - locationX0DownP3, e.getY(0) - locationY0DownP3);//第一个手指向量
                    Vector2f Vector2fP1 = new Vector2f(e.getX(1) - locationX1DownP3, e.getY(1) - locationY1DownP3);//第二个手指向量
                    Vector2f Vector2fP2 = new Vector2f(e.getX(2) - locationX2DownP3, e.getY(2) - locationY2DownP3);//第二个手指向量
                    //复制一个3D图元
                    if (VectorUtil.Product(Vector2fP0, Vector2fP1) > 0 &&
                            VectorUtil.Product(Vector2fP0, Vector2fP2) > 0 &&
                            VectorUtil.Product(Vector2fP1, Vector2fP2) > 0) {
                        isPush = true;
                        //复制一个3D图元
                        if (isCopy) {

                            isCopy = false;
                            //复制一个物体
                            isCreateNormal = true;
                            createType = 0;
                            isCreateNew = false;

                        }
                        //移动复制的3D图元
                        else {
                            copyDirection = new Vector2f(e.getX(0) - locationX0DownP3, locationY0DownP3 - e.getY(0));

                            curBody.switchAxis(copyDirection);


                            float lengthMove = Constant.MOVESPEED * Constant.MOVESPEED * (
                                    VectorUtil.Length(locationX0BeginP3, locationY0BeginP3, locationX0EndP3, locationY0EndP3) +
                                            VectorUtil.Length(locationX1BeginP3, locationY1BeginP3, locationX1EndP3, locationY1EndP3) +
                                            VectorUtil.Length(locationX2BeginP3, locationY2BeginP3, locationX2EndP3, locationY2EndP3)) / 3;
                            if (modeP3 == 1) {
                                if (VectorUtil.getDegree(copyDirection, curBody.vx) > 0)
                                    isMove = 1;
                                else isMove = -1;

                                float[] direction = new float[]{isMove, 0, 0, 1};
                                if ((isMove == 1 && curBody.xLength < 10.0f) || (isMove == -1 && curBody.xLength > -10.0f))
                                    curBody.Translate(direction, lengthMove);
                            } else if (modeP3 == 2) {
                                if (VectorUtil.getDegree(copyDirection, curBody.vy) > 0)
                                    isMove = 1;
                                else isMove = -1;

                                float[] direction = new float[]{0, isMove, 0, 1};
                                if ((isMove == 1 && curBody.yLength > -8.0f) || (isMove == -1 && curBody.yLength < 3.0f))
                                    curBody.Translate(direction, lengthMove);
                            } else if (modeP3 == 3) {
                                if (VectorUtil.getDegree(copyDirection, curBody.vz) > 0)
                                    isMove = 1;
                                else isMove = -1;

                                float[] direction = new float[]{0, 0, isMove, 1};
                                if ((isMove == 1 && curBody.zLength < 10.0f) || (isMove == -1 && curBody.zLength > -10.0f))
                                    curBody.Translate(direction, lengthMove);
                            }


                        }
                    }
                    //拉近拉远摄像头
                    else {
                        float lengthP01MoveP3 = VectorUtil.Length(e.getX(0), e.getY(0), e.getX(1), e.getY(1));
                        float lengthP02MoveP3 = VectorUtil.Length(e.getX(0), e.getY(0), e.getX(2), e.getY(2));
                        float lengthP12MoveP3 = VectorUtil.Length(e.getX(1), e.getY(1), e.getX(2), e.getY(2));
                        if (lengthP01MoveP3 > 10f && lengthP02MoveP3 > 10f && lengthP12MoveP3 > 10f) {

                            float scale01 = lengthP01MoveP3 / (Vector2fP01DownP3.module());
                            float scale02 = lengthP02MoveP3 / (Vector2fP02DownP3.module());
                            float scale12 = lengthP12MoveP3 / (Vector2fP12DownP3.module());
                            if (!isObject) {
                                camera.distance /= (scale01 + scale02 + scale12) / 3;
                                camera.distancetotal = (float) Math.sqrt(1 + camera.distance * camera.distance);
                                camera.angelC = (float) Math.atan(1 / camera.distance);
                            } else {
                                curBody.xScale *= (scale01 + scale02 + scale12) / 3;
                                curBody.yScale *= (scale01 + scale02 + scale12) / 3;
                                curBody.zScale *= (scale01 + scale02 + scale12) / 3;
                                isPush = true;
                            }
                            Vector2fP01DownP3 = new Vector2f(e.getX(0) - e.getX(1), e.getY(0) - e.getY(1));
                            Vector2fP02DownP3 = new Vector2f(e.getX(0) - e.getX(2), e.getY(0) - e.getY(2));
                            Vector2fP12DownP3 = new Vector2f(e.getX(1) - e.getX(2), e.getY(1) - e.getY(2));
                        }
                    }
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;//设置为单点模式
                if (mode == 1) {
                    isAxle = 0;//设置坐标轴不变色
                } else if (mode == 2) {
                    isCopy = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;

                //面贴和
                if (modeP1 == 2 && fitMode == 3 && !isP1Lock && BodyAll.size() != 0) {//当系统从贴合状态的贴合完成状态抬起手指时，进行贴合操作
                    curBody.faceMatch(
                            curBody.getFitTargetFace(fitFingerDirection[0], 1),
                            fitTargetBody.getFitTargetFace(fitFingerDirection[1], -1),
                            fitTargetBody);
                    isPush = true;
                }
                modeP1 = 1;
                fitMode = 0;

                isShowBeginFace = false;
                isShowEndFace = false;
                isP1Lock = false;
                isP2Lock = false;

                if (isPush) {
                    Vector<Body> temp = new Vector<Body>();
                    for (Body e1 : BodyAll) {
                        Body tempBody = (Body) e1.clone();
                        temp.add(tempBody);
                    }
                    indesign.addRedoStack(temp);
                    isPush = false;
                }
                break;
        }

        locationXEndP1 = locationXBeginP1;    //记录每次touch时间触发结束时的X坐标
        locationYEndP1 = locationYBeginP1;    //记录每次touch时间触发结束时的Y坐标
        locationX0EndP2 = locationX0BeginP2;    //记录每次touch时间触发结束时的X坐标
        locationY0EndP2 = locationY0BeginP2;    //记录每次touch时间触发结束时的Y坐标
        locationX1EndP2 = locationX1BeginP2;    //记录每次touch时间触发结束时的X坐标
        locationY1EndP2 = locationY1BeginP2;    //记录每次touch时间触发结束时的Y坐标

        return true;
    }

    public int getCur(Body b) {
        for (int i = 0; i < BodyAll.size(); i++) {
            Body temp = BodyAll.get(i);
            if (temp == b)
                return i;
        }
        return -1;
    }

    //新建物体
    public void createObject(int Type, boolean isNew) {
        boolean isTemp = false;
        if (isNew == true) {
            Solid solid = null;
            if (Type == 1) {
                solid = new Solid(cube);
            } else if (Type == 2) {
                solid = new Solid(cylinder);
            } else if (Type == 3) {
                solid = new Solid(cone);
            } else if (Type == 4) {
                solid = new Solid(sphere);
            } else if (Type == 5) {
                solid = new Sweep(0.1f, face, line);
            } else if (Type == 6) {
                solid = new Revolve(0.1f, face, 18);
            }
            BodyAll.add(solid);
            isTemp = true;
        } else {
            Solid s = new Solid(curBody);
            BodyAll.add(s);
        }

        curBody = BodyAll.get(BodyAll.size() - 1);
        for (Body b : BodyAll) {
            if (b.isChoosed)
                b.isChoosed = false;
        }
        curBody.isChoosed = true;

        if (isTemp) {
            Vector<Body> temp = new Vector<>();
            for (Body e1 : BodyAll) {
                Body tempBody = (Body) e1.clone();
                temp.add(tempBody);
            }
            indesign.addRedoStack(temp);
        }

    }

    //新建布尔物体
    public void createBool(Body b1, Body b2) {
        BooleanModeller bm = new BooleanModeller((Solid) b2, (Solid) b1);
        if (bm.BooleanOp()) {
            Solid boolSolid = null;
            switch (boolMode) {
                case 1:
                    boolSolid = bm.getIntersection();
                    break;
                case 2:
                    boolSolid = bm.getUnion();
                    break;
                case 3:
                    boolSolid = bm.getDifference();
                    break;
            }

            BodyAll.remove(b1);
            BodyAll.remove(b2);
            BodyAll.add(boolSolid);

            isCreateBool = false;

            curBody = boolSolid;

            for (Body b : BodyAll) {
                if (b.isChoosed)
                    b.isChoosed = false;
            }
            curBody.isChoosed = true;

            Vector<Body> temp = new Vector<>();
            for (Body e1 : BodyAll) {
                Body tempBody = (Body) e1.clone();
                temp.add(tempBody);
            }
            indesign.addRedoStack(temp);
        } else {
            //Toast.makeText(mContext, "布尔运算的两物体必须相交", Toast.LENGTH_LONG).show();
            curBody2.isChoosed = false;
        }
    }

    //通过IO加载图片
    public Bitmap loadTexture(int drawableId) {
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapTmp;
    }

    public int initTexture(Bitmap bitmapTmp, boolean needRrelease) {
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0           //偏移量
                );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(
                GLES20.GL_TEXTURE_2D, //纹理类型
                0,
                GLUtils.getInternalFormat(bitmapTmp),
                bitmapTmp, //纹理图像
                GLUtils.getType(bitmapTmp),
                0 //纹理边框尺寸
        );

        if (needRrelease) {
            bitmapTmp.recycle(); //纹理加载成功后释放图片
        }
        return textureId;
    }

    public void undo() {
        if (indesign.undoCheck()) {
            Vector<Body> temp = new Vector<Body>();
            for (Body e : indesign.Undo()) {
                Body e1 = (Body) e.clone();
                temp.add(e1);
            }
            BodyAll = temp;
            for (Body b : BodyAll)
                if (b.isChoosed) {
                    curBody = b;
                    break;
                }
        }

    }

    public void redo() {
        if (indesign.redoCheck()) {
            Vector<Body> temp = new Vector<Body>();
            for (Body e : indesign.Redo()) {
                Body e1 = (Body) e.clone();
                temp.add(e1);
            }
            BodyAll = temp;
            for (Body b : BodyAll)
                if (b.isChoosed) {
                    curBody = b;
                    break;
                }
        }

    }

    public class SceneRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-Constant.RATIO, Constant.RATIO, -1, 1, 10, 400);
            // 调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0, 0, 70, 0, 0, 0, 0, 1, 0);

            MatrixState.pushMatrix();    //进栈
            //画按钮
            //......
            MatrixState.popMatrix();//出栈

            //设置摄像头
            camera.setCarema();

            if (isCreateNormal == true) {
                createObject(createType, isCreateNew);
                isCreateNormal = false;
            }

            if (isCreateBool == true) {
                createBool(curBody, curBody2);
                isCreateBool = false;
            }

            if (!hasLoad) {
                initTaskReal();
                hasLoad = true;
            } else {
                //绘制坐标平面矩形
                MatrixState.pushMatrix();    //进栈
                texRect.drawSelf(axisTexId);    //绘制大四边形
                MatrixState.popMatrix();//出栈

                //绘制贴合面
                if (isShowBeginFace) {
                    MatrixState.pushMatrix();    //进栈
                    beginFace.drawSelf(redTexId);
                    MatrixState.popMatrix();//出栈
                }
                if (isShowEndFace) {
                    MatrixState.pushMatrix();    //进栈
                    endFace.drawSelf(redTexId);
                    MatrixState.popMatrix();//出栈
                }

                //绘制
                for (Body b : BodyAll) {
                    MatrixState.pushMatrix();    //进栈
                    b.drawSelf(0);
                    //s.drawSelf(1);
                    MatrixState.popMatrix();//出栈
                }
                /*
                if(BodyAll.size()==1)
	            {
	            	Solid s=(Solid)curBody;
	            	stlPrint=s.getStlPoint();
	            }else
	            {
	            	stlPrint=null;
	            }
	            */
            }
        }

        public void initTaskReal() {
            //物体的数组
            BodyAll = new Vector<>();
            BodyPick = new Vector<>();

            //创建纹理矩形对象
            texRect = new TextureRect(8.0f);
            texRect.initShader(ShaderManager.getObjectshaderProgram());
            texRect.Translate(new float[]{0, -1, 0, 1}, 2.0f);    //平移
            texRect.Rotate(1, 0, 0, 90);

            beginFace = new TextureRectDouble(1.0f);
            beginFace.initShader(ShaderManager.getObjectshaderProgram());
            endFace = new TextureRectDouble(1.0f);
            endFace.initShader(ShaderManager.getObjectshaderProgram());

            //加载要绘制的物体
            pm = LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),0.2f);
            cone = LoadUtil.loadFromFileVertexOnlyAverage("cone.obj", MySurfaceView.this.getResources(),  0.2f);
            cube = LoadUtil.loadFromFileVertexOnlyFace("cube.obj", MySurfaceView.this.getResources(), 0.2f);
            cylinder = LoadUtil.loadFromFileVertexOnlyAverage("cylinder.obj", MySurfaceView.this.getResources(), 0.2f);
            sphere = LoadUtil.loadFromFileVertexOnlyAverage("sphere.obj", MySurfaceView.this.getResources(), 0.2f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            Constant.RATIO = (float) width / height;
            Constant.WIDTH = width;
            Constant.HEIGHT = height;

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);

            //初始化shader
            ShaderManager.compileShader();
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化像素的纹理
            axisBm = loadTexture(R.drawable.axis);    //设置默认纹理
            redBm = loadTexture(R.drawable.red);//设置默认纹理
            //初始化像素的纹理
            axisTexId = initTexture(axisBm, true);    //设置默认纹理
            redTexId = initTexture(redBm, true);//设置默认纹理
        }
    }

}
