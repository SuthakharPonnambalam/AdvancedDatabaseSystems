package com.concordia;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Node{

	Node ancestor, nodeLink ; 
	int label = -1, count = 1; 

	List<Node> child = new ArrayList<Node>();
	
	Node getLabel(int label) 
	{
		for(Node child : child)
		{
			if(label == child.label )
				return child;
		}
		return null;
	}
}






public class FrequenPatternGrowth {
	
	
	
	int[] outputBuffer = null;
	int maxLen = 1000;
	HashMap<Integer , Integer> oneItemCount ;
	
	BufferedWriter bWriter = null; 
	int bufferSize = 2000;
	int[] itemBuffer = null;
	Node[] nodeBuffer = null;
	
	FPTree tree;
	
	
	int bucketNum = 0; 
	int noOfFreqItemSet; 
	int minimumSupport;
	
	Integer count;
	int item;
	BufferedReader bReader;
	String[] arrayData;
	
	class FPTree

	{
		List<Integer> header = null;
		Map<Integer, Node> itemNode = new HashMap<Integer, Node>();
		Map<Integer, Node> itemNodeEnd = new HashMap<Integer, Node>();
		Node root = new Node();

		
		void Header(Map<Integer, Integer> oneItemCount) 
		{
			header =  new ArrayList<Integer>(itemNode.keySet());
			Collections.sort(header, new Comparator<Integer>()
			{
				public int compare(Integer id1, Integer id2){
					int compare = oneItemCount.get(id2) - oneItemCount.get(id1);
					if(compare==0)
						return id1-id2;
					return compare;
				}
			});
		}
		
		
		public void addItemSets(List<Integer> usefulItemSets) {
			Node currNode = root;
			for(Integer item : usefulItemSets){
				Node child = currNode.getLabel(item);
				if(child == null){ 
					Node newNode = new Node();
					newNode.label = item;
					newNode.ancestor = currNode;
					currNode.child.add(newNode);
					currNode = newNode;
					fixNodeLinks(item, newNode);	
				}else{ 
					child.count++;
					currNode = child;
				}
			}
			
		}
		
		
		  void fixNodeLinks(Integer item, Node newNode) 
		  {
			Node lastNode = itemNodeEnd.get(item);
			if(lastNode != null) {
				lastNode.nodeLink = newNode;
			} 
			itemNodeEnd.put(item, newNode); 
			Node headernode = itemNode.get(item);
			if(headernode == null){  
				itemNode.put(item, newNode);
			}
		}
		  
		  
		void previousPath(List<Node> prefixPath, Map<Integer, Integer> singletonItemCountBeta, int relativeMinsupp) {
			int pathCount = prefixPath.get(0).count;  
			Node currNode = root;
			for(int i = prefixPath.size() -1; i >=1; i--){ 
				Node pathItem = prefixPath.get(i);
				if(singletonItemCountBeta.get(pathItem.label) >= relativeMinsupp){
					Node child = currNode.getLabel(pathItem.label);
					if(child == null){ 
						Node newNode = new Node();
						newNode.label = pathItem.label;
						newNode.ancestor = currNode;
						newNode.count = pathCount;  
						currNode.child.add(newNode);
						currNode = newNode;
						fixNodeLinks(pathItem.label, newNode);		
					}else{ 
						child.count += pathCount;
						currNode = child;
					}
				}
			}
		}

		
	}

	
	
	 void checkMinSupportValue(String iFile, String ofile) throws IOException 
	{
		
		 String line;
		 tree = new FPTree();
		 bWriter = new BufferedWriter(new FileWriter(ofile)); 
		 BufferedReader bReader = new BufferedReader(new FileReader(iFile));
		
		bReader.readLine();
	
		for(int i=0; i<bucketNum; i++)
		{ 
			line =  bReader.readLine();
			line = line.replace("{", "");
			line = line.replace("}", "");
			
			arrayData = line.split(",");
			
			List<Integer> usefulItemSets = new ArrayList<Integer>();
			
			for(int j=1; j<arrayData.length; j++)
			{
				Integer item = Integer.parseInt(arrayData[j]);
				if(minimumSupport <= oneItemCount.get(item)  )
				{
					usefulItemSets.add(item);
				}
			}
			
			
			Collections.sort(usefulItemSets, new Comparator<Integer>()
			{
				public int compare(Integer item1, Integer item2)
				{
					int compare = oneItemCount.get(item2) - oneItemCount.get(item1);
					if(compare == 0)
						return (item2 - item1);
					
					return compare;
				}
			});
			
			
			tree.addItemSets(usefulItemSets);
		}
		
		bReader.close();
		
		tree.Header(oneItemCount);
		
		if(tree.header.size() > 0) 
		{
			itemBuffer = new int[bufferSize];
			nodeBuffer = new Node[bufferSize];
			fp(tree, itemBuffer, 0, bucketNum, oneItemCount);
		}
		
		if(bWriter != null)
		{
			bWriter.close();
		}
	
	
	}	
	 
	 
	  void fp(FPTree tree, int [] prefix, int pLen, int pSupport, Map<Integer, Integer> oneItemCount) throws IOException 
	  {
		
		if(maxLen == pLen)
		{
			return;
		}
		
		int pos = 0;
		boolean oneBranch = true;
		
		if(tree.root.child.size() > 1)
		{
			oneBranch = false;
		}
		else 
		{
			Node currNode = tree.root.child.get(0);
			
			while(true)
			{
				if(currNode.child.size() > 1) 
				{
					oneBranch = false;
					break;
				}
				
				nodeBuffer[pos] = currNode;				
				pos++;
				
				if(currNode.child.size() == 0)
				{
					break;
				}
				currNode = currNode.child.get(0);
			}
		}		
		
		
		if(oneBranch)
		{	
			combinePrefix(nodeBuffer, pos, prefix, pLen);
		}
		else 
		{
			for(int i = tree.header.size()-1; i>=0; i--)
			{
				Integer item = tree.header.get(i);
				int support = oneItemCount.get(item);
				prefix[pLen] = item;
				
				int support2 = (pSupport < support) ? pSupport: support;
				saveSet(prefix, pLen+1, support2);			
				
				if(pLen+1 < maxLen)
				{
					List<List<Node>> prefixPaths = new ArrayList<List<Node>>();
					Node nodePath = tree.itemNode.get(item);
					Map<Integer, Integer> singletonItemCountBeta = new HashMap<Integer, Integer>();	
					
					while(nodePath != null)
					{
						if(nodePath.ancestor.label != -1)
						{
							List<Node> prefixPath = new ArrayList<Node>();
							prefixPath.add(nodePath);   
							
							int pathCount = nodePath.count;
							Node ancestor = nodePath.ancestor;
							
							while(ancestor.label != -1)
							{
								prefixPath.add(ancestor);
								
								
								
								
								if(singletonItemCountBeta.get(ancestor.label) != null)
								{
									singletonItemCountBeta.put(ancestor.label, singletonItemCountBeta.get(ancestor.label) + pathCount);
								}
								else
								{
									singletonItemCountBeta.put(ancestor.label, pathCount);
								}
								
								
								
								
								ancestor = ancestor.ancestor;
							}
							
							
							prefixPaths.add(prefixPath);
						}
						nodePath = nodePath.nodeLink;
					}
					FPTree fpTreeObj = new FPTree();
				
					
					for(List<Node> prefixPath : prefixPaths)
					{
						fpTreeObj.previousPath(prefixPath, singletonItemCountBeta, minimumSupport); 
					}  
					
					if(fpTreeObj.root.child.size() > 0)
					{
						fpTreeObj.Header(singletonItemCountBeta); 
						fp(fpTreeObj, prefix, pLen+1, support2, singletonItemCountBeta);
					}
				}
			}
		}
		
	}
	  void findSingletonCount(String iFile) throws IOException
	  {
		
		outputBuffer = new int[bufferSize];
		bReader = new BufferedReader(new FileReader(iFile));
		arrayData = bReader.readLine().split(" ");
	
		bucketNum = Integer.parseInt(arrayData[0]);
		minimumSupport = Integer.parseInt(arrayData[1]);
		
		
		double val = Double.parseDouble(arrayData[1]) / Double.parseDouble(arrayData[0]);
		System.out.println("min support is :"+val);
		noOfFreqItemSet = 0;
		String line ;
		
		
		oneItemCount = new HashMap<>();
		
		
		for(int i=0; i<bucketNum; i++)
		{
			line = bReader.readLine();
			
			line = line.replace("{", "");
			line = line.replace("}", "");
		
			arrayData = line.split(",");
			for(int j=1; j<arrayData.length; j++)
			{
				item = Integer.parseInt(arrayData[j]);
				count = oneItemCount.get(item);
				
				
				
				if(count != null)
					oneItemCount.put(item, ++count);
				else
					oneItemCount.put(item, 1);
					
			}
		}
		bReader.close();	
	}
		


	  void combinePrefix(Node[] nodeBuffer, int pos, int[] prefix, int pLen) throws IOException {

		int support = 0;
loop1:	for (long i = 1, max = 1 << pos; i < max; i++)
		{
			int newPrefixLength = pLen;
			for (int j = 0; j < pos; j++) {
				int isSet = (int) i & (1 << j);
				if (isSet > 0) {
					if(newPrefixLength == maxLen){
						continue loop1;
					}
					
					prefix[newPrefixLength++] = nodeBuffer[j].label;
						support = nodeBuffer[j].count;
				}
			}
			saveSet(prefix, newPrefixLength, support);
		}
	}
	  void saveSet(int [] itemset, int itemsetLength, int support) throws IOException {
			
			noOfFreqItemSet++;
			System.arraycopy(itemset, 0, outputBuffer, 0, itemsetLength);
			//Arrays.sort(outputBuffer, 0, itemsetLength);
			Arrays.parallelSort(outputBuffer, 0, itemsetLength);
			StringBuilder buffer = new StringBuilder();
			buffer.append("{");
			buffer.append(outputBuffer[0]);
			for(int i=1; i< itemsetLength; i++)
				buffer.append(","+outputBuffer[i]);
			buffer.append("}");
			buffer.append("#C: ");
			buffer.append(support);
			if(buffer.charAt(3)=='}'||buffer.charAt(2)=='}')
				noOfFreqItemSet--;
			else
				bWriter.write(buffer.toString()+"\n");
	}
	
	
	public static void main(String [] arg) throws FileNotFoundException, IOException{
		String input = "C:\\\\Users\\\\Suthakhar\\\\Desktop\\\\ADB\\\\Project2\\\\Project2\\\\Input3s.txt";  
		String output = "C:\\\\Users\\\\Suthakhar\\\\Desktop\\\\ADB\\\\Project2\\\\Project2\\\\outputFP.txt";
		FrequenPatternGrowth ob = new FrequenPatternGrowth();
		long startTime = System.currentTimeMillis();
		ob.findSingletonCount(input); 
		ob.checkMinSupportValue(input,output);
		long endTime = System.currentTimeMillis();
		System.out.println("Time :"+(endTime-startTime)+" ms");
		
	}
}




