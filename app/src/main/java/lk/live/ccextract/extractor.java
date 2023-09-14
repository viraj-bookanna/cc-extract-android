package lk.live.ccextract;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.regex.Matcher;
import android.util.Log;
import android.widget.Toast;

public class extractor {
    
	private static boolean luhn(String ccnum){
		try{
			int l = ccnum.length();
			int sum = 0;
			for(int i=0;i<l;i++){
				int pos = l-i-1;
				int curr = Integer.parseInt(Character.toString(ccnum.charAt(pos)));
				if(i%2==0){
					sum += curr;
				}
				else{
					curr *= 2;
					int rem = curr%10;
					sum += rem+(curr-rem)/10;
				}
			}
			return sum%10 == 0;
		}
		catch(Exception e){
			return false;
		}
	}
	private static String cc_type(String ccnum){
		String[] amex = {"34","37"};
		String[] master = {"51","52","53","54","55"};
		if(ccnum.substring(0,1) == "4"){
			return "visa";
		}
		else if(ccnum.substring(0,4) == "6011"){
			return "discover";
		}
		else if(Arrays.asList(amex).contains(ccnum.substring(0,2))){
			return "american_express";
		}
		else if(Arrays.asList(master).contains(ccnum.substring(0,2))){
			return "master";
		}
		else{
			return "default";
		}
	}
    public static String[] findcc(String text){
		Pattern p_cc = Pattern.compile("(?:^|[^0-9])(\\d{15,19})(?:[^0-9]|$)");
		Pattern p_exp = Pattern.compile("(?:^|[^0-9])(?:(?:(\\d{2}|20\\d{2})([^0-9a-zA-Z])\\2*?(\\d{2}))|(?:(\\d{2})([^0-9a-zA-Z])\\5*?(\\d{2}|20\\d{2})))(?:[^0-9]|$)");
		Pattern p_exp2 = Pattern.compile("(?:^|[^0-9])(?:(0\\d|1[012])((?:20)?[23]\\d))(?:[^0-9]|$)");
		Pattern p_cvv = Pattern.compile("(?:^|[^0-9])(\\d{3})(?:[^0-9]|$)");
		Matcher m_cc = p_cc.matcher(text);
		Matcher m_cc2 = p_cc.matcher(text.replaceAll("\\s(\\d{4})", "$1"));
		String cc,y,m,cvv;
		if(m_cc.find()){
			cc = m_cc.group(1);
		}
		else if(m_cc2.find()){
			cc = m_cc2.group(1);
		}
		else{
			return null;
		}
		Matcher m_exp = p_exp.matcher(text);
		Matcher m_exp2 = p_exp.matcher(text.replace(" ", ""));
		Matcher m_exp3 = p_exp2.matcher(text);
		if(m_exp.find()){}
		else if(m_exp2.find()){
			m_exp = m_exp2;
		}
		else if(m_exp3.find()){
			m_exp = p_exp.matcher(m_exp3.group(1)+"|"+m_exp3.group(2));
			if(!m_exp.find()){
				return null;
			}
		}
		else{
			return null;
		}
		String[] exp = {
			(m_exp.group(1) != null) ? m_exp.group(1) : m_exp.group(4),
			(m_exp.group(3) != null) ? m_exp.group(3) : m_exp.group(6)
		};
		if(exp[0].length()==4 || !(exp[0].startsWith("0") || exp[0].startsWith("1"))){
			y = exp[0];
			m = exp[1];
		}
		else{
			y = exp[1];
			m = exp[0];
		}
		y = (y.length()==4) ? y.substring(2,4) : y;
		if(cc_type(cc) == "american_express"){
			p_cvv = Pattern.compile("(?:^|[^0-9])(\\d{4})(?:[^0-9]|$)");
		}
		Matcher m_cvv = p_cvv.matcher(text);
		if(m_cvv.find()){
			cvv = m_cvv.group(1);
		}
		else{
			return null;
		}
		if(!luhn(cc)){
			return null;
		}
		String[] ret = {cc, m, y, cvv};
		return ret;
	}
	public static String parseCc(String[] cc, String pattern){
		if(cc == null){
			return null;
		}
		for(int i=0;i<cc.length;i++){
			pattern = pattern.replaceAll("\\{"+(i+1)+"\\}", cc[i]);
		}
		return pattern;
	}
	public static String findAndParseCc(String text, String pattern){
		String[] cc = findcc(text);
		if(cc == null){
			return null;
		}
		for(int i=0;i<cc.length;i++){
			pattern = pattern.replaceAll("\\{"+(i+1)+"\\}", cc[i]);
		}
		return pattern;
	}
}
