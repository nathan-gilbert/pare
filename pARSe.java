/* 
	pARSe.java 
	@author Eric Welborn, Nathan Gilbert
	@version 0.4.3
	@param This program assumes that you have a sentence (or paragraphs) that you want to Parse. 
	@return Returns a parse tree of your text. 
*/

//package edu.depauw.nlp.parser;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import edu.stanford.nlp.tagger.maxent.*;

class Parser {

	private static FileReader fr;
	private static ArrayList allSentences = new ArrayList(); //all the sentences from text, in Penn Treebank format, ready for more work to be done to them.

	//alot of the regex are for getting the pos input in Penn Treebank format. 
	private static Pattern p = Pattern.compile("\\.[\\s\\n]|\\?[\\s\\n]|\\![\\s\\n]|\\!+$|\\.+$|\\?+$", Pattern.CASE_INSENSITIVE); //a few more punctuations are needed. 
	private static Pattern p2 = Pattern.compile("[\\?\\.\\!]\"|\"|Mr\\.|Dr\\.|Ms\\.|Mrs\\.|PhD\\.|BSc\\.|M\\.D\\.", Pattern.CASE_INSENSITIVE); //there are going to be a lot of these. 
	
	//This expression helps keep unwanted line breaks from happening. 	
	private static Pattern p3 = Pattern.compile("_"); 

	//Some Penn Treebank specifications. Obviously more to come later. 
//	private static Pattern p4 = Pattern.compile("\\'s|\\'m|\\'|n\\'t", Pattern.CASE_INSENSITIVE);
	private static Pattern p5 = Pattern.compile("\\*"); //helps in capturing punctuation.
	private static Pattern p6 = Pattern.compile("\""); //double quotes
	private static Pattern p8 = Pattern.compile("\\sand\\s", Pattern.CASE_INSENSITIVE);
	private static Matcher m;
	private static Matcher m2;
	private static boolean and;
	private static AutoTester tester;

	//this will probably be changed to 'run' when we integrate this into PARE.
	public static void main(String args[]) throws IOException {
	
		String text = ""; // a temp variable to hold stuff coming out of files. 
		String[] POS; //an array to hold parts of speech from the sentence.
		int wordCount; //The number of words in the original text.
		StringBuffer strbuf = new StringBuffer();
		StringBuffer POSbuf = new StringBuffer();
		BufferedReader br;
		String punct, tmp;
		AgendaAgent aa;
		tester = new AutoTester();

		if(args.length > 0) {
			try {
					fr = new FileReader(args[0]);
			}
			catch(IOException exc) {
				System.out.println("Something went wrong. Unable to read in test file.");
			}
		}
		else {

			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("What is the name of the file: ");
			String file;
		    file = br.readLine();

			try {
					fr = new FileReader(file);
			}
			catch(IOException exc) {
				System.out.println("Something went wrong. Unable to read in test file. #2"); 
			}
		}

		br = new BufferedReader(fr);

		while((text = br.readLine()) != null) {
			if(text.length() <= 2)
				continue;

			if(!(text.substring(text.length()-2).equals(".\n"))) 
				text = text.trim() + " ";

			strbuf.append(text);
		}

		prepareText(strbuf.toString()); //fills the ArrayList allSentences. Also puts the text in a format the POS tagger likes. 
	
		for(int n = 0;n < allSentences.size(); n++) {

			tmp = allSentences.get(n).toString().trim();
			
			if(tmp.length() > 2)
				punct = tmp.substring(tmp.length()-1);
			else
				continue;
			
			if((n % 10) == 0)
				System.out.println("\t\t\t\t" + (((float)n/(float)allSentences.size())*100) + "% done. ");
			
			System.out.println("\nWorking on sentence " + n);	

			POS = getPOS(tmp);
			for(int z = 0;z < POS.length;z++) 
				POSbuf.append(POS[z]).append(" ");

			System.out.println("\n\tDone with tagging.");
			aa = new AgendaAgent(POSbuf.toString().trim(), tmp, punct, and);
		
			//where the magic happens. 
			if (aa.run())
				tester.log(tmp,POSbuf.toString().trim(),((String)aa.outPutLinks(n)), true);
			else 
				tester.log(tmp, POSbuf.toString().trim(), "", false);
		
			POSbuf.delete(0,POSbuf.length());
		}//for

		tester.finalize();
		fr.close();
	}//main
	
	private static String[] getPOS(String sentence) {

		String taggedSentence = new String();
		MaxentTagger mt = new MaxentTagger("./left3words/wsj3t0-18.holder");
		taggedSentence = mt.tagString(sentence);

		String[] tmp = taggedSentence.split(" ");
		int numPOS = tmp.length;
		String[] POS = new String[numPOS];

		for (int i = 0; i < numPOS; i++)
			POS[i] = ((String)tmp[i]).substring(((String)tmp[i]).lastIndexOf('/')+1, tmp[i].length()).trim().toLowerCase(); //trim may be excessive

		return POS;
	}
	
	private static void prepareText(String s) {

		boolean paren = false;

		StringBuffer catcher = new StringBuffer();
		m = p2.matcher(s);
	
		//this is used to keep words like "mr." from breaking to a new line.
		while(m.find()) 
			m.appendReplacement(catcher, (s.substring(m.start(),m.end())) + "_");
	
		m.appendTail(catcher);

		s = catcher.toString();
		catcher.delete(0,catcher.length());

		m = p.matcher(s);

		//this is used to capture punction, so it isn't deleted when the string is split.	
		while(m.find())
			m.appendReplacement(catcher, (s.substring(m.start(),m.end())) + "* ");

		m.appendTail(catcher);
		s = catcher.toString();
		
		m = p6.matcher(s);
		catcher.delete(0,catcher.length());

		//looking for ands.
		m = p8.matcher(s);
		and = false;

		if(m.find())
			and = true;
				
		//splits the string 'catcher' into an array of senteneces. 
		String[] Temp = p5.split(s.trim());	

		m.reset();

		for(int z = 0;z < Temp.length; z++) {

			catcher.delete(0,catcher.length());
			m = p3.matcher(Temp[z]);
			
			while(m.find())
				m.appendReplacement(catcher, "");
	
			m.appendTail(catcher);

			//putting this new sentence onto the array list of all current sentences.
			allSentences.add(catcher.toString());
		}
	}
}

