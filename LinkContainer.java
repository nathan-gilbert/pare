// 6/13/2005
// @author Eric, Nathan
// @modified 6/16/2005 by nathan
//package edu.depauw.nlp.parser;
import java.util.ArrayList;
import java.io.*;

class Link_t
{
	private String POS;   // part of speech
	private String word;   // the word
	private int linkBegin; // initial array/word index of link
	private int linkEnd;   // concluding array/word index of link
	private int sentenceNum;

	Link_t ()
	{ 
	}

	Link_t(String p, String w, int b, int e, int s) {
	
		POS = p;
		word = w;
		linkBegin = b;
		linkEnd = e;
		sentenceNum = s;
	}

	public String getPOS ()
	{ return POS; }
	
	public void setPOS (String partOfSpeech)
	{ POS = new String(partOfSpeech); }

	public String getWord ()
	{ return word; }

	public void setWord (String w)
	{ word = new String(w); }

	public int getLinkBegin ()
	{ return linkBegin; }
	
	public void setLinkBegin (int begin)
	{ linkBegin = begin; }

	public int getLinkEnd ()
	{ return linkEnd; }

	public int getSentence() {
		return sentenceNum;
	}

	public void setLinkEnd (int end)
	{ linkEnd = end; }

}//class Link_t
	
class LinkContainer
{
	private ArrayList al;

	//constructor
	LinkContainer() {
		al = new ArrayList();
	}

	public void addLink (Link_t Link) 
	{ al.add(Link); }
	
	public void addLink (int index, Link_t Link)
	{ al.add(index, Link); }
	
	public void removeLink (Link_t Link)
	{ al.remove(al.indexOf(Link)); }

	//Removes the last item in the link container.
	public void removeLastLink ()
	{ al.remove(al.size() - 1); }

	public Link_t getLink(int index)
	{ return (Link_t)al.get(index); }

	public int getSize()
	{ return al.size(); }
	
	//This overloads Object.toString() 
	public String toString() {
			StringBuffer sb = new StringBuffer();
			Link_t tmpLink;
			for(int i=0;i<al.size();i++) {
				tmpLink = ((Link_t)al.get(i));
				sb.append(tmpLink.getPOS().toString()).append(" ").append(tmpLink.getLinkBegin()).append(" ").append(tmpLink.getLinkEnd()).append(" ").append(tmpLink.getSentence()).append("\n");
			}	
			return sb.toString();
	}

}//class LinkContainer

