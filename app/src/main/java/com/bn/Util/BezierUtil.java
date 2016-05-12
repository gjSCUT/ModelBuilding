package com.bn.Util;

import com.bn.csgStruct.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BezierUtil 
{
   
   public static List<Vector2f> getBezierData(List<Vector2f> line, float span)
   {
	   List<Vector2f> result=new ArrayList<>();
	   
	   int n=line.size()-1;
	   
	   if(n<1)
	   {
		   return result;
	   }
	   
	   int steps=(int) (1.0f/span);
	   long[] jiechengNA=new long[n+1];
	   
	   for(int i=0;i<=n;i++)
	   {
		   jiechengNA[i]=jiecheng(i);
	   }
	   
	   for(int i=0;i<=steps;i++)
	   {
		   float t=i*span;
		   if(t>1)
		   {
			   t=1;
		   }
		   float xf=0;
		   float yf=0;
		   
		   float[] tka=new float[n+1];
		   float[] otka=new float[n+1];
		   for(int j=0;j<=n;j++)
		   {
			   tka[j]=(float) Math.pow(t, j); 
			   otka[j]=(float) Math.pow(1-t, j);
		   }
		   
		   for(int k=0;k<=n;k++)
		   {
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+line.get(k).x*xs;
			   yf=yf+line.get(k).y*xs;
		   }
		   result.add(new Vector2f(xf,yf));
	   }
	   return result;
   }
   
   //��׳�
   public  static long jiecheng(int n)
   {
	   long result=1;
	   if(n==0)
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++)
	   {
		   result=result*i;
	   }
	   
	   return result;
   }
}
