package cc.heroy.douban.core;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textgethtml {
	public static String CutString(String start1,String end1,String content1){
	       String start = " ";
	       String end = " ";
	       String content = "作者: [美]埃里克·马瑟斯 出版社: 人民邮电出版社 副标题: 从入门到实践 原作名: Python Crash Course 译者: 袁国忠 出版年: 2016-7-1 页数: 459 定价: CNY 89.00 装帧: 平装 丛书: 图灵程序设计丛书 ISBN: 9787115428028";
	       String regex = String.format("%s.*%s", start,end);
	       Pattern pattern = Pattern.compile(regex);
	       Matcher matcher = pattern.matcher(content);
	      //System.out.println("------"+matcher.group(1).toString());
	       
	       String aim;
	       if (matcher.find()){
	    	   //System.out.println(matcher.group(1));
	    	   //System.out.println(matcher.group());
	    	   aim=matcher.group().replace(start, "").replace(end, "");
	    	   
	           System.out.println(aim);
	       }else{
	    	   aim=content;
	           System.out.println("not found");
	       }
		
		return aim;
				
	}
	public static void main(String[] args) throws Exception, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault() ;
		//String url="https://book.douban.com/subject_search?search_text=互联网，编程，算法&cat=1003&start=1";
		//String url="https://book.douban.com";
		//String url="https://book.douban.com/subject/26906838/";
		String url="https://book.douban.com/subject/26916700/";
		//String url="https://book.douban.com/subject_search?search_text=互联网，编程，算法";
		//String url="http://www.douban.com/tag/%E5%B0%8F%E8%AF%B4/book?start=0";
		/*URI url = new URIBuilder("https://book.douban.com/subject_search").setParameter("search_text",
                "互联网，编程，算法").build();
		System.out.println("url"+url.toString());*/
		
		HttpGet get = new HttpGet(url);
		
	
		
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
		get.setConfig(config);
		CloseableHttpResponse response = null ;
			response = httpClient.execute(get);
//			
//			if (response.getStatusLine().getStatusCode() == 200) {  
//                HttpEntity httpEntity = response.getEntity();  
//                if(httpEntity.getContentEncoding()!=null){  
//                if("gzip".equalsIgnoreCase(httpEntity.getContentEncoding().getValue())){  
//                    httpEntity = new GzipDecompressingEntity(httpEntity);                 
//                } else if("deflate".equalsIgnoreCase(httpEntity.getContentEncoding().getValue())){  
//                    httpEntity = new DeflateDecompressingEntity(httpEntity);              
//                }}  
//                String result = EntityUtils.toString(httpEntity, "UTF-8");// 取出应答字符串  
//                   System.out.println(result);} 
			
			
			String content = EntityUtils.toString(response.getEntity(),"UTF-8");
			
			Document doc = Jsoup.parse(content);
//			//Bookurl
//			String url = content.substring(0,content.indexOf("#"));
//			System.out.println(url);
			//BookName
			Elements titles = doc.getElementsByTag("title");
			String title = titles.get(0).text();
			System.out.println(title);
			Element elements1 = doc.getElementById("info");
			//Element elements2 =  (Element) elements1.childNode(2);
			String tem = elements1.text().toString();
			System.out.println(elements1.text());
//			System.out.println(CutString("作者: "," ",tem));
//			System.out.println(CutString("作者: ","出版社",tem));
//			System.out.println(CutString("出版社:","译者:",tem));
//			System.out.println(CutString("译者:","出版年:",tem));
//			System.out.println(CutString("出版年:","页数:",tem));
//			System.out.println(CutString("页数:","定价:",tem));
//			System.out.println(CutString("定价:","装帧:",tem));
//			System.out.println(CutString("装帧:","ISBN:",tem));
			//System.out.println(CutString("ISBN:","出版社:",tem));
			
			//评分
			Elements races = doc.getElementsByClass("ll rating_num ");
			String race = races.text().toString();
			System.out.println(race);
			//评价人数
			Elements ratingpeoples = doc.getElementsByClass("rating_people");
			String ratingpeople = ratingpeoples.text().toString();
			System.out.println(ratingpeople);
			
			///作者*出版社/
			/*Pattern p = Pattern.compile("作者*出版社");
		       Matcher m = p.matcher(tem); // 获取 matcher 对象
		       int count = 0;
		       System.out.println("11111"+m.start());
		       while(m.find()) {
		         count++;
		         System.out.println("Match number "+count);
		         System.out.println("start(): "+m.start());
		         System.out.println("end(): "+m.end());
		      }*/
		       
		      
			
			
			
			
//			String[] tem=elements1.text().toString().split(":");
//			for (int i = 0; i < tem.length; i++) {
//				System.out.println("--"+tem[i]);
//			}
			
			//CutString("作者: "," ",tem);
			
	}
	

}
