package cn.com.screendata.client.controller.lenovo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GetNeedMessage {
	
	public final static String FILE_NAME = "contract";
	public final static String MODEL_CURRENT = "ContractModel";
	public final static String MODEL_INHERIT = "ItemModel";
	public final static String SHEET_NAME = FILE_NAME;
	
	public static final String NAME = SHEET_NAME + "Name";
	public static final String DESC = SHEET_NAME + "Desc";
	
	public static final String NAMEPATH = "D:\\htmlDoc\\nameDesc\\" + NAME + ".txt";
	public static final String DESCPATH = "D:\\htmlDoc\\nameDesc\\" + DESC  + ".txt";
	
	public static final String MODEL0_NAME_HIGH = "Item";
	public static final String MODEL0_NAME_LOW  = "item";
	public static final String MODEL1_NAME_HIGH = "Contract";
	public static final String MODEL1_NAME_LOW  = "contract";
//	public static final String MODEL2_NAME_HIGH = "Type";
//	public static final String MODEL2_NAME_LOW  = "type";
//	public static final String MODEL3_NAME_HIGH = "ComposedType";
//	public static final String MODEL3_NAME_LOW  = "composedtype";
//	public static final String MODEL4_NAME_HIGH = "VariantType";
//	public static final String MODEL4_NAME_LOW  = "varianttype";
//	public static final String MODEL5_NAME_HIGH = "NemoSubSeriesProduct";
//	public static final String MODEL5_NAME_LOW  = "nemosubseriesproduct";
	
//	public static final String MODEL0_NAME_HIGH = "";
//	public static final String MODEL0_NAME_LOW  = "";
//	public static final String MODEL1_NAME_HIGH = "";
//	public static final String MODEL1_NAME_LOW  = "";
	public static final String MODEL2_NAME_HIGH = "";
	public static final String MODEL2_NAME_LOW  = "";
	public static final String MODEL3_NAME_HIGH = "";
	public static final String MODEL3_NAME_LOW  = "";
	public static final String MODEL4_NAME_HIGH = "";
	public static final String MODEL4_NAME_LOW  = "";
	public static final String MODEL5_NAME_HIGH = "";
	public static final String MODEL5_NAME_LOW  = "";
	
	public static void getNameAndDesc(){
		StringBuffer nameBuffer = new StringBuffer();
		StringBuffer descBuffer = new StringBuffer();
		
		String filePath = "D:\\htmlDoc\\allType.properties";
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader reader = null;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream1 = null;
		FileOutputStream fileOutputStream2 = null;
		List<String> list = new ArrayList<String>();
		List<String> lineList = new ArrayList<String>();
		List<String> descList = new ArrayList<String>();
		int count = 0;
		try {
			inputStream = new FileInputStream(filePath);
			String line;
			reader = new BufferedReader(new InputStreamReader(inputStream));
			line = reader.readLine();
			while (line != null) {
				if((!line.contains(".description") && !line.contains(".desc")) ){
					if(line.contains("type."+MODEL0_NAME_HIGH+".") || line.contains("type."+MODEL0_NAME_LOW+".")
							|| line.contains("type."+MODEL1_NAME_HIGH+".") || line.contains("type."+MODEL1_NAME_LOW+".")
							|| line.contains("type."+MODEL2_NAME_HIGH+".") || line.contains("type."+MODEL2_NAME_LOW+".")
							|| line.contains("type."+MODEL3_NAME_HIGH+".") || line.contains("type."+MODEL3_NAME_LOW+".")
							|| line.contains("type."+MODEL4_NAME_HIGH+".") || line.contains("type."+MODEL4_NAME_LOW+".")
							|| line.contains("type."+MODEL5_NAME_HIGH+".") || line.contains("type."+MODEL5_NAME_LOW+".")){
						line = line.replace(".name", "\t\t").trim()
								.replace("type."+MODEL0_NAME_HIGH+".", "").replace("type."+MODEL0_NAME_LOW+".", "")
								.replace("type."+MODEL1_NAME_HIGH+".", "").replace("type."+MODEL1_NAME_LOW+".", "")
								.replace("type."+MODEL2_NAME_HIGH+".", "").replace("type."+MODEL2_NAME_LOW+".", "")
								.replace("type."+MODEL3_NAME_HIGH+".", "").replace("type."+MODEL3_NAME_LOW+".", "")
								.replace("type."+MODEL4_NAME_HIGH+".", "").replace("type."+MODEL4_NAME_LOW+".", "")
								.replace("type."+MODEL5_NAME_HIGH+".", "").replace("type."+MODEL5_NAME_LOW+".", "")
								.replace(".", "");
						lineList.add(line);
					}					
				}else {
					if(line.contains("type."+MODEL0_NAME_HIGH+".") || line.contains("type."+MODEL0_NAME_LOW+".")
							|| line.contains("type."+MODEL1_NAME_HIGH+".") || line.contains("type."+MODEL1_NAME_LOW+".")
							|| line.contains("type."+MODEL2_NAME_HIGH+".") || line.contains("type."+MODEL2_NAME_LOW+".")
							|| line.contains("type."+MODEL3_NAME_HIGH+".") || line.contains("type."+MODEL3_NAME_LOW+".")
							|| line.contains("type."+MODEL4_NAME_HIGH+".") || line.contains("type."+MODEL4_NAME_LOW+".")
							|| line.contains("type."+MODEL5_NAME_HIGH+".") || line.contains("type."+MODEL5_NAME_LOW+".")){
						line = line.replace(".description", "\t\t").replace("desc",  "\t\t").trim()
								.replace("type."+MODEL0_NAME_HIGH+".", "").replace(MODEL0_NAME_LOW, "")
								.replace("type."+MODEL1_NAME_HIGH+".", "").replace(MODEL1_NAME_LOW, "")
								.replace("type."+MODEL2_NAME_HIGH+".", "").replace(MODEL2_NAME_LOW, "")
								.replace("type."+MODEL3_NAME_HIGH+".", "").replace(MODEL3_NAME_LOW, "")
								.replace("type."+MODEL4_NAME_HIGH+".", "").replace(MODEL4_NAME_LOW, "")
								.replace("type."+MODEL5_NAME_HIGH+".", "").replace(MODEL5_NAME_LOW, "")
								.replace(".", "");
						descList.add(line);
					}
				}
				stringBuffer.append(line);
				stringBuffer.append("\n");
				line = reader.readLine();
			}
			
			File file1 = new File(NAMEPATH);
			File file2 = new File(DESCPATH);
			if(!file1.exists()){
				file1.createNewFile();
			}
			if(!file2.exists()){
				file2.createNewFile();
			}
			fileOutputStream1 = new FileOutputStream(file1);
			fileOutputStream2 = new FileOutputStream(file2);
			
			Collections.sort(lineList);
			for (String string : lineList) {
				nameBuffer.append(string);
				nameBuffer.append("\n");
			}
			Collections.sort(descList);
			for (String string : descList) {
				descBuffer.append(string);
				descBuffer.append("\n");
			}
			
			fileOutputStream1.write(nameBuffer.toString().getBytes());
			fileOutputStream2.write(descBuffer.toString().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				reader.close();
				inputStream.close();
				fileOutputStream1.close();
				fileOutputStream2.close();
				System.out.println("+++++++++++++++++++++++++++++");
				System.out.println("Done~!");
				System.out.println("+++++++++++++++++++++++++++++");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getNameAndDesc();
//		String filePath = "D:\\htmlDoc\\nemo-locales.txt";
//		String filePath = "D:\\htmlDoc\\commerceservices.txt";
//		String filePath = "D:\\htmlDoc\\store-local.txt";
//		String filePath =  "D:\\htmlDoc\\site-local.txt";
//		String filePath = "D:\\htmlDoc\\unit-b2b-local.txt";
//		String filePath = "D:\\htmlDoc\\punchout-local-1.txt";
		
//		ThreadLocal<String> threadLocal = new ThreadLocal<String>();
//		StringBuffer nameBuffer = new StringBuffer();
//		StringBuffer descBuffer = new StringBuffer();
//		
//		String filePath = "D:\\htmlDoc\\allType.properties";
//		StringBuffer stringBuffer = new StringBuffer();
//		BufferedReader reader = null;
//		InputStream inputStream = null;
//		FileOutputStream fileOutputStream1 = null;
//		FileOutputStream fileOutputStream2 = null;
//		List<String> list = new ArrayList<String>();
//		List<String> lineList = new ArrayList<String>();
//		List<String> descList = new ArrayList<String>();
//		int count = 0;
//		try {
//			inputStream = new FileInputStream(filePath);
//			String line;
//			reader = new BufferedReader(new InputStreamReader(inputStream));
//			line = reader.readLine();
//			while (line != null) {
//				if((!line.contains(".description") && !line.contains(".desc")) ){
//					if(line.contains("type."+MODEL0_NAME_HIGH+".") || line.contains("type."+MODEL0_NAME_LOW+".")
//							|| line.contains("type."+MODEL1_NAME_HIGH+".") || line.contains("type."+MODEL1_NAME_LOW+".")
////							|| line.contains("type."+MODEL2_NAME_HIGH+".") || line.contains("type."+MODEL2_NAME_LOW+".")
////							|| line.contains("type."+MODEL3_NAME_HIGH+".") || line.contains("type."+MODEL3_NAME_LOW+".")
////							|| line.contains("type."+MODEL4_NAME_HIGH+".") || line.contains("type."+MODEL4_NAME_LOW+".")
//							|| line.contains("type."+MODEL5_NAME_HIGH+".") || line.contains("type."+MODEL5_NAME_LOW+".")){
//						line = line.replace("type.", "").replace(".name", "\t\t").replace(".", "").trim()
//								.replace(MODEL0_NAME_HIGH, "").replace(MODEL0_NAME_LOW, "")
//								.replace(MODEL1_NAME_HIGH, "").replace(MODEL1_NAME_LOW, "")
////								.replace(MODEL2_NAME_HIGH, "").replace(MODEL2_NAME_LOW, "")
////								.replace(MODEL3_NAME_HIGH, "").replace(MODEL3_NAME_LOW, "")
////								.replace(MODEL4_NAME_HIGH, "").replace(MODEL4_NAME_LOW, "")
//								.replace(MODEL5_NAME_HIGH, "").replace(MODEL5_NAME_LOW, "");
//						lineList.add(line);
//						System.out.println(line);
//					}					
//				}else {
//					if(line.contains("type."+MODEL0_NAME_HIGH+".") || line.contains("type."+MODEL0_NAME_LOW+".")
//							|| line.contains("type."+MODEL1_NAME_HIGH+".") || line.contains("type."+MODEL1_NAME_LOW+".")
////							|| line.contains("type."+MODEL2_NAME_HIGH+".") || line.contains("type."+MODEL2_NAME_LOW+".")
////							|| line.contains("type."+MODEL3_NAME_HIGH+".") || line.contains("type."+MODEL3_NAME_LOW+".")
////							|| line.contains("type."+MODEL4_NAME_HIGH+".") || line.contains("type."+MODEL4_NAME_LOW+".")
//							|| line.contains("type."+MODEL5_NAME_HIGH+".") || line.contains("type."+MODEL5_NAME_LOW+".")){
//						line = line.replace("type.", "").replace(".description", "\t\t").replace("desc",  "\t\t").replace(".", "").trim()
//								.replace(MODEL0_NAME_HIGH, "").replace(MODEL0_NAME_LOW, "")
//								.replace(MODEL1_NAME_HIGH, "").replace(MODEL1_NAME_LOW, "")
////								.replace(MODEL2_NAME_HIGH, "").replace(MODEL2_NAME_LOW, "")
////								.replace(MODEL3_NAME_HIGH, "").replace(MODEL3_NAME_LOW, "")
////								.replace(MODEL4_NAME_HIGH, "").replace(MODEL4_NAME_LOW, "")
//								.replace(MODEL5_NAME_HIGH, "").replace(MODEL5_NAME_LOW, "");
//						descList.add(line);
////						System.out.println(line);
//					}
//				}
//				stringBuffer.append(line);
//				stringBuffer.append("\n");
//				line = reader.readLine();
//			}
//			
//			File file1 = new File(NAMEPATH);
//			File file2 = new File(DESCPATH);
//			if(!file1.exists()){
//				file1.createNewFile();
//			}
//			if(!file2.exists()){
//				file2.createNewFile();
//			}
//			fileOutputStream1 = new FileOutputStream(file1);
//			fileOutputStream2 = new FileOutputStream(file2);
//			
//			Collections.sort(lineList);
//			for (String string : lineList) {
//				nameBuffer.append(string);
//				nameBuffer.append("\n");
//			}
//			Collections.sort(descList);
//			for (String string : descList) {
//				descBuffer.append(string);
//				descBuffer.append("\n");
//			}
//			
//			fileOutputStream1.write(nameBuffer.toString().getBytes());
//			fileOutputStream2.write(descBuffer.toString().getBytes());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally{
//			try {
//				reader.close();
//				inputStream.close();
//				fileOutputStream1.close();
//				fileOutputStream2.close();
//				System.out.println("+++++++++++++++++++++++++++++");
//				System.out.println("Done~!");
//				System.out.println("+++++++++++++++++++++++++++++");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

}
