package framework;

import java.io.File;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TheInternetHerokuTest 
{
	public WebDriver driver;
	public WebDriverWait wait;
	public static Logger LOG = Logger.getLogger(TheInternetHerokuTest.class);
	
	@BeforeTest
	public void configure()
	{
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		wait=new WebDriverWait(driver,10);
	}
	@BeforeMethod
	public void setup()
	{
		driver.get("https://the-internet.herokuapp.com/");
	}
	
	@Test
	public void ABTestingTest()
	{
		WebElement abTestingLink=driver.findElement(By.xpath("//a[contains(text(),'A/B Testing')]"));
		abTestingLink.click();
		WebElement abTestingText=driver.findElement(By.tagName("p"));
		LOG.info(abTestingText.getText());
		Assert.assertTrue(abTestingText.getText().contains("split testing"));
	}
	@AfterTest
	public void tearDown()
	{
		driver.quit();
	}
}
