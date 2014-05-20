package uk.ac.kcl.dcs.agentpref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class agentprefParser {

	public static void main(String[] args) {
		
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			File fi = new File("/Users/Martin/Dropbox/University/PhD/2013/19. 6th May - 10th May/ActionsValues/Results/simulationfalseThu May 23.csv");
			
			Scanner sc = new Scanner(fi);
			
			double DatabaseServerTrue = 0;
			double DatabaseServerFalse = 0;
			double MailServerTrue = 0;
			double MailServerFalse = 0;
			double WebServerTrue = 0;
			double WebServerFalse = 0;
			
			int lines = 0;
			
			Hashtable<String, Double> attackPercentages = new Hashtable<String, Double>();
			
			while (sc.hasNextLine()) {
				
				sCurrentLine = sc.nextLine();
				lines++;
				
				if (sCurrentLine.toLowerCase().contains("database server") && sCurrentLine.toLowerCase().contains("true")) {
					
					DatabaseServerTrue++;
					
				} else if (sCurrentLine.toLowerCase().contains("mail server") && sCurrentLine.toLowerCase().contains("true")) {	
					
					MailServerTrue++;
					
				} else if (sCurrentLine.toLowerCase().contains("web server") && sCurrentLine.toLowerCase().contains("true")) {
					
					WebServerTrue++;
					
				} else if (sCurrentLine.toLowerCase().contains("database server") && sCurrentLine.toLowerCase().contains("false")) {
				
					DatabaseServerFalse++;
					
				} else if (sCurrentLine.toLowerCase().contains("mail server") && sCurrentLine.toLowerCase().contains("false")) {	
					
					MailServerFalse++;
				
				} else if (sCurrentLine.toLowerCase().contains("web server") && sCurrentLine.toLowerCase().contains("false")) {
					
					WebServerFalse++;
					
				}
				
				if (sCurrentLine.toLowerCase().contains("*(")) {
					
					if (attackPercentages.containsKey(sCurrentLine.substring(sCurrentLine.indexOf("*("), sCurrentLine.indexOf(").*")))) {
						
						attackPercentages.put(sCurrentLine.substring(sCurrentLine.indexOf("*("), sCurrentLine.indexOf(").*")),
										  attackPercentages.get(sCurrentLine.substring(sCurrentLine.indexOf("*("), sCurrentLine.indexOf(").*"))) + 1);
					
					} else {
						
						attackPercentages.put(sCurrentLine.substring(sCurrentLine.indexOf("*("), sCurrentLine.indexOf(").*")), 1.0);
						
					}
					
				}
				
			}
			
			System.out.println("Database Server uptime (%): " + (DatabaseServerTrue / (DatabaseServerTrue + DatabaseServerFalse)) * 100 );
			System.out.println("Mail Server uptime (%): " + (MailServerTrue / (MailServerTrue + MailServerFalse)) * 100 );
			System.out.println("Web Server uptime (%): " + (WebServerTrue / (WebServerTrue + WebServerFalse)) * 100 );
			
			for ( Map.Entry<String, Double> entry : attackPercentages.entrySet() ) {
				
				attackPercentages.put(entry.getKey(), entry.getValue() / lines * 100);
				
			}
			
			System.out.println(attackPercentages);
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
}
