package stdSimLib;

public class CASObject {
	private String objectName = "";
	public CASObject(String objName) { 
		objectName = objName;
	}
	
	public void print(String str){
		System.out.println(str);
	}
	
	public void errLog(Object o) {
		System.err.println(objectName+" Warning: "+o.toString());
	}

}
