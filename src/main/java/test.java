import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class test  extends JdbcDaoSupport  {

	public static void main(String[] args) throws ParseException {
		Date date = null;
		
		String sw= "23/04/24$.";
		
		System.out.println("ds "+sw.replaceAll("/", "-"));
		
		
		System.out.println("rwemove ");
String value = "27-04-2024";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
		date = sdf.parse(value);
		if(!value.equals(sdf.format(date))){
			date = null;
		}
		
		if(date==null){
			System.out.println(1);
		}else{
			System.out.println(2);
		}
		String s = "6082210100821142";
		
		
	s=	org.apache.commons.lang.StringUtils.overlay(s, org.apache.commons.lang.StringUtils.repeat("X", s.length()-2), 6, s.length()-4);
		System.out.println("ss "+ s);


	}
	
	

}
