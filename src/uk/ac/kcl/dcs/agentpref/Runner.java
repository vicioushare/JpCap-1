package uk.ac.kcl.dcs.agentpref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runner {
	
	public static void main(String args[]) throws IOException{
	     for(int i = 0; i < 100; i++){
	    	  System.out.println("Run: " + (i + 1));
	          Process proc = Runtime.getRuntime().exec("java -classpath /Users/Martin/Downloads/owlapi-distribution-3.4.3-bin.jar:/Users/Martin/Downloads/HermiT/HermiT.jar:bin uk.ac.kcl.dcs.agentpref.Controller");
	          BufferedReader in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));  
	          String line = null;  
	          while ((line = in.readLine()) != null) {  
	        	  System.out.println(line);  
	          }  
	          try{
	               proc.waitFor();
	          } catch (InterruptedException e) { System.out.println(e); }
	     }
	}
	
}
