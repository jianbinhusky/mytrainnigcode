package cn.com.screendata.client.controller.util;

import cn.com.screendata.client.controller.lenovo.GetNeedMessage;
import org.apache.log4j.Logger;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;

public class FileUtil {
	private static Logger log = Logger.getLogger(FileUtil.class);
	private static final String dir = "C:\\Users\\hujb4\\Desktop\\Task b2b\\";
    private static String attribute;
    private static String description;
    
	public static void main(String[] args){
//		String attrfile = "D:\\htmlDoc\\nameDesc\\" + "b2cUnitName" + ".txt";
//		String descfile = "D:\\htmlDoc\\nameDesc\\" + "b2cUnitDesc" + ".txt";
//		Map<String, String> attMap = readTxt(attrfile);
//		System.out.println("####################################");
//		System.out.println("####################################");
//		System.out.println("####################################");
//		Map<String, String> descMap = readTxt(descfile);
//
//		String fileName = "C:\\Users\\hujb4\\Desktop\\Task b2b\\2222.xlsx";
//		readExcelAndWrite(fileName , attMap , descMap);

		downloadImage("http://img1.mm131.com/pic/2371/m.jpg","mm.jpg","D:\\mm");
	}
	
	public static int parseFileLine(String file) throws Exception{
		int lineCount = 0;
		if (file.endsWith(".txt")) {// txt
			FileReader in = new FileReader(file);
			LineNumberReader reader = new LineNumberReader(in);
			String s = reader.readLine();
			while (s != null&&!"".equals(s)) {
				lineCount++;
				s = reader.readLine();
			}
			reader.close();
			in.close();
		}

		return lineCount;
	}
	
	public static List<String> parseFile(String file){
		List<String> mobiles=new ArrayList<String>();
		BufferedReader buf=null;
		FileInputStream input=null;
		try {
			if(file.endsWith(".txt")){
				buf=new BufferedReader(new UnicodeReader(new FileInputStream(file),Charset.defaultCharset().name()));
				String mobile=null;
				while((mobile=buf.readLine())!=null&&!"".equals(mobile)){
					mobiles.add(mobile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buf != null) {
					buf.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mobiles;
	}
	
	public static List<String> parseMobileFile(String file){
		List<String> mobiles=new ArrayList<String>();
		List<String> tmp=parseFile(file);
		for (String string : tmp) {
			if(string.length()<13)
				mobiles.add(string);
		}
		tmp.clear();
		tmp=null;
		return mobiles;
	}
	
	public static List<String> parseBlackWordFile(String file){
		return parseFile(file);
	}
	
	public static List<String> parseCorpNoFile(String file){
		List<String> corpNos=new ArrayList<String>();
		List<String> tmp=parseFile(file);
		for (String string : tmp) {
			if(string.length()<7)
				corpNos.add(string);
		}
		tmp.clear();
		tmp=null;
		return corpNos;
	}
	
	public static List<String> listResource(String dir){
		List<String> list=new ArrayList<String>();
		File directory=new File(dir);
		if(directory.exists()){
			File[] files=directory.listFiles();
			long st=DateUtil.getTodayStartTime();
			for (File file : files) {
				long mt=file.lastModified();
				if(mt<st)//今天以前的文件
					list.add(file.getAbsolutePath());
			}
		}
		return list;
	}
	
	public static void deleteFile(String file){
		File f=new File(file);
		if(f.exists())
			f.delete();
	}
	
	public static String createTempFile(String dir,List<String> list,String charsetName){
		String fileName=UUID.randomUUID().toString()+".txt";
		try {
			OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(dir+fileName), charsetName);
			for (String string : list) {
				osw.write(string);
				osw.write("\r\n");
			}
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	
	public static Map<String, String> readTxt(String fileName){
		InputStream inputStream = null;
		Map<String, String> attrMap = new LinkedHashMap<String, String>();
		BufferedReader reader = null;
		try {
			inputStream = new FileInputStream(fileName);
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = reader.readLine();
			while (line != null) {
				String[] attrStrings = line.split("=");
				if(attrStrings != null && attrStrings.length > 0){
					try {
						attrMap.put(attrStrings[0].trim().toLowerCase(), attrStrings[1].trim());
					} catch (Exception e) {
						
					}
				}
				line = reader.readLine();
			}
//			int count = 1;
//			for (Entry<String, String> entry : attrMap.entrySet()) {
//				System.out.println(count +"\t"+entry.getKey() +"\t" + entry.getValue());
//				count++;
//			}
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
		
		return attrMap;
	}
	
    public static void readExcelAndWrite(String fileName , Map<String, String> attrMap , Map<String, String> descMap) {

        boolean isE2007 = false;    //判断是否是excel2007格式
        InputStream input = null;
        FileOutputStream fos = null;
        Workbook wb  = null;
        if(fileName.endsWith("xlsx"))
            isE2007 = true;
        try {
            input = new FileInputStream(fileName);  //建立输入流

            fos = new FileOutputStream(dir+ GetNeedMessage.FILE_NAME + "^_^" + UUID.randomUUID() + ".xlsx");
            //根据文件格式(2003或者2007)来初始化
            if(isE2007) {
				wb = new XSSFWorkbook(input);

			}else {
				wb = new HSSFWorkbook(input);
			}
            Sheet sheet = wb.getSheetAt(0);     //获得第一个表单
            Iterator<Row> rows = sheet.rowIterator(); //获得第一个表单的迭代器
            while (rows.hasNext()) {
                Row row = rows.next();  //获得行数据
            	if(row.getRowNum() > 0){
                    Iterator<Cell> cells = row.cellIterator();    //获得第一行的迭代器
                    while (cells.hasNext()) {
                        Cell cell = cells.next();
                        if(cell.getColumnIndex() == 0){
                        	 System.out.println("row# "+row.getRowNum() + "\t" +"cell = " +cell.getStringCellValue() + "\t\t" + " MapValue = " +attrMap.get(cell.getStringCellValue().toLowerCase()));
                        	 attribute = attrMap.get(cell.getStringCellValue().toLowerCase());
							row.createCell(1).setCellValue(attribute);
                        	 description = descMap.get(cell.getStringCellValue().toLowerCase());
							row.createCell(8).setCellValue(description);
							System.out.println("cell 1 = " + row.getCell(1).getStringCellValue());
							System.out.println("cell 8 = " + row.getCell(8).getStringCellValue());
                        }
                    }
            	}
            }
            wb.write(fos);
			fos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }  finally{
        	try {
				input.close();
	        	fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }  
	
	public static String exportExcelFile(List<String[]> dataList,List<String> titleList,String dir,String sheetName){
		String fileName=null;
		File dirFile=new File(dir);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		FileOutputStream fos=null;
		try {
			fileName=UUID.randomUUID().toString()+".xlsx";
			Workbook wb=new SXSSFWorkbook(2000);
			POIXMLProperties props=((SXSSFWorkbook)wb).getXSSFWorkbook().getProperties();
			POIXMLProperties.CoreProperties coreProps=props.getCoreProperties();
			coreProps.setCreator("");
			Sheet sheet=wb.createSheet(sheetName);
			Row row=sheet.createRow(0);
			CellStyle titleStyle=wb.createCellStyle();
			Font titleFont=wb.createFont();
			titleFont.setFontName("黑体");
			titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			titleStyle.setFont(titleFont);
			titleStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			titleStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());
			titleStyle.setFillPattern(CellStyle.BIG_SPOTS);
			for(int i=0;i<titleList.size();i++){//写标题
				Cell cell=row.createCell(i);
				cell.setCellStyle(titleStyle);
				cell.setCellValue(titleList.get(i));
			}
			for(int i=0;i<dataList.size();i++){//写数据
				row=sheet.createRow(i+1);
				String[] obj=dataList.get(i);
				for (int j = 0; j < obj.length; j++) {
					String object = obj[j];
					Cell cell=row.createCell(j);
					cell.setCellValue(object);
				}
			}
			for(int i=0;i<titleList.size();i++){
				sheet.autoSizeColumn(i);
			}
			fos=new FileOutputStream(dir+fileName);
			wb.write(fos);
		} catch (Exception e) {
			fileName=null;
			e.printStackTrace();
		} finally{
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	
	public static void writeFile(File downfile, HttpServletRequest request, HttpServletResponse response, String filename,boolean isDownload) {

		if (downfile != null && downfile.exists()) {
			if (downfile.getName().toLowerCase().endsWith(".gif")) {
				response.setContentType(MediaType.IMAGE_GIF_VALUE);
			} else if (downfile.getName().toLowerCase().endsWith(".jpg")) {
				response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			} else if (filename.toLowerCase().endsWith(".jpg")) {
				response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			} else if (downfile.getName().toLowerCase().endsWith(".jpeg")) {
				response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			} else if (downfile.getName().toLowerCase().endsWith(".png")) {
				response.setContentType(MediaType.IMAGE_PNG_VALUE);
			} else if (downfile.getName().toLowerCase().endsWith(".flv")) {
				response.setContentType("video/flv");
			} else if (downfile.getName().toLowerCase().endsWith(".mp4")) {
				response.setContentType("video/mp4");
			} else if (downfile.getName().toLowerCase().endsWith(".mov")) {
				response.setContentType("video/mov");
			} else if (downfile.getName().toLowerCase().endsWith(".3gp")) {
				response.setContentType("video/3gp");
			} else if (downfile.getName().toLowerCase().endsWith(".ogg")) {
				response.setContentType("video/ogg");
			} else {
				response.setContentType("application/download");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			}
			
			response.setHeader("Content-Length", "" + downfile.length());
			response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

			response.setHeader("Accept-Ranges", "bytes");

			if(isDownload){
				response.setContentType("application/download");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			}
			long start = 0;
			long end = 0;
			end = downfile.length();
			
			response.setHeader("Content-Length", new Long(end - start).toString());


			FileInputStream fin = null;

			try {
				byte buff[] = new byte[1024];
				fin = new FileInputStream(downfile);
				int rsize = 0;
				int totalrsize = (int) (end - start);
				fin.skip(start);
				while (totalrsize > 0 && (rsize = fin.read(buff, 0, Math.min(1024, totalrsize))) > 0) {
					response.getOutputStream().write(buff, 0, rsize);
					// response.getOutputStream().flush();
					totalrsize -= rsize;
				}

			} catch (Exception e) {
				log.error("下载文件异常",e);
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void downloadImage(String urlString, String filename,String savePath) {
		try {
			// 构造URL
			URL url = new URL(urlString);
			// 打开连接
			URLConnection con = url.openConnection();
			//设置请求超时为5s
			con.setConnectTimeout(5*1000);
			// 输入流
			InputStream is = con.getInputStream();

			// 1K的数据缓冲
			byte[] bs = new byte[102400];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			File sf=new File(savePath);
			if(!sf.exists()){
				sf.mkdirs();
			}
			OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完毕，关闭所有链接
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}
}
