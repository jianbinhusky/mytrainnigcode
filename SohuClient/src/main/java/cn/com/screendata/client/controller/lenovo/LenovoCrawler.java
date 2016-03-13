package cn.com.screendata.client.controller.lenovo;

import cn.com.screendata.client.controller.parser.HtmlDocUtil;
import cn.com.screendata.client.controller.util.FileUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LenovoCrawler {
	
//	public final static String MODEL_CURRENT = "BaseStoreModel";
//	public final static String MODEL_INHERIT = "ItemModel";
	
//	public final static String MODEL_CURRENT = "CMSSiteModel";
//	public final static String MODEL_INHERIT = "BaseSiteModel";
	
//	public final static String MODEL_CURRENT = "B2BUnitModel";
//	public final static String MODEL_INHERIT = "CompanyModel";	
	
//	public final static String MODEL_CURRENT = "PunchOutCustomerProfileModel";
//	public final static String MODEL_INHERIT = "ItemModel";	
	
//	public final static String MODEL_CURRENT = "ClassificationClassModel";
//	public final static String MODEL_INHERIT = "CategoryModel";	
	
//	public final static String FILE_NAME = "b2cUnit";
//	public final static String SHEET_NAME = "b2cUnit";
//	public final static String MODEL_CURRENT = "B2CUnitModel";
//	public final static String MODEL_INHERIT = "CompanyModel";	
	
	//写文件路径
	private final static String dir = "D:\\htmlDoc\\";
	
	public static String fileToString(String filePath){
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader reader = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			String line;
			reader = new BufferedReader(new InputStreamReader(inputStream));
			line = reader.readLine();
			while (line != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				line = reader.readLine();
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
		
		return stringBuffer.toString();
	}
	
	public static String execute(String htmlString){
//		//写文件路径
//		String dir = "D:\\htmlDoc\\";
		//Excel 表头
		List<String> title=new ArrayList<String>();
		title.add("属性名");
		title.add("hmc 显示名");
		title.add("hmc tab 分组");
		title.add("类型");
		title.add("必填");
		title.add("多语言");
		title.add("默认值");
		title.add("所属Model");
		title.add("属性值业务用途/功能说明");
		title.add("备注");
		//Excel 数据
		List<String[]> dataList=new ArrayList<String[]>();
		
		Document document = HtmlDocUtil.getHtmlDocFromStr(htmlString);
		Elements elements = document.select("table.listtable > tbody > tr");
		for (Element element : elements) {
			String[]  records = new String[title.size()];
			String attr = "";
			String hmcName = "";
			String hmcGroup = "";
			String type = "";
			String optional = "";
			String mutlLanguage = "N";
			String defaultVal = "";
			String model = GetNeedMessage.MODEL_CURRENT;
			String function = "";
			String remark = "";
			if(element.hasAttr("id")){
				Elements elements2 = element.children();
				//属性名
				attr = elements2.get(2).select("input").val();
				//类型
				type = elements2.get(3).select("input").val();
				//必填optional
				Elements optionalElments = elements2.get(6).select("input");
				
				if(attr == null || attr.equals("")){
					attr = elements2.get(2).text();
					model = GetNeedMessage.MODEL_INHERIT;
				}
				
				if(type == null || type.equals("")){
					type = elements2.get(3).text();
				}else {
					type = type.split("-")[0].trim();					
				}
				type = type.replace("java.lang.", "").replace("java.util.","" );
				if(type.contains("localized:")){
					mutlLanguage = "Y";
					type = type.replace("localized:", "");
				}
				
				if(optionalElments != null && optionalElments.size() > 0){
					optional = optionalElments.get(0).val();
					if(optional.equals("true")){
						optional = "N";
					}else if(optional.equals("false")){
						optional = "Y";
					}
				}else{
					optional =  elements2.get(6).text();
					if(optional.equals("Yes")){
						optional = "N";
					}else if(optional.equals("No")){
						optional = "Y";
					}
				}
				records[0] = attr;
				records[1] = hmcName;
				records[2] = hmcGroup;
				records[3] = type;
				records[4] = optional;
				records[5] = mutlLanguage;
				records[6] = defaultVal;
				records[7] = model;
				records[8] = function;
				records[9] = remark;	
				dataList.add(records);
			}
		}
		String fileName = FileUtil.exportExcelFile(dataList, title, dir, GetNeedMessage.SHEET_NAME);
		
		return fileName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath = "D:\\htmlDoc\\"+ GetNeedMessage.FILE_NAME +".txt"; //html源码文件存放路径
		String htmlString = fileToString(filePath); //读取文件内容为字符串
		String fileName = execute(htmlString); //执行生成EXcel, 返回文件名
		
		GetNeedMessage.getNameAndDesc(); //生成Name & Desc 对应的txt文件
		
		Map<String, String> attMap = FileUtil.readTxt(GetNeedMessage.NAMEPATH);
		Map<String, String> descMap = FileUtil.readTxt(GetNeedMessage.DESCPATH);
		
		String targetFile = "D:\\htmlDoc\\" + fileName;
		FileUtil.readExcelAndWrite(targetFile , attMap , descMap);
		
		System.out.println("Done !");
	}

}
