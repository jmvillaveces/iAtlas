package mpg.biochem.de.interbase.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import psidev.psi.mi.tab.model.BinaryInteraction;

public class Util {
	
	/*public static String getInteractionIdPair(BinaryInteraction interaction){
		
		String intAId="";
		if(interaction.getInteractorA() != null){
			intAId = interaction.getInteractorA().getIdentifiers().get(0).getIdentifier();
		}
		
		String intBId="";
		if(interaction.getInteractorB() != null){
			intBId = interaction.getInteractorB().getIdentifiers().get(0).getIdentifier();
		}
		
		if(intAId.equals("")){
			intAId = intBId;
		}else if(intBId.equals("")){
			intBId = intAId;
		}
		
		int c = intAId.compareToIgnoreCase(intBId);
		if(c > 0)
			return intBId+"_"+intAId;
			
		return intAId+"_"+intBId;
	}*/
	
	public static String[] merge(Object a, Object b){
		
		String[] arrA = new String[0]; 
		if(a instanceof String){
			arrA = new String[]{a.toString()};
		}else if(a instanceof String[]){
			arrA = (String[]) a;
		}
		
		String[] arrB = new String[0]; 
		if(b instanceof String){
			arrB = new String[]{b.toString()};
		}else if(b instanceof String[]){
			arrB = (String[]) b;
		}
		
		//convert arrays to collections (lists)
	    Collection<String> coll1 = Arrays.asList(arrA);
	    Collection<String> coll2 = Arrays.asList(arrB);
	    
	    //Create a SortedSet from the first collection
	    SortedSet<String> sorter = new TreeSet<String>(coll1);

	    //Add the second collection
	    sorter.addAll(coll2);
	    
	    return sorter.toArray(new String[0]);
	}
	
	public static String getSimpleDate(Date date){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd<HH:mm:ss.SSS>");
		return df.format(date);
	}
	
	public static void delete(File file) throws IOException{
		 
		if(file.isDirectory()){
			
			//directory is empty, then delete it
		    if(file.list().length==0){
		    	file.delete();
		    }else{
		    	//list all the directory contents
		        String files[] = file.list();
		 
		        for (String temp : files) {
		        	//construct the file structure
		        	File fileDelete = new File(file, temp);
		 
		        	//recursive delete
		        	delete(fileDelete);
		        }
		 
		        //check the directory again, if empty then delete it
		        if(file.list().length==0){
		        	file.delete();
		        }
		    }
		 }else{
			 //if file, then delete it
		    file.delete();
		 }
	}
	
	public static void executeCommand(String comand) throws IOException, InterruptedException{
		Process p;
		
		try {
			p = Runtime.getRuntime().exec(comand);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
 
			StringBuffer sb = new StringBuffer();
 
			String line = reader.readLine();
			sb.append(line);
			while (line != null) {
				line = reader.readLine();
				sb.append(line);
				System.out.println(line);
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
