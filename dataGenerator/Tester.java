package dataGenerator;

import java.security.MessageDigest;

public class Tester{

	public static void main(String[] args) {

		int totalSteps = 100;
		try {
			Long l = new Long(System.currentTimeMillis());
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(l.byteValue());
			String id = new String(byteArrayToHexString(md.digest()));
			
			
			OutputToJSON testOut = new OutputToJSON("tewsttt",id);

			
			for (int i = 0; i < totalSteps; i++) {
				testOut.newStep();
				//Add System Info
				testOut.dumpSystem("TEST SYSTEM", id,i,100,0,12);
				//Add Environment Info
				testOut.dumpEnvironments();
				//Add Group Info

				//Add Agents Info

				//Finalise
				testOut.finished();
				//Print
				// System.out.println(testOut.printToString());
			}

		} catch (Exception e){
			e.printStackTrace();
		}

	
	}

	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result +=
	      	Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
}