package com.concordia.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


class FileList {
	public MyScanner inputBuff;
	public int l=0;
	public int len=0;
	public FileInputStream fileInputStream;
	public int[] tab;
}



public class TwoPhaseMergeSort {

	static final int sizeOfRecord = 4;
	static  Integer sizeOfBlock =0;
	static  Integer mainMemoryBuffers=0;
	static  Integer inputBuffers=0;
	static  Integer pagesCount=(1000000*4)/(1024);
	static Integer passesCount=0;
	static String outputFile="output";
	static  Integer totalNumRec =0;
	static  Integer memoryRecords =0;
	static  Integer subListCount =0 ;
	
	
	public static void main(String[] args) throws Exception {
		FileInputStream  fileInputStream = new FileInputStream(new File("input.txt"));
		MyScanner inputBuff = new MyScanner(fileInputStream);
		totalNumRec=inputBuff.nextInt();
		 String memory=	inputBuff.next();
		 long free = Runtime.getRuntime().freeMemory()/2;
		 memoryRecords = (int) ((free)/((sizeOfRecord)));
		 subListCount = (int) Math.ceil(((double)totalNumRec) / ((double)(memoryRecords))) ;
		 sizeOfBlock = 1024;
		 
		 if(sizeOfBlock<=0) {
			 sizeOfBlock = 1;
		 }
		 mainMemoryBuffers = (int) ((free)/(sizeOfBlock*4) );
		 inputBuffers = mainMemoryBuffers - 1;
		 pagesCount=(1000000*4)/(1024);
		 passesCount = (int) Math.ceil( (Math.log10(subListCount) / Math.log10(inputBuffers)) );
		 long startTime = System.nanoTime();
		 System.out.println("Start time: "+ startTime);
		 Phases phaseObject = new Phases();
		 
		try {
			
			phaseObject.phaseOne(inputBuff,memoryRecords,totalNumRec,subListCount,passesCount,inputBuffers,sizeOfBlock);
			fileInputStream.close();
			phaseObject.phaseTwo();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			long endTime = System.nanoTime();
			 System.out.println("End time: "+ endTime);
			 System.out.println();
			System.out.println("Total Time taken :"+((endTime-startTime)/1000000000.0));
	}
	
	
}





 

  