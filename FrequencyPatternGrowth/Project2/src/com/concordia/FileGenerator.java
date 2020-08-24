package com.concordia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileGenerator {

	public static void main(String args[]) throws IOException {
		
		BufferedWriter brC = new BufferedWriter(new FileWriter("Input9s.txt"));
		//BufferedWriter brS = new BufferedWriter(new FileWriter("NewOutput_Space.txt"));
		int noOfBaskets = 500, minsup = 30;
		brC.write(noOfBaskets+" "+minsup+"\n");
		for(int i=0; i<noOfBaskets; i++)
		{
			int sizeOfBasket = (int)(Math.random()*100);
			while(sizeOfBasket==0)
				sizeOfBasket = (int)(Math.random()*100);
			StringBuilder s = new StringBuilder();
			StringBuilder s1 = new StringBuilder();
			//s.append(i+1);
			s.append("{");
			for(int j=0; j<sizeOfBasket; j++)
			{
				int rand = (int)(Math.random()*100);
				while(rand==0)
					rand = (int)(Math.random()*100);
				if(j != sizeOfBasket-1)
				s.append(rand+",");
				else
				s.append(rand);
			}
			s.append("}");
			brC.write(s.toString()+"\n");
			//brS.write(s1.toString()+"\n");
			
		}
		
		brC.close();
		//brS.close();
	}
}


