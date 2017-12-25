package cc.heroy.douban.test;

import org.openqa.selenium.*;

import org.openqa.selenium.firefox.*;

import org.openqa.selenium.support.ui.*;

 

public class tpush {

public static void main(String[] args){

         

     //设置浏览器属性

    System.setProperty ("webdriver.firefox.bin","D:\\21dayjava\\firefox.exe");

     

       // 创建一个 FireFox 的浏览器实例
    System.out.print("test success111");
      WebDriver driver = new FirefoxDriver();

 
      System.out.print("test success22222");
        //WebDriverdriver = new InternetExplorerDriver ();

         driver.get("http://www.duba.com");

         System.out.print("test success33333");

}

}
