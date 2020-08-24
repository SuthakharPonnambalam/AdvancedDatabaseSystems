package com.concordia.database;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class MyScanner {
	int c;
     BufferedInputStream in;
 
    boolean firstLine;

    public MyScanner(InputStream stream) {
    	 in = new BufferedInputStream(stream);
       try {
          firstLine = true;
          c  = (char)in.read();
       } catch (IOException e) {
          c  = -1;
       }
    }

    public boolean hasNext() {
       if (!firstLine) 
          throw new Error("hasNext only works "+
          "after a call to nextLine");
       return c != -1;
    }

    public String next() {
    	StringBuffer sb = new StringBuffer();
       firstLine = false;
       try {
          while (c <= ' ' && c!=-1) {
             c = in.read();
          } 
          while (c > ' ') {
             sb.append((char)c);
             c = in.read();
          }
       } catch (IOException e) {
          c = -1;
          return "";
       }
       return sb.toString();
    }

    public String nextLine() {
       StringBuffer sb = new StringBuffer();
       firstLine = true;
       try {
          while (c != '\n') {
             sb.append((char)c);
             c = in.read();
          }
          c = in.read();
       } catch (IOException e) {
          c = -1;
          return "";
       }
       return sb.toString();   
    }

    public int nextInt() {
       String s = next();
       try {
    	   if(!s.isEmpty()) {
          return Integer.parseInt(s);}
    	   else {
    		   in.close();
    		   return -1;
    	   }
       } catch (Exception e) {
    	   System.out.println("Empty");
          return -1; 
       }
    }

    public double nextDouble() {
       return new Double(next());
    }

    public long nextLong() {
       return Long.parseLong(next());
    } 

    public void useLocale(int l) {}
 }


