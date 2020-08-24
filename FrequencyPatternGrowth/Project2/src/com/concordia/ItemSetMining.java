package com.concordia;

import java.util.Arrays;
import java.util.Comparator;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;



import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import java.io.IOException;


class MemoryLogger {
	
	 static MemoryLogger instance = new MemoryLogger();

	 double maxMemory = 0;
	
	
	 public double getMaxMemory() {
			return maxMemory;
		}

	 
	 public static MemoryLogger getInstance(){
		return instance;
	}
	
	
	public void reset(){
		maxMemory = 0;
	}
	
	public void checkMemory() {
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
				/ 1024d / 1024d;
		if (currentMemory > maxMemory) {
			maxMemory = currentMemory;
		}
	}
}




public class ItemSetMining {

	int itemCount;
	double minSupportValue;
	ItemClass[] itemObj;
	int[] res;
	int resLen = 0;
	int resultCount = 0;

	int[][] buff;
	int buffCurrent;
	int buffSize;
	int buffCol;
	int buffCurrSize;

	
	int nlLenSum = 0;
	PCTreeNode ppcRoot;

	TreenNode root;
	int[] itemsetCount;
	int[] nStart;
	int nList;
	int[] nLength;
	int firstNlistBegin;
	
	int PPCNodeCount;
	int[] suppDict;
	int[] posDict;

	int[] sameItems;
	int nodeCounter;
	
	long startTimestamp;
	long endTimestamp;

	int outputCount = 0;

	BufferedWriter writer = null;

	class Counter {
		int count;
	}

	class ItemClass {
		int num;
		int index;

	}

	class TreenNode {
		int label;
		TreenNode firstChild;
		TreenNode next;
		int support;
		int NLStartinBf;
		int NLLength;
		int NLCol;
	}

	class PCTreeNode {
		int label;
		PCTreeNode firstChild;
		PCTreeNode rightSibling;
		PCTreeNode father;
		int count;
		int preIndex;
		int postIndex;
	}

	int transCounter;
	
	void initialize() {

		TreenNode lastChild = null;

		for (int a = itemCount - 1; a >= 0; a--) {
			TreenNode nlNode = new TreenNode();
			nlNode.label = a;
			nlNode.support = 0;
			nlNode.NLStartinBf = nStart[a];
			nlNode.NLLength = nLength[a];
			nlNode.NLCol = buffCol;
			nlNode.firstChild = null;
			nlNode.next = null;
			nlNode.support = itemObj[a].num;

			if (root.firstChild == null) {
				root.firstChild = nlNode;
				lastChild = nlNode;
			} else {
				lastChild.next = nlNode;
				lastChild = nlNode;
			}
		}
	}
	
	
	

	public static void main(String[] args) {
		String ifile = "C:\\Users\\Suthakhar\\Desktop\\ADB\\Project2\\Project2\\Input9s.txt";  
		String ofile = "C:\\Users\\Suthakhar\\Desktop\\ADB\\Project2\\Project2\\Output_Proj2\\outputItem.txt";
	
		try {
			new ItemSetMining().start(ifile, ofile);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	void readData(String ifile) throws IOException {
		transCounter = 0;

		Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>();

		BufferedReader reader = new BufferedReader(new FileReader(ifile));
		String line;

		line = reader.readLine();
		String[] min = line.split(" ");
		minSupportValue = Double.parseDouble(min[1]) / Double.parseDouble(min[0]);

		System.out.println("min support is : " + minSupportValue);

		while (((line = reader.readLine()) != null)) {

			/*
			 * if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%'
			 * || line.charAt(0) == '@') { continue; }
			 */

			transCounter++;

			line = line.replace("{", "");
			line = line.replace("}", "");

			String[] lineArray = line.split(",");

			for (String itemString : lineArray) {

				Integer itemObj = Integer.parseInt(itemString);
				Integer count = mapItemCount.get(itemObj);
				if (count != null) {
					mapItemCount.put(itemObj, ++count);
				} else {
					mapItemCount.put(itemObj, 1);
				}
			}

		}

		reader.close();
		this.minSupportValue = (int) Math.ceil(minSupportValue * transCounter);
		itemCount = mapItemCount.size();
		ItemClass[] tempItems = new ItemClass[itemCount];
		int c = 0;
		for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			if (entry.getValue() >= minSupportValue) {
				tempItems[c] = new ItemClass();
				tempItems[c].index = entry.getKey();
				tempItems[c].num = entry.getValue();
				c++;
			}
		}

		itemObj = new ItemClass[c];
		System.arraycopy(tempItems, 0, itemObj, 0, c);
		itemCount = itemObj.length;
		Arrays.parallelSort(itemObj, comp);
	}
	
	void fileWriter(TreenNode curNode, int sameCount) throws Exception

	{

		StringBuilder buffer = new StringBuilder();

		if (curNode.support >= minSupportValue) 
		{
			outputCount++;

			if (resLen > 1) 
			{
				for (int i = 0; i < resLen; i++) 
				{

					buffer.append(itemObj[res[i]].index);
					buffer.append(' ');
				}

				buffer.append("#C: ");
				buffer.append(curNode.support);
				buffer.append("\n");
			}
		}

		if (sameCount > 0) 
		{

			for (long i = 1, max = 1 << sameCount; i < max; i++) 
			{
				for (int k = 0; k < resLen; k++) 
				{
					buffer.append(itemObj[res[k]].index);
					buffer.append(' ');
				}

				for (int j = 0; j < sameCount; j++) 
				{

					int isSet = (int) i & (1 << j);
					
					if (isSet > 0) 
					{

						buffer.append(itemObj[sameItems[j]].index);
						buffer.append(' ');

					}
				}
				buffer.append("#C: ");
				buffer.append(curNode.support);
				buffer.append("\n");
				outputCount++;
			}
		}

		writer.write(buffer.toString());
	}
	

	void start(String ifile, String ofile) throws Exception {

		nodeCounter = 0;
		ppcRoot = new PCTreeNode();
		root = new TreenNode();
		MemoryLogger.getInstance().reset();
		writer = new BufferedWriter(new FileWriter(ofile));

		startTimestamp = System.currentTimeMillis();

		buffSize = 1000000;
		buff = new int[100000][];
		buffCurrSize = buffSize * 10;
		buff[0] = new int[buffCurrSize];

		buffCol = 0;
		buffCurrent = 0;

		readData(ifile);
		resLen = 0;
		res = new int[itemCount];

		buildTree(ifile);

		root.label = itemCount;
		root.firstChild = null;
		root.next = null;

		initialize();
		sameItems = new int[itemCount];

		int from_cursor = buffCurrent;
		int from_col = buffCol;
		int from_size = buffCurrSize;

		TreenNode curNode = root.firstChild;
		TreenNode next = null;

		while (curNode != null) {
			next = curNode.next;

			traverse(curNode, root, 1, 0);

			for (int c = buffCol; c > from_col; c--) {
				buff[c] = null;
			}
			buffCol = from_col;
			buffCurrent = from_cursor;
			buffCurrSize = from_size;
			curNode = next;
		}
		writer.close();
		MemoryLogger.getInstance().checkMemory();
		endTimestamp = System.currentTimeMillis();
		System.out.println("Time Taken is : " + (endTimestamp - startTimestamp) + "ms");
	}

	void buildTree(String ifile) throws Exception {

		PPCNodeCount = 0;
		ppcRoot.label = -1;

		BufferedReader reader = new BufferedReader(new FileReader(ifile));
		String line;

		nStart = new int[itemCount];
		nLength = new int[itemCount];

		for (int i = 0; i < itemCount; i++) {
			nLength[i] = 0;
		}

		reader.readLine();

		ItemClass[] transaction = new ItemClass[1000];

		while (((line = reader.readLine()) != null))

		{

			/*
			 * if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%'
			 * || line.charAt(0) == '@') { continue; }
			 */

			line = line.replace("{", "");
			line = line.replace("}", "");

			String[] lineArray = line.split(",");

			int tLen = 0;

			for (String itemString : lineArray) {

				int itemX = Integer.parseInt(itemString);

				for (int j = 0; j < itemCount; j++) {

					if (itemObj[j].index == itemX) {
						transaction[tLen] = new ItemClass();
						transaction[tLen].index = itemX;
						transaction[tLen].num = 0 - j;
						tLen++;
						break;
					}
				}
			}

			Arrays.parallelSort(transaction, 0, tLen, comp);

			int curPos = 0;
			PCTreeNode curRoot = (ppcRoot);
			PCTreeNode rightSibling = null;

			while (tLen != curPos) {

				PCTreeNode child = curRoot.firstChild;

				while (child != null) {
					if (child.label == 0 - transaction[curPos].num) {
						curPos++;
						child.count++;
						curRoot = child;
						break;
					}
					if (child.rightSibling == null) {
						rightSibling = child;
						child = null;
						break;
					}
					child = child.rightSibling;
				}
				if (child == null) {
					break;
				}
			}

			for (int j = curPos; j < tLen; j++) {
				PCTreeNode ppcNode = new PCTreeNode();
				ppcNode.label = 0 - transaction[j].num;

				if (rightSibling == null) {
					curRoot.firstChild = ppcNode;
				} else {
					rightSibling.rightSibling = ppcNode;
					rightSibling = null;
				}

				ppcNode.firstChild = null;
				ppcNode.rightSibling = null;

				ppcNode.father = curRoot;
				ppcNode.count = 1;
				curRoot = ppcNode;
				PPCNodeCount++;
				nLength[ppcNode.label]++;
			}
		}

		reader.close();

		int sum = 0;

		for (int i = 0; i < itemCount; i++) {
			nStart[i] = sum;
			sum += nLength[i];
		}

		if (buffCurrSize * 0.85 < buffCurrent + sum) {
			buffCol++;
			buffCurrent = 0;
			buffCurrSize = sum + 1000;
			buff[buffCol] = new int[buffCurrSize];
		}
		nList = buffCol;
		firstNlistBegin = buffCurrent;
		buffCurrent += sum;

		int pre = 0;
		int post = 0;
		PCTreeNode root = ppcRoot.firstChild;

		suppDict = new int[PPCNodeCount + 1];
		posDict = new int[PPCNodeCount + 1];// XXX inserted
		while (root != null) {
			root.preIndex = pre;
			suppDict[pre] = root.count;

			int cursor = firstNlistBegin + nStart[root.label];
			buff[nList][cursor] = pre;
			nStart[root.label]++;

			pre++;

			if (root.firstChild != null) {
				root = root.firstChild;
			} else {

				if (root != ppcRoot) {
					root.postIndex = post;
					posDict[root.preIndex] = post;
					post++;
				}

				if (root.rightSibling != null) {
					root = root.rightSibling;
				} else {
					root = root.father;
					while (root != null) {

						if (root != ppcRoot) {
							root.postIndex = post;
							posDict[root.preIndex] = post;
							post++;
						}

						if (root.rightSibling != null) {
							root = root.rightSibling;
							break;
						}
						root = root.father;
					}
				}
			}
		}

		for (int i = 0; i < itemCount; i++) {
			nStart[i] = nStart[i] - nLength[i];
		}
	}

	
	
	
	
	
	
	

	

	TreenNode itemSetFreq(TreenNode x, TreenNode y, TreenNode lastChild, Counter sameCountRef) {
		if (buffCurrSize < buffCurrent + x.NLLength) {
			buffCol++;
			buffCurrent = 0;
			buffCurrSize = buffSize > x.NLLength * 1000 ? buffSize : x.NLLength * 1000;
			buff[buffCol] = new int[buffCurrSize];
		}

		TreenNode nlNode = new TreenNode();
		nlNode.support = x.support;
		nlNode.NLStartinBf = buffCurrent;
		nlNode.NLCol = buffCol;
		nlNode.NLLength = 0;

		int cursor_x = x.NLStartinBf;
		int cursor_y = y.NLStartinBf;
		int col_x = x.NLCol;
		int col_y = y.NLCol;
		int preX, postX, preY, postY;

		while (cursor_x < x.NLStartinBf + x.NLLength && cursor_y < y.NLStartinBf + y.NLLength) {
			preX = buff[col_x][cursor_x];
			postX = posDict[preX];

			preY = buff[col_y][cursor_y];
			postY = posDict[preY];

			if (postX > postY) {
				cursor_y++;
			} else {
				if (postX < postY && preX > preY) {
					cursor_x++;
				} else {
					buff[buffCol][buffCurrent++] = preX;
					nlNode.support -= suppDict[preX];
					nlNode.NLLength++;
					cursor_x++;
				}
			}
		}

		while (cursor_x < x.NLStartinBf + x.NLLength) {
			preX = buff[col_x][cursor_x];
			buff[buffCol][buffCurrent++] = preX;
			nlNode.support -= suppDict[preX];
			nlNode.NLLength++;
			cursor_x++;
		}

		if (nlNode.support >= minSupportValue) {
			if (x.support == nlNode.support)
				sameItems[sameCountRef.count++] = y.label;
			else {
				nlNode.label = y.label;
				nlNode.firstChild = null;
				nlNode.next = null;

				if (x.firstChild != null) {
					lastChild.next = nlNode;
					lastChild = nlNode;
				} else {
					x.firstChild = nlNode;
					lastChild = nlNode;
				}
			}
			return lastChild;
		} else {
			buffCurrent = nlNode.NLStartinBf;
		}
		return lastChild;
	}

	TreenNode iskItemSetFreq2(TreenNode x, TreenNode y, TreenNode lastChild, Counter sameCountRef) {

		if (buffCurrent + x.NLLength > buffCurrSize) {
			buffCol++;
			buffCurrent = 0;
			buffCurrSize = buffSize > x.NLLength * 1000 ? buffSize : x.NLLength * 1000;
			buff[buffCol] = new int[buffCurrSize];
		}

		TreenNode nlNode = new TreenNode();
		nlNode.support = x.support;
		nlNode.NLStartinBf = buffCurrent;
		nlNode.NLCol = buffCol;
		nlNode.NLLength = 0;

		int cursor_x = x.NLStartinBf;
		int cursor_y = y.NLStartinBf;
		int col_x = x.NLCol;
		int col_y = y.NLCol;

		int node_indexX, node_indexY;

		while (x.NLStartinBf + x.NLLength > cursor_x && y.NLStartinBf + y.NLLength > cursor_y) {
			node_indexX = buff[col_x][cursor_x];
			node_indexY = buff[col_y][cursor_y];

			if (node_indexY < node_indexX) {
				cursor_y += 1;
				buff[buffCol][buffCurrent++] = node_indexY;
				nlNode.support -= suppDict[node_indexY];
				nlNode.NLLength++;
			} else if (node_indexY > node_indexX) {
				cursor_x += 1;
			} else {
				cursor_x += 1;
				cursor_y += 1;
			}
		}

		while (cursor_y < y.NLStartinBf + y.NLLength) {
			node_indexY = buff[col_y][cursor_y];
			buff[buffCol][buffCurrent++] = node_indexY;
			nlNode.support -= suppDict[node_indexY];
			nlNode.NLLength++;
			cursor_y++;
		}

		if (nlNode.support >= minSupportValue) {
			if (x.support == nlNode.support) {
				sameItems[sameCountRef.count++] = y.label;
			} else {
				nlNode.label = y.label;
				nlNode.firstChild = null;
				nlNode.next = null;

				if (x.firstChild != null) {
					lastChild.next = nlNode;
					lastChild = nlNode;
				} else {
					x.firstChild = nlNode;
					lastChild = nlNode;
				}
			}
			return lastChild;
		} else {
			buffCurrent = nlNode.NLStartinBf;
		}
		return lastChild;
	}

	void traverse(TreenNode curNode, TreenNode curRoot, int level, int sameCount) throws Exception {

		MemoryLogger.getInstance().checkMemory();

		TreenNode sibling = curNode.next;
		TreenNode lastChild = null;

		while (sibling != null) {

			if (level > 1) {
				Counter sameCountTemp = new Counter();
				sameCountTemp.count = sameCount;
				lastChild = iskItemSetFreq2(curNode, sibling, lastChild, sameCountTemp);
				sameCount = sameCountTemp.count;
			} else if (level == 1) {
				Counter sameCountTemp = new Counter();
				sameCountTemp.count = sameCount;
				lastChild = itemSetFreq(curNode, sibling, lastChild, sameCountTemp);
			}

			sibling = sibling.next;
		}
		resultCount += Math.pow(2.0, sameCount);
		nlLenSum += Math.pow(2.0, sameCount) * curNode.NLLength;
		res[resLen++] = curNode.label;
		fileWriter(curNode, sameCount);

		nodeCounter++;

		int from_cursor = buffCurrent;
		int from_col = buffCol;
		int from_size = buffCurrSize;
		TreenNode child = curNode.firstChild;
		TreenNode next = null;

		while (child != null) {
			next = child.next;
			traverse(child, curNode, level + 1, sameCount);

			for (int c = buffCol; c > from_col; c--) {
				buff[c] = null;
			}
			buffCol = from_col;
			buffCurrent = from_cursor;
			buffCurrSize = from_size;
			child = next;
		}
		resLen--;
	}

	
	
	

	static Comparator<ItemClass> comp = new Comparator<ItemClass>() 
	{
		public int compare(ItemClass a, ItemClass b)
		{
			return ((ItemClass) b).num - ((ItemClass) a).num;
		}
	};

}
