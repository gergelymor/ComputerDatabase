package com.bacskai.crud.elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CompDBAccessor {

	protected WebDriver driver;
	protected String BASE_URL = "http://computer-database.herokuapp.com/computers";
	protected WebDriverWait wait;
	protected By addNewBtn = By.id("add");
	protected By nameTextField = By.id("name");
	protected By introDateField = By.id("introduced");
	protected By discDateField = By.id("discontinued");
	protected By companySelect = By.id("company");
	protected By submitFormBtn = By.xpath("//*[@class='btn primary']");
	protected By alertMessage = By.xpath("//*[@class='alert-message warning']");
	protected By filterTextField = By.id("searchbox");
	protected By filterBtn = By.id("searchsubmit");
	protected By resultsTable = By.xpath("//table[contains(@class,'computers')]");
	protected By deleteBtn = By.xpath("//*[@class='btn danger']");
	protected By emptyResult = By.xpath("//*[@class='well']");
	protected DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected DateFormat tableDateFormat = new SimpleDateFormat("dd MMM yyyy");
	protected By errorInvalidDate = By.xpath("//*[@class='clearfix error']");

	public CompDBAccessor(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 10);
	}
	
	protected void fillValueInTextField(By element, String value){
		driver.findElement(element).clear();
		driver.findElement(element).sendKeys(value);
	}

	public void addElement(Computer computer) {
		driver.findElement(addNewBtn).click();
		wait.until(ExpectedConditions.urlContains("/computers/new"));

		fillValueInTextField(nameTextField, computer.getName());
		fillValueInTextField(introDateField, inputDateFormat.format(computer.getIntroDate()));
		fillValueInTextField(discDateField, inputDateFormat.format(computer.getDiscDate()));

		Select select = new Select(driver.findElement(companySelect));
		select.selectByVisibleText(computer.getCompany());
		driver.findElement(submitFormBtn).click();

		wait.until(ExpectedConditions.not(ExpectedConditions
				.urlContains("/computers/new")));
		String successMessage = "Done! Computer " + computer.getName()
				+ " has been created";
		Assert.assertTrue(driver.findElement(alertMessage).getText()
				.equals(successMessage));
	}
	
	public void updateElement(Computer oldValuesComputer, Computer newValuesComputer){
		goToElement(oldValuesComputer);
		
		fillValueInTextField(nameTextField, newValuesComputer.getName());
		fillValueInTextField(introDateField, inputDateFormat.format(newValuesComputer.getIntroDate()));
		fillValueInTextField(discDateField, inputDateFormat.format(newValuesComputer.getDiscDate()));

		Select select = new Select(driver.findElement(companySelect));
		select.selectByVisibleText(newValuesComputer.getCompany());
		driver.findElement(submitFormBtn).click();

		wait.until(ExpectedConditions.not(ExpectedConditions
				.urlContains("/computers/new")));
		String successMessage = "Done! Computer " + newValuesComputer.getName()
				+ " has been updated";
		Assert.assertTrue(driver.findElement(alertMessage).getText()
				.equals(successMessage));
	}
	
	public void updateElementWithInvalidValues(Computer computer){
		goToElement(computer);
		fillValueInTextField(introDateField, "BlahBlah");
		fillValueInTextField(discDateField, "BlahBlah");
		driver.findElement(submitFormBtn).click();
		Assert.assertTrue(driver.findElements(errorInvalidDate).size() == 2);
	}

	public void validateElementPresenceInDatabase(Boolean shouldBePresent, Computer computer) {
		if (!driver.getCurrentUrl().equals(BASE_URL)) {
			driver.get(BASE_URL);
		}
		driver.findElement(filterTextField).sendKeys(computer.getName());
		driver.findElement(filterBtn).click();
		wait.until(ExpectedConditions.urlContains("?f="));
		WebElement resultRow = getCorrectRowFromResultsTable(computer.getName(), tableDateFormat.format(computer.getIntroDate()), tableDateFormat.format(computer.getDiscDate()), computer.getCompany());
		if(shouldBePresent){
			Assert.assertTrue(resultRow != null);
		}
		else{
			Assert.assertTrue((resultRow == null) ||( driver.findElements(emptyResult).size()==1));
		}
	}
	
	public void deleteElementFromDatabaseIfExists(Computer computer){
		if(goToElement(computer)){
			Assert.assertFalse(driver.findElement(submitFormBtn).getText().equals("Create this computer"));
			driver.findElement(deleteBtn).click();
			String resultMessage = driver.findElement(alertMessage).getText();
			Assert.assertTrue(resultMessage.equals("Done! Computer has been deleted"));
		}
		
	}
	
	public Boolean goToElement(Computer computer){
		if (!driver.getCurrentUrl().equals(BASE_URL)) {
			driver.get(BASE_URL);
		}
		driver.findElement(filterTextField).sendKeys(computer.getName());
		driver.findElement(filterBtn).click();
		wait.until(ExpectedConditions.urlContains("?f="));
		WebElement resultRow = getCorrectRowFromResultsTable(computer.getName(), tableDateFormat.format(computer.getIntroDate()), tableDateFormat.format(computer.getDiscDate()), computer.getCompany());
		if(resultRow!=null){
			List<WebElement> columnsList = resultRow.findElements(By.xpath("//td/a"));
			columnsList.get(0).click();
			return true;
		}
		return false;
	}
	
	public WebElement getCorrectRowFromResultsTable(String computerName, String introDate,
			String discDate, String company){
		if(driver.findElements(resultsTable).size() == 0){
			return null;
		}
		WebElement table = driver.findElement(resultsTable);
		List<WebElement> rowsList = table.findElements(By.tagName("tr"));
		
		Boolean nameCorrect = false;
		Boolean fromDateCorrect = false;
		Boolean toDateCorrct = false;
		Boolean companyCorrect = false;
		List<WebElement> columnsList = null;
		for (WebElement row : rowsList) {
			columnsList = row.findElements(By.tagName("td"));

			for (WebElement column : columnsList) {
				if (column.getText().equals(computerName)) {
					nameCorrect = true;
				} else if (column.getText().equals(introDate)) {
					fromDateCorrect = true;
				} else if (column.getText().equals(discDate)) {
					toDateCorrct = true;
				} else if (column.getText().equals(company)) {
					companyCorrect = true;
				}
			}
			if (nameCorrect && fromDateCorrect && toDateCorrct && companyCorrect) {
				return row;
			}
			else{
				nameCorrect = false;
				fromDateCorrect = false;
				toDateCorrct = false;
				companyCorrect = false;
			}
		}
		return null;
	}


}
