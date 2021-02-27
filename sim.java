import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class sim {

	public static void main(String[] args) {
		
		//input to scheduler
		int S = Integer.parseInt(args[0]);
		int N = Integer.parseInt(args[1]);
		Path traceFile= Paths.get(args[2]);
		
		//load instructions from trace file  to a list 
		List<String> instructionList = readTraceFile(traceFile.toString());
		
		//instantiate Pipeline
		Pipeline pipe = new Pipeline(S, N, getStackFromList(instructionList));
		
		//Start pipeline
		pipe.Process();
		
		
		//pipe.printOutput();
		
		

	}
	
	public static List<String> readTraceFile(String filename) {
		
		List<String> traceList = new ArrayList<String>(); 
		
		try {
		      File traceFile = new File(filename);
		      Scanner scReader = new Scanner(traceFile);
		      while (scReader.hasNextLine()) {
		    	  traceList.add(scReader.nextLine());
		      }
		      scReader.close();
		    } 
		catch (FileNotFoundException e) {
		      System.out.println("An error occurred while reading trace file");
		      System.exit(0);
		    }
		
		return traceList;
	}

	public static Stack<String> getStackFromList(List<String> InstList) {
		
		Stack<String> instStack = new Stack<String>();
		
		for (int i = InstList.size()-1; i >=0  ; i--) {
			
			instStack.push(InstList.get(i));
		}
		
		return instStack;
	}
}
