package com.bn.object;

import com.bn.main.MatrixState;
import com.bn.csgStruct.Quaternion;

//�������˫��
public class TextureRectDouble extends Bo

{
	//���ڻ��Ƹ��������ɫ����
	TextureRect t1,t2;
	public TextureRectDouble(float size)
	{
		//�������ڻ��Ƹ��������ɫ����
		t1=new TextureRect(size);
		t2=new TextureRect(size);
	}
	
	@Override
	public void initShader(int mProgram)
	{
		t1.initShader(mProgram);
		t2.initShader(mProgram);
	}
	
	public void drawSelf(int TexId)
	{
		setBody();
		
		//�����ֳ�
		MatrixState.pushMatrix();	

		
		//����
		MatrixState.pushMatrix();	
        t1.drawSelf(TexId);		
		MatrixState.popMatrix();
		
		//����
		MatrixState.pushMatrix();	
		MatrixState.rotate(1, 0, 0, 180);
		t2.drawSelf(TexId);			
		MatrixState.popMatrix();	
		
				
		//�ָ��ֳ�
		MatrixState.popMatrix();
      
	}
	

	 public void match(Body b,float[] face)
	 {
    	quater = new Quaternion(b.quater);
	    xLength=b.xLength;//��x��ƽ�ƾ���
	    yLength=b.yLength;//��y��ƽ�ƾ���
	    zLength=b.zLength;//��x��ƽ�ƾ���
	    xScale=1.2f*b.xScale;//��x���������
	    yScale=1.2f*b.yScale;//��y���������
	    zScale=1.2f*b.zScale;//��z��������� 
	    
	    //����ǰС��
	    if(face[2]==1)
	    {
	  		Translate(face,zScale+0.5f);
	    }
  		//���ƺ�С��
	    else if(face[2]==-1)
	    {
	    	Translate(face,zScale+0.5f);
	    	Rotate( 0, 1, 0, 180);		
	    }
  		//�����ϴ���
	    else if(face[1]==1)
	    {
	    	Translate(face,yScale+0.5f);
	  		Rotate(1, 0, 0 ,90);
	    }
  		//�����´���
	    else if(face[1]==-1)
	    {
	    	Translate(face,yScale+0.5f);
	  		Rotate(1, 0, 0, -90);
	    }
  		//���������
	    else if(face[0]==-1)	
	    {
	    	Translate(face,xScale+0.5f);
	  		Rotate(1, 0, 0, -90);
	  		Rotate(0, 1, 0, 90);
	    }
  		//�����Ҵ���
  		else if(face[0]==1)
  		{			
  			Translate(face,xScale+0.5f);
	  		Rotate(1, 0, 0, 90);
	  		Rotate(0, 1, 0, -90);
  		}
    }
}
