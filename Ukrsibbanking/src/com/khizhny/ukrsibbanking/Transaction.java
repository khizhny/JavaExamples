package com.khizhny.ukrsibbanking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Transaction implements Comparable<Transaction> {
	 @Override  
	  public int compareTo(Transaction o) {
	    int rez=0;
		try{rez=o.getTransanctionDate().compareTo(getTransanctionDate());}
	    catch (Exception e)  { }
		 return rez;
	  }
	// Конструктор класса. Задаем дефолтные значения
	Transaction(){
		this.transanctionType=R.drawable.ic_transanction_unknown;
		this.currencyRate=new BigDecimal(1).setScale(3, RoundingMode.HALF_UP);
		this.setComission(new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
		}
	// Сведения о транзакнции
	public int transanctionType;
	private String body;
	private String number;	
	private Date transanctionDate;
	public String accountStateCurrency;
	private String accountDifferenceCurrency;
	private BigDecimal accountStateBefore;
	private BigDecimal accountStateAfter;
	private BigDecimal accountDifferencePlus;
	private BigDecimal accountDifferenceMinus;
	private BigDecimal currencyRate;
	private BigDecimal comission;
	
	public boolean hasTransanctionDate=false;
	public boolean hasAccountNumber=false;
	public boolean hasCardNumber=false;
	public boolean hasAccountStateCurrency=false;
	public boolean hasAccountDifferenceCurrency=false;
	public boolean hasAccountStateBefore=false;
	public boolean hasAccountStateAfter=false;
	public boolean hasAccountDifference=false;
	
	//=======================================================GETs============================
	//private String getCurrency(String s){return  s.replaceAll("[^A-Za-z]","");}
	public String getBody() {return body;}	
	public String getNumber() {return number;}
	public Date getTransanctionDate() {return transanctionDate;}
	public String getAccountStateCurrency(){return accountStateCurrency;}
	public BigDecimal getAccountStateBefore(){return accountStateBefore;}
	public BigDecimal getAccountStateAfter(){return accountStateAfter;}
	public String getAccountDifferenceCurrency(){return accountDifferenceCurrency;}
	public BigDecimal getAccountDifferencePlus(){return accountDifferencePlus;}
	public BigDecimal getCurrencyRate(){return currencyRate;}
	public BigDecimal getAccountDifferenceMinus(){return accountDifferenceMinus;}
	public String getTransanctionDateAsString(String transanctionDateFormat) {
		DateFormat f = new SimpleDateFormat(transanctionDateFormat, Locale.ENGLISH);
		return f.format(transanctionDate);		
	}
	public String getAccountDifferenceAsString(){
		String rez="";
		switch (accountDifferencePlus.signum()) {
		case -1: 
			rez=accountDifferencePlus.toString()+accountDifferenceCurrency;
			break;
		case 0: 
			rez=accountDifferencePlus.toString()+accountDifferenceCurrency;
			break;
		case 1: 
			rez="+"+accountDifferencePlus.toString()+accountDifferenceCurrency;
			break;
		}
		if (!currencyRate.equals(new BigDecimal(1).setScale(3, RoundingMode.HALF_UP))) {
			BigDecimal calculated_price = currencyRate.multiply(accountDifferencePlus,  MathContext.UNLIMITED).setScale(2, RoundingMode.HALF_UP);
			rez+="\n("+calculated_price+accountStateCurrency+")";
			rez+="\n(rate "+currencyRate.toString()+")";
		}
		return rez;
	}
	public String getAccountStateBeforeAsString(){
		return accountStateBefore.toString()+accountStateCurrency;
	}
	public String getAccountStateAfterAsString(){
		return accountStateAfter.toString()+accountStateCurrency;
	}
	
	// методы ввода сведений о транзакнции
	public void setCurrencyRate(BigDecimal currencyRate){
		this.currencyRate=currencyRate;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void setNumber(String number) {
		this.number = number;
	}	
	public void setTransanctionDate(Date transanctionDate) {
		this.transanctionDate=transanctionDate;
		this.hasTransanctionDate=true;
	}

	public void setAccountStateCurrency(String accountStateCurrency) {
		this.accountStateCurrency = accountStateCurrency;
		this.hasAccountStateCurrency=true;
	}
	public void setAccountDifferenceCurrency(String accountDifferenceCurrency) {
		this.accountDifferenceCurrency = accountDifferenceCurrency;
		this.hasAccountDifferenceCurrency=true;
	}
	public void setAccountStateBefore(BigDecimal accountStateBefore) {
		this.accountStateBefore = accountStateBefore;
		this.hasAccountStateBefore=true;
	}	
	public void setAccountStateAfter(BigDecimal accountStateAfter) {
		this.accountStateAfter = accountStateAfter;
		this.hasAccountStateAfter=true;		
	}	
	public void setAccountDifferencePlus(BigDecimal accountDifferencePlus) {
		this.accountDifferencePlus = accountDifferencePlus;
		this.accountDifferenceMinus = accountDifferencePlus.negate();
		this.hasAccountDifference=true;
	}
	public void setAccountDifferenceMinus(BigDecimal accountDifferenceMinus) {
		this.accountDifferenceMinus = accountDifferenceMinus;
		this.accountDifferencePlus = accountDifferenceMinus.negate();
		this.hasAccountDifference=true;	
	}
	public void setAccountStateBeforeFromString(String s){
		try {
			setAccountStateBefore(new BigDecimal(s.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP));
		}catch (Exception e) {}		
	}
	public void setAccountStateAfterFromString(String s){
		try{
		setAccountStateAfter(new BigDecimal(s.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP));
		}catch (Exception e) {}		
	}
	public void setAccountDifferencePlusFromString(String s){				
		try{
		setAccountDifferencePlus(new BigDecimal(s.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP));
		}catch (Exception e) {}
	}
	public void setAccountDifferenceMinusFromString(String s){
		try {
		setAccountDifferenceMinus(new BigDecimal(s.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP));
		}catch (Exception e) {}		
	}
	public BigDecimal getComission() {
		return comission;
	}
	public void setComission(BigDecimal comission) {
		this.comission = comission;
	}
	public void setComissionFromString(String comission) {		
		try{
			this.comission = new BigDecimal(comission.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP);
		}catch (Exception e) {}		
	}
	public void calculateMissedData(){
		// Calculating accountStateAfter if possible
		if (!hasAccountStateAfter && hasAccountStateBefore && hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountStateAfter=accountStateBefore.add(accountDifferencePlus).subtract(comission);
				hasAccountStateAfter=true;
			}
		}
		// Calculating accountStateBefore if possible
		if (!hasAccountStateBefore && hasAccountStateAfter && hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountStateBefore=accountStateAfter.subtract(accountDifferencePlus).add(comission);
				hasAccountStateBefore=true;
			}
		}
		// Calculating account difference if possible
		if (!hasAccountDifference && hasAccountStateBefore && hasAccountStateAfter) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountDifferencePlus=accountStateAfter.subtract(accountStateBefore).add(comission);
				accountDifferenceMinus= accountDifferencePlus.negate();
				hasAccountDifference=true;
			}
		}		
	}
}
