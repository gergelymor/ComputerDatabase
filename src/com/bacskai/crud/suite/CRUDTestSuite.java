package com.bacskai.crud.suite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.bacskai.crud.elements.*;

public class CRUDTestSuite {
	
private WebDriver driver;
private CompDBAccessor cdbAccessor;
	
	@Before
	public void setUp(){
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		ChromeOptions options = new ChromeOptions(); 
		options.addArguments("disable-infobars");
		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
		driver.get("http://computer-database.herokuapp.com/computers");
		cdbAccessor = new CompDBAccessor(driver);
	}
	
	@Test
	public void testCreateReadDelete() throws ParseException{
		Date introDate = new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01");
		Date discDate = new SimpleDateFormat("yyyy-MM-dd").parse("2005-01-01");
		Computer computer = new Computer("TestComputer001", introDate, discDate, "Apple Inc.");
		
		cdbAccessor.addElement(computer);
		cdbAccessor.validateElementPresenceInDatabase(true, computer);
		cdbAccessor.deleteElementFromDatabaseIfExists(computer);
		cdbAccessor.validateElementPresenceInDatabase(false, computer);
		
	}
	
	@Test
	public void testUpdateElementsValid() throws ParseException{
		Date introDateOld = new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01");
		Date discDateOld = new SimpleDateFormat("yyyy-MM-dd").parse("2005-01-01");
		Date introDateNew = new SimpleDateFormat("yyyy-MM-dd").parse("1996-04-08");
		Date discDateNew = new SimpleDateFormat("yyyy-MM-dd").parse("2011-10-22");
		
		Computer oldComputer = new Computer("TestComputer001", introDateOld, discDateOld, "Apple Inc.");
		Computer newComputer = new Computer("TestComputer002", introDateNew, discDateNew, "IBM");
		cdbAccessor.addElement(oldComputer);
		cdbAccessor.updateElement(oldComputer, newComputer);
		cdbAccessor.validateElementPresenceInDatabase(true, newComputer);
		cdbAccessor.deleteElementFromDatabaseIfExists(newComputer);
	}
	
	@Test
	public void testUpdateElementsInvalid() throws ParseException{
		Date introDate = new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01");
		Date discDate = new SimpleDateFormat("yyyy-MM-dd").parse("2005-01-01");
		Computer computer = new Computer("TestComputer001", introDate, discDate, "Apple Inc.");
		cdbAccessor.addElement(computer);
		cdbAccessor.updateElementWithInvalidValues(computer);
		cdbAccessor.validateElementPresenceInDatabase(true, computer);
		cdbAccessor.deleteElementFromDatabaseIfExists(computer);
	}
	
	
	@After
	public void tearDown(){
		driver.close();
	}

}
