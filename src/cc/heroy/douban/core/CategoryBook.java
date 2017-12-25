package cc.heroy.douban.core;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.impl.client.CloseableHttpClient;

import com.alibaba.fastjson.JSONObject;

import cc.heroy.douban.bean.Book;
//import cc.heroy.douban.bean.Movie;
import cc.heroy.douban.task.HTMLAnalyzer;
import cc.heroy.douban.task.URLAnalyzer;
import cc.heroy.douban.task.URLSpider;
import cc.heroy.douban.task.URLSpiderListener;
import cc.heroy.douban.util.HttpClientUtil;

/**
 * 功能 ：爬取豆瓣
 */
public class CategoryBook {

	
	
	//具体详情url
	String durl = "https://book.douban.com/subject/26906838/";
	String durl1 = "https://book.douban.com/subject/5267472/";
	String durl2 = "https://book.douban.com/subject/26596778/";
	
	// 豆瓣详情页的url容器，作为 生产者消费者 的容器使用
	BlockingQueue<String> urls = new ArrayBlockingQueue<String>(400000);
	// 获取的页面实体(URLAnalyzer使用)
	BlockingQueue<String> entitys1 = new ArrayBlockingQueue<String>(200000);
	// 获取的页面实体(HTMLAnalyzer使用)
	BlockingQueue<String> entitys2 = new ArrayBlockingQueue<String>(200000);
	// 被使用过的url(去重)
	CopyOnWriteArraySet<String> usedURLS = new CopyOnWriteArraySet<>();
	// 储存获取的json对象
	List<JSONObject> jsons = new ArrayList<JSONObject>();
	
	// 储存获取的book对象（理解Vector）
	Vector<Book> books = new Vector<Book>(200);
	// 线程池(后期添加线程日志)
	ExecutorService pool = Executors.newFixedThreadPool(100);
	// URLSpider线程数
	private final int spiderCount = 3;
	// URLAnalyzer线程数
	private final int urlAnalyzerCount = 2;
	// HTMLAnalyzer线程数
	private final int HTMLAnalyzerCount = 2;
	

	// URLSpider 的 二元闭锁
	int spiderStartGateNum = 1;
	int spiderEndGateNum = spiderStartGateNum * spiderCount;

	// URLAnalyzer的二元闭锁
	int urlAnalyzerStartGateNum = 1;
	int urlAnalyzerEndGateNum = urlAnalyzerStartGateNum * urlAnalyzerCount;

	// HTMLAnalyzer的二元闭锁
	int HTMLAnalyzerStartGateNum = 1;
	int HTMLAnalyzerEndGateNum = HTMLAnalyzerStartGateNum * HTMLAnalyzerCount;
	
	CountDownLatch spiderStartGate = new CountDownLatch(spiderStartGateNum);
	CountDownLatch spiderEndGate = new CountDownLatch(spiderEndGateNum);

	CountDownLatch urlAnalyzerStartGate = new CountDownLatch(urlAnalyzerStartGateNum);
	CountDownLatch urlAnalyzerEndGate = new CountDownLatch(urlAnalyzerEndGateNum);

	CountDownLatch HTMLAnalyzerStartGate = new CountDownLatch(HTMLAnalyzerStartGateNum);
	CountDownLatch HTMLAnalyzerEndGate = new CountDownLatch(HTMLAnalyzerEndGateNum);
	
	CountDownLatch urlSpiderStartGate = new CountDownLatch(spiderStartGateNum);
	CountDownLatch urlSpiderEndGate = new CountDownLatch(spiderEndGateNum);
	
	public void spider() {

		// 开始时间
		long begin_time = System.currentTimeMillis();

		// 获取单个httpClient
		CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();

		try {
			urls.put(durl);
			urls.put(durl1);
			urls.put(durl2);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//创建URLSpider监听任务
		URLSpiderListener urlSpiderListener = new URLSpiderListener(httpClient, urls, entitys1, entitys2, pool);
		
		// 同时启动URLAnalyzer和HTMLAnalyzer,URLSpider
		for (int i = 0; i < urlAnalyzerCount; i++) {
			pool.submit(new Thread(new URLAnalyzer(entitys1,entitys2, urls, usedURLS, urlAnalyzerStartGate, urlAnalyzerEndGate)));
		}
		
		for(int i = 0;i < HTMLAnalyzerCount;i++){
			pool.submit(new Thread(new HTMLAnalyzer(entitys1,entitys2, urls ,usedURLS,HTMLAnalyzerStartGate,HTMLAnalyzerEndGate,books)));
		}
		
		for(int i = 0;i < spiderCount ;i++) {
			URLSpider spider = new URLSpider(httpClient, urls, entitys1, entitys2, urlSpiderStartGate, urlSpiderEndGate);
			//将spider任务注册到监听器
			spider.registerListner(urlSpiderListener);
			pool.submit(new Thread(spider));
		}
		
		
		
		try {
			urlSpiderStartGate.countDown();
			Thread.sleep(10000);
			urlAnalyzerStartGate.countDown();
			Thread.sleep(10000);
			HTMLAnalyzerStartGate.countDown();
			System.out.println("启动监听器");
			pool.submit(urlSpiderListener);
			//需要改动！
			urlSpiderEndGate.await();
			urlAnalyzerEndGate.await();
			HTMLAnalyzerEndGate.await();
			//启动监听器

//			for(Book m : books){
//				System.out.println("爬取到的电影信息 ："+"《"+m.getTitle()+"》"+" 剧情 ："+m.getStory());
//				System.out.println(m.getTitle()+"  类型:"+m.getInfo());
//			}
			
			long end_time = System.currentTimeMillis();
System.out.println("待访问的url数量  ："+urls.size());
System.out.println("已访问的url数量  ："+usedURLS.size());
			System.out.println("结束时间"+(end_time - begin_time));
	
			httpClient.close();
			// 必须关闭线程池
			pool.shutdownNow();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new CategoryBook().spider();
	}
}

