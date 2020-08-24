package com.concordia.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Phases {
	
	 Integer numSubList =0 ;
	 String outputFile="output";
	 Integer numOfPass=0;
	 Integer numInputBuffer=0;
	 Integer blockSiz =0;
	
	public  void phaseOne(MyScanner buff,Integer memRecord,Integer  totalRecord,Integer  subListCount,Integer passesCount,Integer  inputBuffersCount,Integer sizeOfBlock)
	{
	try {
		int count=0;
		int size=0;
		int subListNum=0;
		int[] recordsList;
		int records =memRecord;
		numOfPass = passesCount;
		numInputBuffer = inputBuffersCount;
		blockSiz = sizeOfBlock;
		
		System.out.println("Phase one started..");
		while(size!=totalRecord)
		{
			if(subListCount == 1 && totalRecord<memRecord) {
				recordsList = new int[totalRecord];
				records = totalRecord;
			}
			else {
			if(memRecord<(totalRecord-size)) {
				recordsList = new int[memRecord];
			}
			else {
				recordsList = new int[totalRecord-size];
			}
			}
			int j;
	            while(count!=records)
	            {
	            	j=buff.nextInt();
	            	if(j!=-1) {
	            		 recordsList[count]=j;
	            		 count++;
	            	}
	            	else break;
	            }
			Arrays.sort(recordsList);
			createFile("Output1"+subListNum+".txt");
			writeToFile("Output1"+subListNum+".txt", recordsList);
			size = size+count;
			count=0;
			subListNum++;
			numSubList = subListCount;
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}     
	
}

		
	
	public  void phaseTwo() throws Exception
	{
		System.gc();
		try {
			int i=0;
			long freemem=0;

			System.out.println("Phase two started..");
			if(numSubList==1) //only single output file
			{
				File oldName = new File("Output"+"10.txt");
			    File newName = new File(outputFile);
			    oldName.renameTo(newName);
			    return;
			}
			
			for (int passCount = 1; passCount <= numOfPass; passCount++)
			{
				int initialSublists = 0;
				int initial =0 ;
				int outputLen=0;
				int counter=0;
				int numGroups = (int) Math.ceil((double) numSubList / (double) numInputBuffer);
				int numSub=numSubList;
				for (int grpCount = 0; grpCount < numGroups; grpCount++)
				{
					int total = 0;
					Vector<FileList> fileRead = new  Vector<>();
					
					if((grpCount+1)==numGroups) {
						total=numSub;
					}
					else {
					if(numSubList > numInputBuffer) {
					 total = numInputBuffer;
					}
					}
					if(numSubList > numInputBuffer) {
						
						numSub=numSub-numInputBuffer;
					}
					
					for(i=0;i<total;i++)
					{
						
						try {
							FileList rd = new FileList();
							  rd.fileInputStream = new FileInputStream(new File("Output"+passCount+counter+".txt"));
							  counter++;
							  if(rd.fileInputStream==null) {
								  break;
							  }
							MyScanner buff = new MyScanner(rd.fileInputStream);
							rd.inputBuff = buff;
							fileRead.add(rd);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println(freemem);
							e.printStackTrace();
						}
					}
					
					String outputGroupFile;
					if(numGroups!=1) {
					 outputGroupFile = "Output"+(passCount+1)+grpCount+".txt";
					} 
					else {
						 outputGroupFile = "FinalOutput"+(passCount+1)+grpCount+".txt";
					}
					int j;
					 initial=initialSublists;
						for(int k=0;k<(total);k++) {
							initialSublists++;
							fileRead.get(k).tab= new int[blockSiz];
						for(int u=0;u<blockSiz;u++) {
						//	a = new ArrayList<>();
							if(fileRead.get(k)!=null)
								try {
									j=fileRead.get(k).inputBuff.nextInt();
									if(j!=-1)
										fileRead.get(k).tab[(fileRead.get(k).len)++]=j;
								}catch (Exception e) {
									System.out.println(Runtime.getRuntime().freeMemory());
									e.printStackTrace();
								}
							
						}
						if(fileRead.get(k).len!=0 && fileRead.get(k).len!=blockSiz) {
							while(fileRead.get(k).len!=blockSiz) {
								fileRead.get(k).tab[(fileRead.get(k).len)++]=-1;
							}
						}
					}
						
						
			int minmax=0;
			int[] output = new int[blockSiz]; 
			outputLen=0;
			while(true)
			{
				List<Integer> tempTab = new ArrayList<>();
				int count = 0;
				for(i=(0);i<(total);i++)
				{
					if(fileRead.get(i)==null || fileRead.get(i).tab==null)
					{
						tempTab.add(null);
						count++;
						continue;
					}
					if(fileRead.get(i).l >= (fileRead.get(i).tab.length))
					{
						fileRead.get(i).tab= new int[blockSiz];
						fileRead.get(i).l=0;
						fileRead.get(i).len=0;
						int p;
						
					for(int u=0;u<blockSiz;u++) {
						if(fileRead.get(i)!=null && fileRead.get(i).inputBuff!=null)
							try {
								p=fileRead.get(i).inputBuff.nextInt();
								if(p!=-1)
									fileRead.get(i).tab[(fileRead.get(i).len)++]=p;
								else 
									break;
							}catch (Exception e) {
								System.out.println(Runtime.getRuntime().freeMemory());
								e.printStackTrace();
							}
					}
					if(fileRead.get(i).len!=0 && fileRead.get(i).len!=blockSiz) {
						while(fileRead.get(i).len!=blockSiz) {
							fileRead.get(i).tab[(fileRead.get(i).len)++]=-1;
						}
					}
					
					if(fileRead.get(i).len==0)
					{
							fileRead.get(i).fileInputStream.close();
							fileRead.get(i).inputBuff=null;
							fileRead.get(i).tab=null;
							tempTab.add(null);
							count++;
					}
				else if(fileRead.get(i).tab[(fileRead.get(i).l)]==-1){
					fileRead.get(i).fileInputStream.close();
					fileRead.get(i).inputBuff=null;
					fileRead.get(i).tab=null;
					tempTab.add(null);
					count++;
				}
				else
				{
					if(fileRead.get(i).tab[(fileRead.get(i).l)]!=-1)
					tempTab.add(fileRead.get(i).tab[(fileRead.get(i).l)]);
				}
					}
					else if(fileRead.get(i).tab[(fileRead.get(i).l)]==-1){
						fileRead.get(i).fileInputStream.close();
						fileRead.get(i).inputBuff=null;
						fileRead.get(i).tab=null;
						tempTab.add(null);
						count++;
					}
					else
					{
						if(fileRead.get(i).tab[(fileRead.get(i).l)]!=-1)
						tempTab.add(fileRead.get(i).tab[(fileRead.get(i).l)]);
					}
				}
				
				if(count == (total)) // all buffers have become null
				{
					writeToOutFile(outputGroupFile, output,outputLen);
					break;
				}
				
					minmax = getMin(tempTab);
				
				output[outputLen++]=(fileRead.get(minmax).tab[(fileRead.get(minmax).l)++]);
				if(outputLen>=(blockSiz))
				{
					writeToOutFile(outputGroupFile, output,outputLen);
					output = new int[blockSiz];
					outputLen=0;
				}
				
				
			}
			}
			numSubList = (int) Math.ceil((double) numSubList / (double) (numInputBuffer)) ;
			}
		}catch (Exception e) {
			System.out.println(Runtime.getRuntime().freeMemory());
			System.out.println("***************EXCEPTION******************");
			e.printStackTrace();
		}		
	}
	
	
	
	public static int getMin(List<Integer> tab)
	{
		List<Integer> sort = new ArrayList<>();
		int minIndex = 0;
		for(int i=0;i<tab.size();i++) {
			if(tab.get(i)!=null && tab.get(i)!=-1) {
				sort.add(tab.get(i));
			}
		}
		if(tab!=null && sort!=null) {
			try {
				minIndex = tab.indexOf(Collections.min(sort));
			}
		 catch (Exception e) {
			 System.out.println("444");
			 for(int i=0;i<tab.size();i++) {
					if(tab.get(i)!=null) {
						System.out.println(tab.get(i));
					}
				}
			 for(int i=0;i<sort.size();i++) {
					
						System.out.println(sort.get(i));
					}
				
			 e.printStackTrace();
		}
		}
		return minIndex;
	}
	
	public static void writeToFile(String fileName, int[] tab ) throws IOException
	{
		FileWriter pw = null;
	    int size=tab.length;
		try {
				
			pw = new FileWriter(fileName,true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int j=0;j<tab.length;j++)
			{
				pw.write(String.valueOf(tab[j]));
				pw.write("\n");
			}
	        pw.close();
	}
	
	public static void writeToOutFile(String fileName, int[] tab,int leng ) throws IOException
	{
		FileWriter pw = null;
			try {
				
				pw = new FileWriter(fileName,true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int j=0;j<leng;j++)
			{
				pw.write(String.valueOf(tab[j]));
				pw.write("\n");
			}
	        pw.close();
	}
	
	
	public static void writeToOutFile(String fileName, List<Integer> tab )
	{
		PrintWriter pw = null;
		for(int i = 0;i<tab.size();i++)
		{
			StringBuilder sb = new StringBuilder();
			List<Integer> tuple = tab;
			sb.append(tuple.get(i).toString());
			sb.append(" ");
			sb.append("\n");
				try {
					
					pw = new PrintWriter(new FileWriter(fileName,true));
				} catch (IOException e) {
					e.printStackTrace();
				}
				pw.write(sb.toString());
		        pw.close();
		}
	}
	
	
	public static void createFile(String fileName)
	{
		PrintWriter pw = null;
		
		try {
			
			pw = new PrintWriter(new FileWriter(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
        pw.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
