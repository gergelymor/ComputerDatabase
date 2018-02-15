package com.bacskai.crud.elements;

import java.util.Date;

public class Computer {
	
	private String name;
	private Date introDate;
	private Date discDate;
	private String company;
	
	public Computer(String name, Date introDate, Date discDate, String company){
		this.setName(name);
		this.setIntroDate(introDate);
		this.setDiscDate(discDate);
		this.setCompany(company);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getIntroDate() {
		return introDate;
	}

	public void setIntroDate(Date introDate) {
		this.introDate = introDate;
	}

	public Date getDiscDate() {
		return discDate;
	}

	public void setDiscDate(Date discDate) {
		this.discDate = discDate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
