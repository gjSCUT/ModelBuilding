package com.bn.Util;

public class MathUtil {
    static double a[][];
    static float[] M;

    //通过doolittle分解解n元一次线性方程组的工具方法
    static double[] doolittle(double a[][]) {
        MathUtil.a = a;
        int rowNum = a.length;//获得未知数的个数
        int xnum = a[0].length - rowNum;// 所求解的组数（一）

        double AugMatrix[][] = new double[10][20];//拓展的增广矩阵

        readData(a, rowNum, xnum, AugMatrix);

        for (int i = 1; i <= rowNum; i++) {
            prepareChoose(i, rowNum, AugMatrix);
            choose(i, rowNum, xnum, AugMatrix);
            resolve(i, rowNum, xnum, AugMatrix);
        }

        findX(rowNum, xnum, AugMatrix);

        double[] result = new double[rowNum];
        for (int i = 0; i < rowNum; i++) {
            result[i] = AugMatrix[i + 1][rowNum + 1];
        }

        return result;
    }

    static void readData(double a[][], int rowNum, int xnum, double AugMatrix[][]) {//增广矩阵的拓展
        for (int i = 0; i <= rowNum; i++) {
            AugMatrix[i][0] = 0;
        }
        for (int i = 0; i <= rowNum + xnum; i++) {
            AugMatrix[0][i] = 0;
        }
        for (int i = 1; i <= rowNum; i++)
            for (int j = 1; j <= rowNum + xnum; j++)
                AugMatrix[i][j] = a[i - 1][j - 1];
    }

    static void prepareChoose(int times, int rowNum, double AugMatrix[][]) {//计算准备选主元
        for (int i = times; i <= rowNum; i++) {
            for (int j = times - 1; j >= 1; j--) {
                AugMatrix[i][times] = AugMatrix[i][times] - AugMatrix[i][j] * AugMatrix[j][times];
            }
        }
    }

    static void choose(int times, int rowNum, int xnum, double AugMatrix[][]) {//选主元
        int line = times;
        for (int i = times + 1; i <= rowNum; i++)//选最大行
        {
            if (AugMatrix[i][times] * AugMatrix[i][times] > AugMatrix[line][times] * AugMatrix[line][times])
                line = i;
        }
        if (AugMatrix[line][times] == 0)//最大数等于零
        {
            System.out.println("doolittle fail !!!");

        }
        if (line != times)//交换
        {
            double temp;
            for (int i = 1; i <= rowNum + xnum; i++) {
                temp = AugMatrix[times][i];
                AugMatrix[times][i] = AugMatrix[line][i];
                AugMatrix[line][i] = temp;
            }
        }
    }

    static void resolve(int times, int rowNum, int xnum, double AugMatrix[][]) {//分解
        for (int i = times + 1; i <= rowNum; i++) {
            AugMatrix[i][times] = AugMatrix[i][times] / AugMatrix[times][times];
        }
        for (int i = times + 1; i <= rowNum + xnum; i++) {
            for (int j = times - 1; j >= 1; j--) {
                AugMatrix[times][i] = AugMatrix[times][i] - AugMatrix[times][j] * AugMatrix[j][i];
            }
        }
    }

    static void findX(int rowNum, int xnum, double AugMatrix[][]) {//求解
        for (int k = 1; k <= xnum; k++) {
            AugMatrix[rowNum][rowNum + k] = AugMatrix[rowNum][rowNum + k] / AugMatrix[rowNum][rowNum];
            for (int i = rowNum - 1; i >= 1; i--) {
                for (int j = rowNum; j > i; j--) {
                    AugMatrix[i][rowNum + k] = AugMatrix[i][rowNum + k] - AugMatrix[i][j] * AugMatrix[j][rowNum + k];
                }
                AugMatrix[i][rowNum + k] = AugMatrix[i][rowNum + k] / AugMatrix[i][i];
            }
        }
    }

    private static float m(int x, int y) {
        return M[4 * x + y];
    }

    private static void swap(int x1, int y1, int x2, int y2) {
        float temp = M[4 * x1 + y1];
        M[4 * x1 + y1] = M[4 * x2 + y2];
        M[4 * x2 + y2] = temp;
    }

    public static float[] InverseMatrix(float[] rhs) {
        M = rhs;
        int[] is = new int[4];
        int[] js = new int[4];


        for (int k = 0; k < 4; k++) {
            // 第一步，全选主元
            float fMax = 0.0f;
            for (int i = k; i < 4; i++) {
                for (int j = k; j < 4; j++) {
                    float temp = Math.abs(m(i, j));
                    if (temp > fMax) {
                        fMax = temp;
                        is[k] = i;
                        js[k] = j;
                    }
                }
            }

            if (is[k] != k) {
                swap(k, 0, is[k], 0);
                swap(k, 1, is[k], 1);
                swap(k, 2, is[k], 2);
                swap(k, 3, is[k], 3);
            }
            if (js[k] != k) {
                swap(0, k, 0, js[k]);
                swap(1, k, 1, js[k]);
                swap(2, k, 2, js[k]);
                swap(3, k, 3, js[k]);
            }


            // 计算逆矩阵

            // 第二步
            M[4 * k + k] = 1.0f / m(k, k);
            // 第三步
            for (int j = 0; j < 4; j++) {
                if (j != k)
                    M[4 * k + j] *= m(k, k);
            }
            // 第四步
            for (int i = 0; i < 4; i++) {
                if (i != k) {
                    for (int j = 0; j < 4; j++) {
                        if (j != k)
                            M[4 * i + j] = m(i, j) - m(i, k) * m(k, j);
                    }
                }
            }
            // 第五步
            for (int i = 0; i < 4; i++) {
                if (i != k)
                    M[4 * i + k] *= -m(k, k);
            }
        }

        for (int k = 3; k >= 0; k--) {
            if (js[k] != k) {
                swap(k, 0, js[k], 0);
                swap(k, 1, js[k], 1);
                swap(k, 2, js[k], 2);
                swap(k, 3, js[k], 3);
            }
            if (is[k] != k) {
                swap(0, k, 0, is[k]);
                swap(1, k, 1, is[k]);
                swap(2, k, 2, is[k]);
                swap(3, k, 3, is[k]);
            }
        }

        return M;
    }


}
