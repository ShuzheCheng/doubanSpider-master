package cc.heroy.douban.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.heroy.douban.bean.Book;
//import cc.heroy.douban.bean.Movie;
import cc.heroy.douban.bean.User;

/**
 * HTML页面解析线程
 * 从entitys中取出html源代码进行数据提取
 * 将movie存到vector容器
 */
public class HTMLAnalyzer implements Runnable{

	private final BlockingQueue<String> entitys1 ;
	private final BlockingQueue<String> entitys2 ;
	private final CountDownLatch startGate ;
	private final CountDownLatch endGate ;
//	private final Vector<Movie> movies ;
	private final Vector<Book> books ;
	private final BlockingQueue<String> urls;
	CopyOnWriteArraySet<String> usedUrls = new CopyOnWriteArraySet<>();
	//线程睡眠时间
	long space = 2000L;
	
	public HTMLAnalyzer(BlockingQueue<String> entitys1,BlockingQueue<String> entitys2 ,BlockingQueue<String> urls,CopyOnWriteArraySet<String> usedUrls ,CountDownLatch startGate , CountDownLatch endGate,Vector<Book> books){
		this.entitys1 = entitys1;
		this.entitys2 = entitys2;
		this.usedUrls = usedUrls;
		this.startGate = startGate;
		this.endGate = endGate;
//		this.movies = movies;
		this.books = books;
		this.urls = urls;
	}
	
	@Override
	public void run() {
		
//创建目录
		String path = "F:/books";
		String execlNum= UUID.randomUUID().toString();
		File p = new File(path);
		if(!p.exists()) {
			//创建文件目录
			p.mkdir();
		}
		//Excel
		 //第一步，创建一个workbook对应一个excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        //第二部，在workbook中创建一个sheet对应excel中的sheet
        HSSFSheet sheet = workbook.createSheet("book");
        //第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
        HSSFRow row = sheet.createRow(0);
        //第四步，创建单元格，设置表头
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("书名");
        cell = row.createCell(1);
        cell.setCellValue("评分");
        cell = row.createCell(2);
        cell.setCellValue("评价人数");
        cell = row.createCell(3);
        cell.setCellValue("详情");
        cell = row.createCell(4);
        cell.setCellValue("url");
        
        
        
        
        FileOutputStream fos = null;
//		FileWriter fw = null;
//		File f = new File(p,UUID.randomUUID()+".txt");
//		if(f.exists()) {
//			try {
//				f.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		//等待startGate
System.out.println(entitys2.size());
		try {
			startGate.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//启动
System.out.println("HTMLAnalyzer启动");
try {
	fos = new FileOutputStream("f:\\books\\"+execlNum+".xls");
} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
		for (int i = 0;!urls.isEmpty()||!entitys1.isEmpty()||!entitys2.isEmpty(); ) {
			
		
		//while(!urls.isEmpty()||!entitys1.isEmpty()||!entitys2.isEmpty()){
			try {

				
	            

				
				String content = entitys2.take();
				Book book = new Book();
				Document doc = Jsoup.parse(content);
				
				//url
				String url = content.substring(0,content.indexOf("#"));
				
				book.setUrl(url);
				//名
				Elements titles = doc.getElementsByTag("title");
				String title = titles.get(0).text();
				
				book.setTitle(title);

				//评分
				Elements races = doc.getElementsByClass("ll rating_num ");
				String race = races.text().toString();
				//System.out.println(race);
				
				book.setRace(race);
		
				//评价人数
				Elements ratingpeoples = doc.getElementsByClass("rating_people");
				String ratingpeople = ratingpeoples.text().toString();
				if(ratingpeople.equals("")){
					ratingpeople="0";
				}
				book.setRacepeople(ratingpeople);
				//System.out.println(ratingpeople);

				
				//详情
				Element elements1 = doc.getElementById("info");
				//Element elements2 =  (Element) elements1.childNode(2);
				String info = elements1.text().toString();
				book.setInfo(info);
				//System.out.println(elements1.text());
				
	           
//				books.add(book);

				//fw.append(book.toString()+"\r\n");
				String tem=book.getRacepeople();
				//if( Integer.parseInt(book.getRacepeople().toString())>100){
				
					HSSFRow row1 = sheet.createRow(i + 1);
		            //User user = users.get(i);
		            //创建单元格设值
					row1.createCell(0).setCellValue(book.getTitle());
					row1.createCell(1).setCellValue(book.getRace());
					row1.createCell(2).setCellValue(book.getRacepeople());
					row1.createCell(3).setCellValue(book.getInfo());
					row1.createCell(4).setCellValue(book.getUrl());
					i++;
				//}
				books.add(book);
System.out.println("写入："+book.getTitle()+book.getRace()+"  当前状态：urls :"+urls.size()+" ,entitys1 :"+entitys1.size()+" ,entitys2 :"+entitys2.size()+" ,usedUrl :"+(usedUrls.size()-urls.size()));
				Thread.sleep(space);
				if(i>=50){
					//workbook.write(fos);
					//fos.close();
					break;
					
				}
			} catch (Exception e) {
				System.out.println("页面解析失败");
			}
		}
		try {
            //FileOutputStream fos = new FileOutputStream("f:\\books\\user1.xls");
            workbook.write(fos);
            System.out.println("写入成功");
            fos.close();
			//fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		endGate.countDown();
System.out.println("HTMLAnalyzer结束");
		
	}
	
	

}
