package cn.com.screendata.client.controller.lenovo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MergeFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String fileString = "";
		File file = new File("D:\\htmlDoc\\backup");
		if(file.exists()){
			String[] fileStrings = file.list();
			for (String string : fileStrings) {
//				System.out.println(string);
				String filePath = "D:\\htmlDoc\\properties\\" + string;
				fileString = fileString + LenovoCrawler.fileToString(filePath);
			}
		}
		
		File newFile = new File("D:\\htmlDoc\\allType.properties");
		if(!newFile.exists()){
			newFile.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(newFile);
		fileOutputStream.write(fileString.getBytes());
		fileOutputStream.close();
		System.out.println("Done!");
	}

}
