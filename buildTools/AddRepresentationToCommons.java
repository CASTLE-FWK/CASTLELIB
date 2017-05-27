package buildTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Takes a Representation class file and outputs the 
 * code for it to work inside Commons.casl
 * @author lachlan
 *
 */
public class AddRepresentationToCommons {
	
	private static String output = "";
	private static String name = "";
	public static void main(String[] args) throws Exception {
		String thePath = args[0];
		System.out.println(thePath);
		name = thePath.split("\\.")[0];
		String[] s = name.split("/");
		name = s[s.length - 1];
		output = "obj "+name+": {\n";
		
		
		
        File file = new File(thePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while((str = br.readLine()) != null){
        	str = str.trim();
            if (str.contains("public class "+name)) {
            	//ignore
            } else if (str.contains("public "+name)){
            	//ignore
            } else if (str.contains("public") && !containsType(str.split(" ")[1]).equalsIgnoreCase("NULL")){
            	str.trim();
            	//Here be a function
            	output += "\tdef ";
            	//Get the Type
            	String theType = containsType(str.split(" ")[1]);

            	//Get the name
            	String functionName = str.split(" ")[2].split("\\(")[0];
            	output += functionName+"(";
            	
            	//Get the parameters
            	String params = str.split("\\(")[1].split("\\)")[0];
            	String[] paramsSplit = params.split("\\,");
            	if (paramsSplit.length > 1){
            		
            	
	            	for (String param : paramsSplit){
	            		param = param.trim();
	            		String paramType = param.split(" ")[0];
	            		String paramName = param.split(" ")[1];
	            		output += "var "+ paramType+":"+paramName+", ";
	            	}
	            	//Lazy remove comma at end of line
	            	output = output.substring(0, output.length()-2);	            
            	}
            	output +=")(";
            	if (!theType.equalsIgnoreCase("void")){
            		output += "var "+theType + ":theReturn";
            	}
            	output += "): {};\n";
            	
            } else {
//            	System.out.println(str);
            }
        }
        output += "};\n";
        System.out.println(output);
        
        br.close();
		
	}
	
	
	public static String containsType (String s){
		String newS = s.trim();
		
		if (newS.equalsIgnoreCase("void") ||
				newS.equalsIgnoreCase("boolean") ||
				newS.equalsIgnoreCase("int")|| 
				newS.equalsIgnoreCase("String") ||
				newS.equalsIgnoreCase("float") ||
				newS.equalsIgnoreCase("double") ||
				newS.equalsIgnoreCase("Vector2") ||
				newS.matches("List<.*>") ||
				newS.equalsIgnoreCase("Entity") ) {
			return newS;
		}
		else {
			return "NULL";
		}
	}

}
