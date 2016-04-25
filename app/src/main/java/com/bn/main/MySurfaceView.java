package com.bn.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bn.csgStruct.BooleanModeller;
import com.bn.csgStruct.Bound;
import com.bn.csgStruct.Struct.Vector2f;
import com.bn.csgStruct.Struct.Vector3f;
import com.bn.object.Body;
import com.bn.object.Solid;
import com.bn.object.TextureRect;
import com.bn.object.TextureRectDouble;
import com.bn.util.Indesign;
import com.bn.util.LoadUtil;
import com.bn.util.VectorUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MySurfaceView extends GLSurfaceView {
    public static boolean isFill = true; //�Ƿ�ʹ����仹������?
    /**
     * ��ָ������
     */
    public static int mode = 0;
    /**
     * ˫ָ����ģʽ
     */
    public static int modeP2 = 0;
    /**
     * ��ָ����ģʽ
     */
    public static int modeP3 = 0;
    public static int isAxle = 0;   //����XYZƥ�������ɫ�ı�?
    private static float[] stlPrint;//STL��ʽ��ӡ������
    public SceneRenderer mRenderer;//������Ⱦ��
    public Body curBody, curBody2;
    public boolean isShowBeginFace = false;//�жϵ���ָ�����Ƿ�����
    public boolean isShowEndFace = false;//�ж�˫��ָ�����Ƿ�����
    public boolean isBool = false; //�Ƿ�ʹ����仹������?
    public boolean isObject = false; //�Ŵ��ӽǻ�������
    public boolean isCreateNew = false;//�Ƿ񴴽�һ���µ�
    public boolean isCreateNormal = false;//�Ƿ��½�
    public boolean isCreateBool = false;//�Ƿ��½�
    /**
     * ��ָ����ģʽ
     * ���壺
     * 1.�������?
     * 2.ʰȡ�ȴ�
     * 3.������
     */
    public int modeP1 = 0;
    public int isMove = 1;  //�ж�ƽ�Ƶ�һ������
    /**
     * ��ǰѡ������
     */
    //////////////
    //�������йصı���
    public int createType = 0;
    public int boolMode;
    /**
     * ����������
     */
    Body fitTargetBody;
    //////////////////////////
    Vector<Body> BodyAll;
    Vector<Body> BodyPick;
    Solid cube;
    Solid sphere;
    Solid cone;
    Solid cylinder;
    Solid unionModel;
    Solid diffModel;
    Solid interModel;
    Solid pm;
    TextureRect texRect;    //������ζ��������
    TextureRectDouble beginFace;    //Ҫ������
    TextureRectDouble endFace;    //��������
    Indesign indesign;//redo��undo����
    private Camera camera;
    /**
     * ��¼ÿ��touchʱ�䴥����ʼʱ������
     */
    private float locationYBeginP1, locationXBeginP1;
    /**
     * ��ָʱtouchʱ�����λ��?
     */
    private float locationXEndP1, locationYEndP1;
    /**
     * ��¼ÿ��touchʱ�䴥��downʱ������
     */
    private float locationYDownP1, locationXDownP1;
    /**
     * ��¼˫ָtouchʱ�䴥����ʼʱ������
     */
    private float locationY0BeginP2, locationX0BeginP2, locationY1BeginP2, locationX1BeginP2;
    /**
     * ��¼˫ָʱtouchʱ�䴥������λ��
     */
    private float locationX0EndP2, locationY0EndP2, locationX1EndP2, locationY1EndP2;
    /**
     * //��¼˫ָDown����ʱ������
     */
    private float locationX0DownP2, locationY0DownP2, locationX1DownP2, locationY1DownP2;
    /**
     * //��¼��ָtouchʱ�䴥����ʼʱ������
     */
    private float locationY0BeginP3, locationX0BeginP3, locationY1BeginP3, locationX1BeginP3, locationY2BeginP3, locationX2BeginP3;
    /**
     * //��¼��ָʱtouchʱ�����λ��?
     */
    private float locationX0EndP3, locationY0EndP3, locationX1EndP3, locationY1EndP3, locationX2EndP3, locationY2EndP3;
    /**
     * //��¼��ָʱtouch��ָ�ɿ�λ��
     */
    private float locationX0DownP3, locationY0DownP3, locationX1DownP3, locationY1DownP3, locationX2DownP3, locationY2DownP3;
    /**
     * //����downʱ��ָ����
     */
    private Vector2f Vector2fP01DownP3, Vector2fP02DownP3, Vector2fP12DownP3;
    /**
     * //˫ָDownʱ��ָ���?
     */
    private float lengthPDownP2 = 1f;
    /**
     * //˫ָdownʱ��ָ����
     */
    private Vector2f Vector2fPDownP2;
    /**
     * ��¼��ָDown����ʱ������
     */
    private Vector2f copyDirection;
    /**
     * 0��ָ������������1��ָֹͣ��������2����������Ӵ�������?
     */
    private Vector2f[] fitFingerDirection = new Vector2f[4];
    /**
     * �������壺
     * 0.δ��ʼʰȡ
     * 1.ʰȡ����������뿪����?
     * 2.ʰȡ����������Ľ������
     * 3.ʰȡ�����������ֹͣ����?
     * 4.ʰȡ��ϣ��ȴ�̧�����?
     */
    private int fitMode = 0;
    private boolean isPush = false;//�ж��Ƿ�������?
    private boolean hasLoad = false;//�Ƿ��ʼ�����
    private boolean isP1Lock = false;//�жϵ���ָ�����Ƿ�����
    private boolean isP2Lock = false;//�ж�˫��ָ�����Ƿ�����
    private boolean isCopy = true;//�Ƿ����MOVE
    //��ǰ�����id
    private int axisTexId, redTexId;
    //��ǰ�����ͼ�?
    private Bitmap axisBm, redBm;
    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();    //����������Ⱦ��
        setRenderer(mRenderer);                //������Ⱦ��
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ
        //���������?
        camera = new Camera();
        //��ʼ����Դ
        MatrixState.setLightLocation(0, 100, 0);

        //��ʼ������ջ�ͻָ�ջ
        indesign = new Indesign();
    }

    public static float[] getStlPrint() {
        return stlPrint;
    }

    /**
     * 3dʰȡ
     *
     * @param locationX
     * @param locationY
     * @return
     */
    public static Body pickupObject(float locationX, float locationY, Vector<Body> objectList) {
        float[] AB = MatrixState.getUnProject(
                locationX,
                locationY);
        //����AB
        Vector3f start = new Vector3f(AB[0], AB[1], AB[2]);//���?
        Vector3f end = new Vector3f(AB[3], AB[4], AB[5]);//�յ�
        Vector3f dir = end.minus(start);//���Ⱥͷ���

        Body temp = null;
        ;//���Ϊû��ѡ���κ�����?
        float minTime = 1;//��¼�б�������������AB�ཻ�����ʱ��?
        float[] Face = new float[4];
        for (Body b : objectList)//�����б��е�����
        {
            Bound box = b.getCurrBox(); //�������AABB��Χ��
            float time = box.rayIntersect(start, dir, null, Face);//�����ཻʱ��
            if (time <= minTime) {
                minTime = time;//��¼��Сֵ
                temp = b;
            }
        }
        if (minTime != 1) {
            return temp;
        }
        return null;
    }

    //�����¼��ص�����
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        locationXBeginP1 = e.getX();    //��¼ÿ��touchʱ�䴥����ʼʱ��Y����
        locationYBeginP1 = e.getY();    //��¼ÿ��touchʱ�䴥����ʼʱ��X����

        if (mode == 2) {
            locationX0BeginP2 = e.getX(0);    //��¼ÿ��touchʱ�䴥����ʼʱ��Y0����
            locationY0BeginP2 = e.getY(0);    //��¼ÿ��touchʱ�䴥����ʼʱ��X0����
            locationX1BeginP2 = e.getX(1);    //��¼ÿ��touchʱ�䴥����ʼʱ��Y1����
            locationY1BeginP2 = e.getY(1);    //��¼ÿ��touchʱ�䴥����ʼʱ��X1����
        } else if (mode == 3) {
            locationX0BeginP3 = e.getX(0);    //��¼ÿ��touchʱ�䴥����ʼʱ��Y0����
            locationY0BeginP3 = e.getY(0);    //��¼ÿ��touchʱ�䴥����ʼʱ��X0����
            locationX1BeginP3 = e.getX(1);    //��¼ÿ��touchʱ�䴥����ʼʱ��Y1����
            locationY1BeginP3 = e.getY(1);    //��¼ÿ��touchʱ�䴥����ʼʱ��X1����
            locationX2BeginP3 = e.getX(2);    //��¼ÿ��touchʱ�䴥����ʼʱ��Y2����
            locationY2BeginP3 = e.getY(2);    //��¼ÿ��touchʱ�䴥����ʼʱ��X2����
        }
        switch (e.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mode = 1;//����Ϊ����ģʽ
                locationXDownP1 = e.getX(0);    //��¼��ָDown����ʱ��X����
                locationYDownP1 = e.getY(0);    //��¼��ָDown����ʱ��Y����

                //ѡ���Ϊ�գ��ƶ����
                if (pickupObject(locationXDownP1, locationYDownP1, BodyAll) == null) {
                    modeP1 = 1;
                }
                //ѡ������Ϊ��ǰ���壬������
                else if (pickupObject(locationXDownP1, locationYDownP1, BodyAll) == curBody) {
                    modeP1 = 2;
                    fitMode = 1;
                }
                //ʰȡ�ȴ�
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
                mode += 1;//����Ϊ˫��ģʽ
                if (mode == 2 && BodyAll.size() != 0) {
                    isP1Lock = true;

                    locationX0DownP2 = e.getX(0);    //��¼˫ָDown����ʱ��X1����
                    locationY0DownP2 = Constant.HEIGHT - e.getY(0);    //��¼˫ָDown����ʱ��Y1����
                    locationX1DownP2 = e.getX(1);    //��¼˫ָDown����ʱ��X1����
                    locationY1DownP2 = Constant.HEIGHT - e.getY(1);    //��¼˫ָDown����ʱ��Y1����

                    Vector2fPDownP2 = new Vector2f(locationX1DownP2 - locationX0DownP2, locationY1DownP2 - locationY0DownP2);//��ָ��ʼ����
                    lengthPDownP2 = Vector2fPDownP2.mod;//��¼˫ָdown����ָ���?

                    //�ж���
                    curBody.switchAxis(Vector2fPDownP2);

                } else if (mode == 3) {
                    isP2Lock = true;

                    isAxle = 0;//���������᲻��ɫ

                    locationX0DownP3 = e.getX(0);    //��¼��ָDown����ʱ��X1����
                    locationY0DownP3 = e.getY(0);    //��¼��ָDown����ʱ��Y1����
                    locationX1DownP3 = e.getX(1);    //��¼��ָDown����ʱ��X2����
                    locationY1DownP3 = e.getY(1);    //��¼��ָDown����ʱ��Y2����
                    locationX2DownP3 = e.getX(2);    //��¼��ָDown����ʱ��X3����
                    locationY2DownP3 = e.getY(2);    //��¼��ָDown����ʱ��Y3����

                    Vector2fP01DownP3 = new Vector2f(locationX0DownP3 - locationX1DownP3, locationY0DownP3 - locationY1DownP3);//��ָ12��ʼ����
                    Vector2fP02DownP3 = new Vector2f(locationX2DownP3 - locationX0DownP3, locationY2DownP3 - locationY0DownP3);//��ָ13��ʼ����
                    Vector2fP12DownP3 = new Vector2f(locationX2DownP3 - locationX1DownP3, locationY2DownP3 - locationY1DownP3);//��ָ23��ʼ����
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == 1 && !isP1Lock) {
                    float locationXMoveP1 = e.getX(0);    //��¼Move����ʱ��X����
                    float locationYMoveP1 = e.getY(0);    //��¼Move����ʱ��Y����

                    //������ƶ���ʰ�?
                    if (modeP1 == 1) {
                        float distanceXMoveP1 = locationXBeginP1 - locationXEndP1; //��¼��ָMove����ʱX���ƶ��ľ���
                        float distanceYMoveP1 = locationYBeginP1 - locationYEndP1;    //��¼��ָMove����ʱY���ƶ��ľ���
                        //�����������ԭ�����?
                        camera.angelA -= distanceXMoveP1 * Constant.CAMERASPEED;
                        camera.angelB += distanceYMoveP1 * Constant.CAMERASPEED;
                        camera.angelB = Math.max(camera.angelB, 0);
                        camera.angelB = Math.min(camera.angelB, (float) (Math.PI / 2));


                    }
                    //������
                    else if (modeP1 == 2 && BodyAll.size() != 0) {
                        if (fitMode == 1) {//ʰȡ���������뿪����
                            if (pickupObject(locationXMoveP1, locationYMoveP1, BodyAll) != curBody) {
                                fitMode = 2;
                                fitFingerDirection[0] = new Vector2f(locationXMoveP1 - locationXDownP1, -(locationYMoveP1 - locationYDownP1));
                                //������
                                beginFace.match(curBody, curBody.getFitTargetFace(fitFingerDirection[0], 1));
                                isShowBeginFace = true;
                            }
                        } else if (fitMode == 2) {//ʰȡ����������������
                            fitTargetBody = pickupObject(locationXMoveP1, locationYMoveP1, BodyAll);
                            if (fitTargetBody != null) {
                                fitMode = 3;
                                fitFingerDirection[2] = new Vector2f(locationXMoveP1, locationYMoveP1);
                            }
                        } else if (fitMode == 3) {//ʰȡ����������ֹͣ���У��뿪����������ص�״�?2
                            if (pickupObject(locationXMoveP1, locationYMoveP1, BodyAll) != fitTargetBody) {
                                fitMode = 2;
                                isShowEndFace = false;
                            } else {
                                fitFingerDirection[1] = new Vector2f(locationXMoveP1 - fitFingerDirection[2].x, -(locationYMoveP1 - fitFingerDirection[2].y));
                                //��������
                                endFace.match(fitTargetBody, fitTargetBody.getFitTargetFace(fitFingerDirection[1], -1));
                                isShowEndFace = true;
                            }
                        }
                    } else if (modeP1 == 3) { //ʰȡ�ȴ�
                        //�ƶ����볬����ֵ��ֹͣʰȡ
                        if (Math.sqrt((locationXMoveP1 - locationXDownP1) * (locationXMoveP1 - locationXDownP1) +
                                (locationYMoveP1 - locationYDownP1) * (locationYMoveP1 - locationYDownP1)) < 1000) {
                            modeP1 = 1;
                        }
                    }
                } else if (mode == 2 && !isP2Lock && BodyAll.size() != 0) {
                    Vector2f Vector2fP1MoveP2 = new Vector2f(e.getX(0) - locationX0DownP2, Constant.HEIGHT - e.getY(0) - locationY0DownP2);//��һ����ָ����
                    Vector2f Vector2fP2MoveP2 = new Vector2f(e.getX(1) - locationX1DownP2, Constant.HEIGHT - e.getY(1) - locationY1DownP2);//�ڶ�����ָ����
                    isPush = true;

                    //˫ָƽ��
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
                    //˫ָ��ת
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
                    //˫ָ����
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
                    Vector2f Vector2fP0 = new Vector2f(e.getX(0) - locationX0DownP3, e.getY(0) - locationY0DownP3);//��һ����ָ����
                    Vector2f Vector2fP1 = new Vector2f(e.getX(1) - locationX1DownP3, e.getY(1) - locationY1DownP3);//�ڶ�����ָ����
                    Vector2f Vector2fP2 = new Vector2f(e.getX(2) - locationX2DownP3, e.getY(2) - locationY2DownP3);//�ڶ�����ָ����
                    //����һ��3DͼԪ
                    if (VectorUtil.Product(Vector2fP0, Vector2fP1) > 0 &&
                            VectorUtil.Product(Vector2fP0, Vector2fP2) > 0 &&
                            VectorUtil.Product(Vector2fP1, Vector2fP2) > 0) {
                        isPush = true;
                        //����һ��3DͼԪ
                        if (isCopy) {

                            isCopy = false;
                            //����һ������
                            isCreateNormal = true;
                            createType = 0;
                            isCreateNew = false;

                        }
                        //�ƶ����Ƶ�3DͼԪ
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
                    //������Զ����ͷ
                    else {
                        float lengthP01MoveP3 = VectorUtil.Length(e.getX(0), e.getY(0), e.getX(1), e.getY(1));
                        float lengthP02MoveP3 = VectorUtil.Length(e.getX(0), e.getY(0), e.getX(2), e.getY(2));
                        float lengthP12MoveP3 = VectorUtil.Length(e.getX(1), e.getY(1), e.getX(2), e.getY(2));
                        if (lengthP01MoveP3 > 10f && lengthP02MoveP3 > 10f && lengthP12MoveP3 > 10f) {

                            float scale01 = lengthP01MoveP3 / (Vector2fP01DownP3.mod);
                            float scale02 = lengthP02MoveP3 / (Vector2fP02DownP3.mod);
                            float scale12 = lengthP12MoveP3 / (Vector2fP12DownP3.mod);
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
                mode -= 1;//����Ϊ����ģʽ
                if (mode == 1) {
                    isAxle = 0;//���������᲻��ɫ
                } else if (mode == 2) {
                    isCopy = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = 0;

                //������
                if (modeP1 == 2 && fitMode == 3 && !isP1Lock && BodyAll.size() != 0) {//��ϵͳ������״̬���������״̧̬����ָʱ���������ϲ���?
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

        locationXEndP1 = locationXBeginP1;    //��¼ÿ��touchʱ�䴥������ʱ��X����
        locationYEndP1 = locationYBeginP1;    //��¼ÿ��touchʱ�䴥������ʱ��Y����
        locationX0EndP2 = locationX0BeginP2;    //��¼ÿ��touchʱ�䴥������ʱ��X����
        locationY0EndP2 = locationY0BeginP2;    //��¼ÿ��touchʱ�䴥������ʱ��Y����
        locationX1EndP2 = locationX1BeginP2;    //��¼ÿ��touchʱ�䴥������ʱ��X����
        locationY1EndP2 = locationY1BeginP2;    //��¼ÿ��touchʱ�䴥������ʱ��Y����

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

    //�½�����
    public void createObject(int Type, boolean isNew) {
        boolean isTemp = false;
        if (isNew == true) {
            if (Type == 1) {
                Solid s = new Solid(MySurfaceView.this, cube);
                BodyAll.add(s);
            } else if (Type == 2) {
                Solid s = new Solid(MySurfaceView.this, cylinder);
                BodyAll.add(s);
            } else if (Type == 3) {
                Solid s = new Solid(MySurfaceView.this, cone);
                BodyAll.add(s);
            } else if (Type == 4) {
                Solid s = new Solid(MySurfaceView.this, sphere);
                BodyAll.add(s);
            }
            isTemp = true;
        } else {
            Solid s = new Solid(MySurfaceView.this, (Solid) curBody);
            BodyAll.add(s);
        }

        curBody = BodyAll.get(BodyAll.size() - 1);
        for (Body b : BodyAll) {
            if (b.isChoosed)
                b.isChoosed = false;
        }
        curBody.isChoosed = true;

        if (isTemp) {
            isTemp = false;
            Vector<Body> temp = new Vector<Body>();
            for (Body e1 : BodyAll) {
                Body tempBody = (Body) e1.clone();
                temp.add(tempBody);
            }
            indesign.addRedoStack(temp);
        }

    }

    //�½���������
    public void createBool(Body b1, Body b2) {
        BooleanModeller bm = new BooleanModeller((Solid) b1, (Solid) b2);
        if (bm.BooleanOp()) {
            Solid boolSolid = null;
            switch (boolMode) {
                case 1:
                    boolSolid = bm.getIntersection(this);
                    break;
                case 2:
                    boolSolid = bm.getUnion(this);
                    break;
                case 3:
                    boolSolid = bm.getDifference(this);
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

            Vector<Body> temp = new Vector<Body>();
            for (Body e1 : BodyAll) {
                Body tempBody = (Body) e1.clone();
                temp.add(tempBody);
            }
            indesign.addRedoStack(temp);
        } else {
            //Toast.makeText(mContext, "�������������������ཻ", Toast.LENGTH_LONG).show();
            curBody2.isChoosed = false;
        }
    }

    //ͨ��IO����ͼƬ
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
        //��������ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //����������id������
                        textures,   //����id������
                        0           //ƫ����
                );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(
                GLES20.GL_TEXTURE_2D, //��������
                0,
                GLUtils.getInternalFormat(bitmapTmp),
                bitmapTmp, //����ͼ��
                GLUtils.getType(bitmapTmp),
                0 //����߿�ߴ�
        );

        if (needRrelease) {
            bitmapTmp.recycle(); //������سɹ����ͷ�ͼ�?
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
            //�����Ȼ�������ɫ����
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            //���ô˷����������͸��ͶӰ����?
            MatrixState.setProjectFrustum(-Constant.RATIO, Constant.RATIO, -1, 1, 10, 400);
            // ���ô˷������������?9����λ�þ���
            MatrixState.setCamera(0, 0, 70, 0, 0, 0, 0, 1, 0);

            MatrixState.pushMatrix();    //��ջ
            //����ť
            //......
            MatrixState.popMatrix();//��ջ

            //��������ͷ
            camera.setCarema();

            if (isCreateNormal == true) {
                createObject(createType, isCreateNew);
                isCreateNormal = false;
            }

            if (isCreateBool == true) {
                //createBool(BodyAll.get(0),BodyAll.get(1));
                createBool(curBody2, curBody);
                isCreateBool = false;
            }

            if (!hasLoad) {
                initTaskReal();
                hasLoad = true;
            } else {
                //��������ƽ�����?
                MatrixState.pushMatrix();    //��ջ
                texRect.drawSelf(axisTexId);    //���ƴ��ı���
                MatrixState.popMatrix();//��ջ

                //����������
                if (isShowBeginFace) {
                    MatrixState.pushMatrix();    //��ջ
                    beginFace.drawSelf(redTexId);
                    MatrixState.popMatrix();//��ջ
                }
                if (isShowEndFace) {
                    MatrixState.pushMatrix();    //��ջ
                    endFace.drawSelf(redTexId);
                    MatrixState.popMatrix();//��ջ
                }

                //����
                for (Body b : BodyAll) {

                    Solid s = (Solid) b;

                    MatrixState.pushMatrix();    //��ջ
                    s.drawSelf(0);
                    //s.drawSelf(1);
                    MatrixState.popMatrix();//��ջ
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
            //���������?
            BodyAll = new Vector<Body>();
            BodyPick = new Vector<Body>();

            //����������ζ���?
            texRect = new TextureRect(8.0f);
            texRect.initShader(ShaderManager.getObjectshaderProgram());
            texRect.Translate(new float[]{0, -1, 0, 1}, 2.0f);    //ƽ��
            texRect.Rotate(1, 0, 0, 90);

            beginFace = new TextureRectDouble(1.0f);
            beginFace.initShader(ShaderManager.getObjectshaderProgram());
            endFace = new TextureRectDouble(1.0f);
            endFace.initShader(ShaderManager.getObjectshaderProgram());

            //����Ҫ���Ƶ�����
            pm = LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(), MySurfaceView.this, 0.2f);
            cone = LoadUtil.loadFromFileVertexOnlyAverage("cone.obj", MySurfaceView.this.getResources(), MySurfaceView.this, 0.2f);
            cube = LoadUtil.loadFromFileVertexOnlyFace("cube.obj", MySurfaceView.this.getResources(), MySurfaceView.this, 0.2f);
            cylinder = LoadUtil.loadFromFileVertexOnlyAverage("cylinder.obj", MySurfaceView.this.getResources(), MySurfaceView.this, 0.2f);
            sphere = LoadUtil.loadFromFileVertexOnlyAverage("sphere.obj", MySurfaceView.this.getResources(), MySurfaceView.this, 0.2f);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ��
            GLES20.glViewport(0, 0, width, height);
            //����GLSurfaceView�Ŀ�߱�?
            Constant.RATIO = (float) width / height;
            Constant.WIDTH = width;
            Constant.HEIGHT = height;

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //�򿪱������?
            GLES20.glEnable(GLES20.GL_CULL_FACE);

            //��ʼ��shader
            ShaderManager.compileShader();
            //��ʼ���任����
            MatrixState.setInitStack();
            //��ʼ�����ص�����
            axisBm = loadTexture(R.drawable.axis);    //����Ĭ������
            redBm = loadTexture(R.drawable.red);//����Ĭ������
            //��ʼ�����ص�����
            axisTexId = initTexture(axisBm, true);    //����Ĭ������
            redTexId = initTexture(redBm, true);//����Ĭ������
        }
    }

}
