/* 
	@author Nathan Gilbert, Eric Welborn
	@version 0.5

	This is the first operational draft of the agenda agent class. This class facilitates the creation of the parse tree.
*/

//package edu.depauw.nlp.parser;

import java.util.*;
import java.io.*;
import java.util.regex.*;

class AgendaAgent {

	private class Rule {

		private String lhs;
		private String rhs;

		Rule() {
			lhs = "";
			rhs = "";
		}

		Rule(String str, String r) {
			lhs = str;
			rhs = r;
		}

		public String getLHS() {
			return lhs;
		}

		public void setLHS(String s) {
			lhs = s;
		}

		public void setRHS(String s) {
			rhs = s;
		}

		public String getRHS() {
			return rhs;
		}

		public String toString() {
			return lhs + " -> " +  rhs;
		}
	}

	private FileReader fr;
	private BufferedReader br;
	private ArrayList sentenceNodes; //this infamous sentenceNodes. 
	private ArrayList purgatory;
	private ArrayList rules;
	private NTree finalTree; //the absolute final parse tree. The winner. 
	private String originalText;
	private final String DELIM = "->";
	private Pattern p = Pattern.compile("\\s[a-z]\\s"); 
	private Matcher m;


	AgendaAgent() {
		finalTree = new NTree();
		sentenceNodes = new ArrayList();
		rules = new ArrayList();
	}

	/*
		@param POS This is an array of strings which denotes what part of speech each sentence fragment inside of the
		words array fall under. For example, if words[2] = "large can", then POS[2] = "ADJ N" 
		@param 
	*/
	AgendaAgent(String POSstr, String ot, String punct, boolean and) throws IOException {

		StringTokenizer words = new StringTokenizer(ot);
		StringTokenizer pos = new StringTokenizer(POSstr);

		int index = 0;
		String tmp;
		originalText = ot;
		sentenceNodes = new ArrayList();
		rules = new ArrayList();
		ArrayList fileIN = new ArrayList();

		while(words.hasMoreTokens()) 
			sentenceNodes.add(new NTree(words.nextToken(),pos.nextToken(),index, index++));
		
		fileIN.add("./parser-rules/Rules.txt");

		if(and)
			fileIN.add("./parser-rules/ARules.txt");

		if(punct.equals("?"))
			fileIN.add("./parser-rules/QRules.txt");

	 	for(int i = 0;i<fileIN.size();i++) {

			try {
				fr = new FileReader((String)fileIN.get(i));
				br = new BufferedReader(fr);

				while((tmp = br.readLine()) != null) {
					if(tmp.substring(0,1).equals("#")) continue;

					rules.add(tmp.trim());
				}
			}
			catch(IOException e) {
				System.out.println("I/O problem. #12");
			}
			
			fr.close();
		}
	
		formatRules();
	}

	//this works similar to a destructor in C++.
	public void finalize() throws IOException {
		fr.close();
	}

	//this part hasn't been verified correct yet, watch out for space character issues. 
	private boolean spans(NTree n) {
		if(((String)n.getWord()).equals(originalText))
			return true;
		else
			return false;
	}

	private void formatRules() {

		ArrayList tmp = new ArrayList();
		String str;
		String[] sa;
		StringTokenizer ruleTokens;
	
		for(int i = 0;i < rules.size();i++) {
			str = new String((String)rules.get(i));
			sa = str.split(DELIM);
			tmp.add(new Rule(sa[0].trim(), sa[1].trim()));
		}
		rules = tmp;
	}

	private void removeFromList(ArrayList removeList, ArrayList list) {
		
		int[] ruleArray;

		ruleArray = new int[removeList.size()];

		//this shouldn't be a very big list.
		for(int i = 0;i < removeList.size();i++)
			ruleArray[i] = ((Integer)removeList.get(i)).intValue();

		Arrays.sort(ruleArray);

		//removing items from the end of the arraylist first.
		for(int i = ruleArray.length-1;i >= 0;i--)
			list.remove(ruleArray[i]);
	}

	//This is where the actual links will be built and transfered to PARE. 
	public boolean run() {

		boolean done = false;
		purgatory = new ArrayList(); //this is where the possible matches await their fate. 
		ArrayList removeList;
		int[] ruleArray;
		boolean shouldRemove = true;


		for(int z = 0;z < 2;z++) {

			removeList = new ArrayList();

			//first pass through, should clean up all singleton nodes.
			//the reason I am doing this here instead of below is to be able to remove these rules from the arraylist so 
			//I don't have to worry about replications in the tree array list. 
			for(int i = 0;i < sentenceNodes.size(); i++) {
				for(int j = 0;j < rules.size();j++) {
					if(((NTree)sentenceNodes.get(i)).getPOS().equals(((Rule)rules.get(j)).getRHS())) {
	
						purgatory.add(new NTree(((NTree)sentenceNodes.get(i)).getWord(),((Rule)rules.get(j)).getLHS(), ((NTree)sentenceNodes.get(i)).getStartIndex(),((NTree)sentenceNodes.get(i)).getEndIndex(),((NTree)sentenceNodes.get(i))));
						if (shouldRemove)
							removeList.add(new Integer(j));
					}
				}
			}	

			removeFromList(removeList, rules);
			
			//purging purgatory
			for(int i = 0;i < purgatory.size();i++) 
				sentenceNodes.add(((NTree)purgatory.get(i)));
			
			purgatory.clear();
			shouldRemove = false;
		}

		purgatory = new ArrayList();
		removeList = new ArrayList();

		for(int i=0;i<rules.size();i++) {
			m = p.matcher(((Rule)rules.get(i)).getRHS());
			if(m.find())
				removeList.add(new Integer(i)); 
		}
		
		removeFromList(removeList, rules);
		removeList = new ArrayList();

		//removing all lower case pos from sentenceNodes.
		for(int i =0;i<sentenceNodes.size();i++) {
			m = p.matcher((String)((NTree)sentenceNodes.get(i)).getPOS());
			if(m.find())
				removeList.add(new Integer(i));		
		}

		removeFromList(removeList, sentenceNodes);
		
		boolean secondPass = false;

		while(true) {

			if(check()) {
				System.out.println("\n\t\tParse tree done.\n");
				return true;
			}
			
			if (!matchIndexes())
				return false;


		}
	}

	//checks the beginning and ending end index of two tokens to see if they could possible make match (because they are beside each
	//other.)
	private boolean matchIndexes() {
		
		StringBuffer posBuffer = new StringBuffer();
		StringBuffer wordBuffer = new StringBuffer();
		boolean addedNode = false;
		boolean needToSet = true;;
		int current = 0;
		int tmpStart;	
		int tmpLast;

		while (current < sentenceNodes.size()) {

			tmpStart = ((NTree)sentenceNodes.get(current)).getStartIndex();	
		
			for (int j = 0; j < sentenceNodes.size(); j++) {
			
				if(current == j)
					continue;
				
				tmpLast = ((NTree)sentenceNodes.get(j)).getEndIndex();
				
				//check for a rule
				if (((NTree)sentenceNodes.get(current)).getEndIndex() == ((NTree)sentenceNodes.get(j)).getStartIndex()-1) {
				
					posBuffer.append(((String)((NTree)sentenceNodes.get(current)).getPOS()));	
					wordBuffer.append((String)((NTree)sentenceNodes.get(current)).getWord());
					posBuffer.append(" ").append(((String)((NTree)sentenceNodes.get(j)).getPOS()));
					wordBuffer.append(" ").append((String)((NTree)sentenceNodes.get(j)).getWord());
					
					if (doStuff(posBuffer, wordBuffer, current, j, tmpStart, tmpLast, false) && needToSet) { //use 0, 0 because are already logically adjacent
					
						addedNode = true;
						needToSet = false;
					}
				}
				
				posBuffer.delete(0, posBuffer.length());
				wordBuffer.delete(0, wordBuffer.length());
			}

			current++;
			
			for(int i = 0;i < purgatory.size();i++) 
				sentenceNodes.add(new NTree(((NTree)purgatory.get(i))));
			
			purgatory.clear();
		}

		return addedNode;
	}

	private boolean doStuff(StringBuffer posBuffer, StringBuffer wordBuffer, int node1, int node2, int tmpStart, int tmpLast, boolean addedNode) {

		for(int k = 0;k<rules.size();k++) {

			if(((Rule)rules.get(k)).getRHS().trim().contentEquals(posBuffer) && !found(wordBuffer.toString().trim(),
			((Rule)rules.get(k)).getLHS().trim(), tmpStart)) {
			
				addedNode = true;

				purgatory.add(new NTree(wordBuffer.toString().trim(), ((Rule)rules.get(k)).getLHS().trim(), tmpStart, tmpLast));
				
				((NTree)purgatory.get(purgatory.size()-1)).addChild((NTree)sentenceNodes.get(node1));
				((NTree)purgatory.get(purgatory.size()-1)).addChild((NTree)sentenceNodes.get(node2));
			}
		}
		return addedNode;
	}

	//checks for the 'S' 
	private boolean check() {

		//this is checking to see if we have an S yet. 
		for(int i = 0;i < sentenceNodes.size();i++)
			if((((NTree)sentenceNodes.get(i)).getPOS().equals("S")) && spans(((NTree)sentenceNodes.get(i)))) {
			 	finalTree = ((NTree)sentenceNodes.get(i));
				return true;
			}
		
		return false;		
	}

	private boolean found(String word, String lhs, int start) {

		NTree tmp;
		boolean foundInNodes = false;
		boolean foundInPurgatory = false;

		for(int i = 0;i<sentenceNodes.size();i++) {
			tmp = ((NTree)sentenceNodes.get(i));
			if(tmp.getWord().equals(word) && tmp.getPOS().equals(lhs))
				foundInNodes = true;
		}

		for(int i = 0;i<purgatory.size();i++) {
			tmp = ((NTree)purgatory.get(i));
			if(tmp.getWord().equals(word) && tmp.getPOS().equals(lhs))
				foundInPurgatory = true;
		}
		
		if(foundInNodes || foundInPurgatory)
			return true; //send to L.
		else
			return false;
	}

	private void printSentenceNodes() {
		
		System.out.println("----------BEGIN-------------------");

		System.out.println("\nSize: " + sentenceNodes.size() + "\n");

		for(int i =0;i<sentenceNodes.size();i++)
			System.out.println(((NTree)sentenceNodes.get(i)) + "\n+++++++++++++++++++++++++++++");

		System.out.println("----------END---------------------");
	}

	public String outPutLinks(int s) {

		Formatter fm = new Formatter(finalTree,s+1);
		
		//System.out.println("Result in PARE Format: ");
		//System.out.println(fm.formatLinksPARE()); 
		return fm.formatTreePARE();	
	}
/*
	NTree getFinalTree() {
		return finalTree;
	}
*/
}
		
