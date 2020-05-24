/*
	Tree data structure for parser.
	@author Nathan
	@modified 6/14/2005 by Eric
	@modified 6/15/2005 by Nathan	
	@modified 6/17/2005 complete overhaul by Nathan. I shouldn't have designed it like that in the first place. :(	
*/

//package edu.depauw.nlp.parser;
import java.util.ArrayList;

class NTree {
	
	//What this tree does.
	private String word;
	private String POS;
	private int start;
	private int end;
	private ArrayList children; 

	//Constructors
	NTree() {
		word = null;
		POS = null;
		start = -1;
		end = -1;
		children = new ArrayList(0); 
	}

	NTree(String w, String p, int s,int e) {
		word = w;
		POS = p;
		start = s;
		end = e;
		children = new ArrayList(0);
	}
	
	NTree(String w, String p, int s, int e,  NTree kid) {
		word = w;
		POS = p;
		start = s;
		end = e;

		if(children == null)
			children = new ArrayList(0);

		children.add(kid);
	}

	NTree(NTree n) {
		word = n.getWord();
		POS = n.getPOS();
		start = n.getStartIndex();
		end = n.getEndIndex();
		children = n.getChildren();
	}

	//methods
	public void copy(NTree n) {
		word = n.getWord();
		start = n.getStartIndex();
		end = n.getEndIndex();
		POS = n.getPOS();
		children = n.getChildren();
	}

	public String getWord() {
		return word;
	}

	public String getPOS() {
		return POS;
	}

	public int getStartIndex() {
		return start;
	}

	public int getEndIndex() {
		return end;
	}

	private String getIndex() {
		return start + " " + end;
	}

	public void setPOS(String p) {
		POS = p;
	}
	
	public void setWord(String w) {
		word = w;
	}

	public void setStartIndex(int i) {
		start = i;
	}

	public void setEndIndex(int i) {
		end = i;
	}

	public void setIndice(int s,int e) {
		start = s;
		end = e;
	}

	public NTree getChild(int i) {
		return (NTree)children.get(i);
	}

	public ArrayList getChildren() {
		return (ArrayList)children;
	}

	public void removeChild(int index) {
		children.remove(index);
	}

	public void addChild(NTree n) {
		children.add(n);	
	}

	public int numChild() {
		return children.size();
	}

	/*
		@return true if the tree has at least one child, otherwise false.	
	*/
	public boolean hasChildren() {
		if(children.size() > 0)
			return true;
		else
			return false;
	}

	private String printChildren(String tab) {
		tab = tab + "\t";

		StringBuffer childBuffer = new StringBuffer();

		if(hasChildren()) {

			childBuffer.append(tab + "->").append("\n");

			for(int i = 0;i < children.size();i++) {
				childBuffer.append(tab).append("Word: ").append(((NTree)children.get(i)).getWord()).append("\n");
				childBuffer.append(tab).append("POS: ").append(((NTree)children.get(i)).getPOS()).append("\n");
				childBuffer.append(tab).append("index: ").append(((NTree)children.get(i)).getIndex()).append("\n");
				childBuffer.append(((NTree)children.get(i)).printChildren(tab)).append("\n");
			}
		}
		return childBuffer.toString();
	}

	public String toString() {
		return new String("Word: " + word + "\nPOS: " + POS + "\nStart index: " + start+ "\nEnd index: " + end + "\n" + printChildren("")); 
	}
}

