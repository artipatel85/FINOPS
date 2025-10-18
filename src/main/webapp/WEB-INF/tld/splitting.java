package com.shikar.prod.struts.util;
import java.lang.Object;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.StringBuffer;
import java.lang.String;
public class splitting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s1="";
		String s="vamsi krishna velagapudi";
		StringTokenizer st = new StringTokenizer(s," ");
		
		long currentTime = System.currentTimeMillis ();
		java.util.Date date = new java.util.Date();
	    long t = date.getTime();
	    java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
		
		while (st.hasMoreTokens())
		{
		s1 = s1 + st.nextToken().charAt(0); 
		}
				
		System.out.print(s1);
	}
	 
}

