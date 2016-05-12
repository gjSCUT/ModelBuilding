precision mediump float;				//����Ĭ�ϵĸ��㾫��
uniform highp int isShadow;			//��Ӱ���Ʊ�־
varying vec4 vColor; //���մӶ�����ɫ�������Ĳ���
varying vec4 ambient;  				//�Ӷ�����ɫ�����ݹ����Ļ���������ǿ��
varying vec4 diffuse;					//�Ӷ�����ɫ�����ݹ�����ɢ�������ǿ��
varying vec4 specular;				//�Ӷ�����ɫ�����ݹ����ľ��������ǿ��
void main() { 
   	if(isShadow==0){						//�������屾��
	    //�ۺ�����ͨ���������ǿ�ȼ�ƬԪ����ɫ���������ƬԪ����ɫ�����ݸ�����
	    gl_FragColor = vColor*ambient+vColor*specular+vColor*diffuse;
   	}else{								//������Ӱ
	   gl_FragColor = vec4(0.1,0.1,0.1,0.0);;//ƬԪ������ɫΪ��Ӱ����ɫ
   	}
}     
