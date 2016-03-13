package cn.com.screendata.client.controller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private static Random random = new Random();
	private static Pattern mp = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[5,7])|(17[0]))\\d{8}$");
	private static String SALT="zhengliang123";
	
	private static Pattern ep = Pattern.compile("^([A-Za-z0-9]+)(([A-Za-z0-9]+)|(_+)|(\\-+)|(\\.+)|(\\++))*@((\\w+\\-+)|(\\w+\\.))*\\w{1,63}\\.[a-zA-Z]{2,6}$");
	
	public static boolean isEmpty(String str){
		return str==null||"".equals(str);
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
	
	public static boolean isMobile(String mobile){
		Matcher matcher=mp.matcher(mobile);
		return matcher.matches();
	}
	
	public static boolean isEmail(String email){
		Matcher matcher=ep.matcher(email);
		return matcher.matches();
	}
	
	public static String getRandomPassword(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<6;i++){
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}
        
        /**
         * 转换字符串，防止SQL注入
         * @return SQL查询所用字符串，以在两头添加“'”符号
         */
    public static String sqlEscapeString(String rawStr){
         return "'" + rawStr.replaceAll("\\\\", "\\\\").replaceAll("'", "\\'") + "'";
    }
        
    public static String inputStream2String(InputStream is,String charset) throws IOException {
    	BufferedReader in = new BufferedReader(new InputStreamReader(is,charset));
    	return buffer2String(in);
    }
    
    public static String buffer2String(BufferedReader buf) throws IOException {
    	StringBuffer buffer = new StringBuffer();
    	String line = "";
   		while ((line = buf.readLine()) != null) {
   			buffer.append(line);
   		}
    	return buffer.toString();
    }
    
    public static String compareStrings(String str1,String str2){
    	return str1.compareTo(str2)>0?(str1+str2):(str2+str1);
    }
    
    public static String getUtf8Encode(String src){
		try {
			src=URLEncoder.encode(src, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}
	public static String decodeUnicode(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = dataStr.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = dataStr.substring(start + 2, dataStr.length());
			} else {
				charStr = dataStr.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}

	public static String gbEncoding(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		System.out.println("unicodeBytes is: " + unicodeBytes);
		return unicodeBytes;
	}
}
