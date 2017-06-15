package parser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Test {

	public static void main(String[] /**/ args) {

		/*
		 * Parser parser = new Parser(
		 * "E:\\java\\playground\\workspace\\GWT\\parser\\src\\test\\test2.cpp")
		 * ; parser.parse();
		 * 
		 * System.out.println("Test Result1: ");
		 * System.out.println(parser.getFucntions());
		 */

		String directory = "D:\\workdir\\NX12IP2\\src\\modl\\no\\ind";

		File dir = new File(directory);
		File[] directoryListing = dir.listFiles();

		long startTime = System.nanoTime();
		int countOfFiles =0;
		
		String outputfolder = "c:\\haystack\\temp\\";
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isFile()) {

					String fname = child.getName();
					if (fname.contains(".c") || fname.contains(".cxx")) {
						countOfFiles++;
						Parser p = new Parser(child.getPath());
						System.out.println(countOfFiles+"th file: "+ child.getPath());
						p.parse();
						
						try{
						    PrintWriter writer = new PrintWriter(outputfolder+fname);
						    writer.print(p.getFucntions());
						    writer.close();
						} catch (IOException e) {
						   System.err.println("Error when output the result");
						}
						
					}
				}
			}
		}
		long endTime = System.nanoTime();
		System.out.println("Total time to parse "+ countOfFiles + "files: "+ (endTime-startTime+500000000)/1000000000);


		//D:\workdir\NX12IP2\src\modl\no\ind\ModlFeatureJA_WaveLinkBuilder.cxx

		
//		 Parser parser2 = new Parser("D:\\workdir\\Personal\\ProjectAndTool\\CppParser\\src\\parser\\test\\test.cpp");
//		 parser2.parse();
//		 System.out.println(parser2.getFucntions());
	}

}
