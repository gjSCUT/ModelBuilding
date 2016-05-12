precision mediump float;				//给出默认的浮点精度
uniform highp int isShadow;			//阴影绘制标志
varying vec4 vColor; //接收从顶点着色器过来的参数
varying vec4 ambient;  				//从顶点着色器传递过来的环境光最终强度
varying vec4 diffuse;					//从顶点着色器传递过来的散射光最终强度
varying vec4 specular;				//从顶点着色器传递过来的镜面光最终强度
void main() { 
   	if(isShadow==0){						//绘制物体本身
	    //综合三个通道光的最终强度及片元的颜色计算出最终片元的颜色并传递给管线
	    gl_FragColor = vColor*ambient+vColor*specular+vColor*diffuse;
   	}else{								//绘制阴影
	   gl_FragColor = vec4(0.1,0.1,0.1,0.0);;//片元最终颜色为阴影的颜色
   	}
}     
