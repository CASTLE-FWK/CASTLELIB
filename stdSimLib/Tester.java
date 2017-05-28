package stdSimLib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import stdSimLib.utilities.RandomGen;
import stdSimLib.utilities.Utilities;

public class Tester {

	
	static MetaModel testMM;	
	
	public static void main(String[] args){
//		File file = new File(args[0]);
//		FileReader fr;
//		try {
//			fr = new FileReader(file);
//			BufferedReader br = new BufferedReader(fr);
//			String currentLine = "";
//			String fileString = "";
//			while ((currentLine = br.readLine()) != null){
//				fileString += currentLine + "\n";
//			}
//			br.close();
//			
//			testMM = new MetaModel();
//			testMM.parseMetaModel(fileString);
//			System.out.println(testMM.getAgents().size());
//			for (Agent agent : testMM.getAgents()){
//				System.out.println(agent.toString());
//				System.out.println(agent.publishAgentStates("\t", "",""));
//			}
//			System.out.println(testMM.getInteractions().size());
//			
//			
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		int dim = 150;
		
		boolean[][] dummy = new boolean[dim][dim];
		for (int i = 0; i < dim; i++){
			for (int j = 0; j < dim; j++){
				dummy[i][j] = RandomGen.generateCoinFlip();
			}
		}
		
		for (int i = 0; i < dim; i++){
			for (int j = 0; j < dim; j++){
				if (dummy[i][j]){
					System.out.print('0');
				} else {
					System.out.print('.');
				}
			}
			System.out.print('\n');
		}
		
		
		
		
	}
}
