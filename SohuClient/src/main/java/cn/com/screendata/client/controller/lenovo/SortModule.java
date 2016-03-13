package cn.com.screendata.client.controller.lenovo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SortModule {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = "D:\\htmlDoc\\backup\\test.txt";
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader reader = null;
		InputStream inputStream = null;
		List<String> list = new ArrayList<String>();
		try {
			inputStream = new FileInputStream(filePath);
			String line;
			reader = new BufferedReader(new InputStreamReader(inputStream));
			line = reader.readLine();
			while (line != null) {
				list.add(line);
				stringBuffer.append(line);
				stringBuffer.append("\n");
				line = reader.readLine();
			}
			for(String str : list){
				System.out.println(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				reader.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
