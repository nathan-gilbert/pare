
import java.io.*;
import java.util.*;

class AutoTester {

	private int correct;
	private int total;
	private String outFile;
	private FileWriter fw;

	AutoTester() throws IOException {
		String postfix;

		Calendar cal = new GregorianCalendar();
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);

		postfix = "." + hour + ":" + minute + ":" + second +".html";
		outFile = "results.log" + postfix;
		total = 0;
		correct = 0;
		
		try {
			fw = new FileWriter(outFile, true);
		}
		catch(IOException e) {
			System.out.println("Couldn't open outfile. ");
		}

		fw.write(header());
		fw.write("<h4><i>Test done at: " + hour + ":" + minute + ":" + second + " ");
		fw.write(month + "\\" + day + "\\" + year + "</h4></i><br /><br /><hr>");
		fw.flush();
		
	}

	private String header() {
		return "<html><head>\n<title>Parser Results.</title>\n</head>\n<body>";
	}

	private String footer() {
		return "</body></html>";
	}

	public void log(String s, String pos, String tree, boolean result) throws IOException {
	
		StringBuffer tmp = new StringBuffer();

		total++;	

		if(result) {
			correct++;
			tmp.append("<br /><h3>");
			tmp.append(s + " --- <font color=\"#00FF00\">CORRECT</font><br />\n\n").append(pos).append("\n<br /><br />\n" + tree + "<br />");
			tmp.append("</h3><br />");
		}
		else {
			tmp.append("<br /><h3>");
			tmp.append(s + " --- <font color=\"#FF0000\"> UNGRAMMATICAL</font><br />\n\n").append(pos + "\n\n");
			tmp.append("</h3><br />");
		}

		fw.write(tmp.toString());
		fw.write("<hr>");
		fw.flush();
	}
	
	public void finalize() throws IOException {
		float tally;

		tally = (((float)correct)/((float)(total))) * 100; 
		System.out.println("\nCompletely done.\n");
		fw.write("\n\n\n<br /><br /><h2><b>The parser perused " + total + " sentences, and all I got was " + tally + "% correct. (" + correct + ")</b></h2>\n");
		if(tally > 50.0)
			fw.write("<br /><br /><font color=\"#3355FF\"><h2>Not Bad.</h2></font>");
		fw.write(footer());
		fw.close();
	}
}

