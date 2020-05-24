/*
	@author Nathan Gilbert, Eric Welborn
	@version 1.0
*/

//package edu.depauw.nlp.parser;

import java.util.Stack;

class Formatter {

	private LinkContainer lc;
	private NTree nt;
	private int currentSentence;
	
	Formatter() {
		lc = null;
	}
	
	Formatter(NTree t, int s) {
		
		lc = new LinkContainer();
		nt = t;
		currentSentence = s;
	}
		
	public String formatLinksPARE() {
		makeLinks();
		return lc.toString();			
	}

	//this is necessary to prime molePeople to create all the links. 
	private void makeLinks() {

		lc.addLink(new Link_t(nt.getPOS(),nt.getWord(),nt.getStartIndex(),nt.getEndIndex(),currentSentence));
		
		for(int i =0;i < nt.getChildren().size();i++) 
			molePeople(nt.getChild(i));	
		
	}

	//a recursive function to establish links 
	private void molePeople(NTree t) {

		if(t.hasChildren()) {
			lc.addLink(new Link_t(t.getPOS(),t.getWord(),t.getStartIndex(),t.getEndIndex(), currentSentence));

			for(int i = 0;i < t.getChildren().size();i++) 
				molePeople(t.getChild(i));
		}
		else
			return;
	}
	
	public String formatTreePARE() {
	
		Stack treeStack = new Stack();	
		StringBuffer sb = new StringBuffer();
		NTree tmp; 

		treeStack.push(nt);
							
		while(!treeStack.empty()) {

			tmp = (NTree)treeStack.pop();

			if(tmp.hasChildren()) {

				sb.append("(").append(tmp.getPOS()).append(" ");

				treeStack.push(new NTree(") ","",-1,-1));

				for(int i = tmp.getChildren().size()-1; i>=0 ; i--)
					treeStack.push(tmp.getChild(i));
			}
			else if(tmp.getWord().equals(") ")) 
				sb.append(") ");
			else 
				sb.append(tmp.getWord() + " ");
		}		
		
		return sb.toString()+"\n";	
	}	

}
