package framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
/**
*TheInternetHeroku app test class
*/
public class TheInternetHerokuTest 
{
	public static WebDriver driver;
	public WebDriverWait wait;
	public static Logger LOG = Logger.getLogger(TheInternetHerokuTest.class);

	public void configure()
	{
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		wait=new WebDriverWait(driver,10);
		driver.get("https://the-internet.herokuapp.com/");
	}

	@Test(enabled=false)
	public void ABTestingTest()
	{
		configure();
		WebElement abTestingLink=driver.findElement(By.xpath("//a[contains(text(),'A/B Testing')]"));
		abTestingLink.click();
		WebElement abTestingText=driver.findElement(By.tagName("p"));
		LOG.info(abTestingText.getText());
		Assert.assertTrue(abTestingText.getText().contains("split testing"));
	}

	@Test(enabled=false)
	public void addRemveElementTest()
	{
		configure();
		WebElement addRemoveElementLink=driver.findElement(By.xpath("//a[contains(text(),'Add/Remove Elements')]"));
		addRemoveElementLink.click();
		WebElement addElementBtn=driver.findElement(By.xpath("//button[@onclick='addElement()']"));
		addElementBtn.click();
		addElementBtn.click();
		List<WebElement> elements=driver.findElements(By.xpath("//div[@id='elements']"));
		for(WebElement deleteElement:elements)
		{
			List<WebElement> btnDeleteElements=deleteElement.findElements(By.tagName("button"));
			for(WebElement btnDelete:btnDeleteElements)
			{
				btnDelete.click();
			}
		}
	}

	@Test(enabled=false)
	public void basicAuthTest()
	{
		driver.get("https://admin:admin@the-internet.herokuapp.com/");
		WebElement basicAuthlink=driver.findElement(By.xpath(" //a[contains(text(),'Basic Auth')]"));
		basicAuthlink.click();
		String message=driver.findElement(By.cssSelector("p")).getText();
		LOG.info(message);
		Assert.assertTrue(message.contains("jhjh! You must have the proper credentials."),"incorrect message");
	}

	@Test(enabled=false)
	public void brokenImagesTest()
	{
		configure();
		WebElement brokenImageslink=driver.findElement(By.xpath("//a[contains(text(),'Broken Images')]"));
		brokenImageslink.click();
		List brokenImages = new ArrayList();
		List<WebElement> images = driver.findElements(By.tagName("img"));
		for(int image = 0; image < images.size(); image++) 
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = null;
			try 
			{
				LOG.info("Image link"+images.get(image).getAttribute("src"));
				response = client.execute(new HttpGet(images.get(image).getAttribute("src")));
			}
			catch (ClientProtocolException e)
			{	
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != 200)
			{ 
				brokenImages.add(images.get(image).getAttribute("src"));
			}
		}
		LOG.info("Broken Images : "+brokenImages);
	}

	@Test(enabled=false)
	public void uploadTest()
	{
		configure();
		WebElement fileUploadLink=driver.findElement(By.xpath("//a[contains(text(),'File Upload')]"));
		fileUploadLink.click();
		String filename = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"testupload.txt";
		File file = new File(filename);
		String path = file.getAbsolutePath();
		driver.findElement(By.id("file-upload")).sendKeys(path);
		driver.findElement(By.id("file-submit")).click();
		String text = driver.findElement(By.id("uploaded-files")).getText();	
		LOG.info("File uploaded successfully ..!!"+text);
	}

	@Test
	public void downloadTest() throws InterruptedException
	{
		String downloadFilepath = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"downloads";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		driver=new ChromeDriver(options);
		driver.get("https://the-internet.herokuapp.com/");
		WebElement downloadLink=driver.findElement(By.xpath("//a[@href='/download']"));
		downloadLink.click();
		List<WebElement> downloadFileLinks=driver.findElements(By.xpath("//div[@class='example']/a"));
		for(WebElement filelink:downloadFileLinks)
		{
			if(filelink.getText().contains("testupload.txt"))
			{
				filelink.click();
				LOG.info("file name "+filelink.getText());
				Thread.sleep(5000);
				break;
			}
		}
		LOG.info("Downloaded files");
		for(String file:getAllFiles(downloadFilepath))
		{
			LOG.info(file);
		}
	}

	private static Set<String> getAllFiles(String path) 
	{
		Set<String> listofFiles=new HashSet<>();
		File filepath=new File(path);
		File[] filesList = filepath.listFiles();
		for(File f : filesList){			
			if(f.isFile())
			{
				listofFiles.add(f.getName());
				f.delete();
			}
		}
		return listofFiles;
	}

	@AfterTest
	public void tearDown()
	{
		driver.quit();
	}
}
