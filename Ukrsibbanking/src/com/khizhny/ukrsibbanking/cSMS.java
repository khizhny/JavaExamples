package com.khizhny.ukrsibbanking;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;

public class cSMS {
	// Конструктор класса. Задаем дефолтные значения
	cSMS(){
		this.accountDifferenceCurrency="UAH";
		this.accountStateCurrency="UAH";
		this.accountNumber="26251009142308";
	}
	public String getWordBetween(String splittingWord1, String splittingWord2) {
		return this.body.split(splittingWord1)[1].split(splittingWord2)[0];
	}
	public String getWordAfter(String splittingWord1, String wordSplitter) {
		return this.body.split(splittingWord1)[1].split(wordSplitter)[0];
	}
	public String getWordBefore(String searchedWord, String wordSplitter,  int wordCount) {
		String temp = this.body.split(searchedWord)[0];
		String[] arr=temp.split(wordSplitter);
		int wordsCount = arr.length; 
		return (arr[wordsCount-2]+wordSplitter+arr[wordsCount-1]);
	}
	private String getCurrency(String s){
		String rez="";
		for (int i=0; i<MainActivity.CURRENCY.length;i++) {
			if (s.lastIndexOf(MainActivity.CURRENCY[i][1])!=-1) {
				rez= MainActivity.CURRENCY[i][0];
			}
		}
		return rez;
	}
	
	private BigDecimal convertFromStringToCurrency(String s){
		if (s.lastIndexOf(".")==(s.length()-1)) {
			s=s.substring(0, s.length()-1);
		}
		int lastDotPosition=s.lastIndexOf(".");
		int lastComaPosition=s.lastIndexOf(",");
		if (lastDotPosition>lastComaPosition){ // 1,234.34
			s=s.replaceAll("[^\\d.]", "");
			s=s.replace(" ","");
			s=s.replace(",","");
		}
		else  // 1 234,34
		{
			s=s.replaceAll("[^\\d,]", "");
			s=s.replace(",",".");
		}
		// удаляем разделитель разрядов если он есть, но оставляем разделитель целой и дробной части.
		return new BigDecimal(s);
	}
	
	// Сведения о транзакнции
	private String body;
	private String number;	
	private Date transanctionDate;
	private String accountNumber;
	private String cardNumber;
	private String accountStateCurrency;
	private String accountDifferenceCurrency;
	private BigDecimal accountStateBefore;
	private BigDecimal accountStateAfter;
	private BigDecimal accountDifferencePlus;
	private BigDecimal accountDifferenceMinus;
	
	public boolean hasTransanctionDate=false;
	public boolean hasAccountNumber=false;
	public boolean hasCardNumber=false;
	public boolean hasAccountStateCurrency=false;
	public boolean hasAccountDifferenceCurrency=false;
	public boolean hasAccountStateBefore=false;
	public boolean hasAccountStateAfter=false;
	public boolean hasAccountDifference=false;
	
	// методы вывода сведений о транзакнции
	public String getBody() {return body;}	
	public String getNumber() {return number;}
	public Date getTransanctionDate() {return transanctionDate;}
	public String getAccountNumber() {return accountNumber;}
	public String getCardNumber() {return cardNumber;}
	public String getAccountStateCurrency(){return accountStateCurrency;}
	public BigDecimal getAccountStateBefore(){return accountStateBefore;}
	public BigDecimal getAccountStateAfter(){return accountStateAfter;}
	public String getAccountDifferenceCurrency(){return accountDifferenceCurrency;}
	public BigDecimal getAccountDifferencePlus(){return accountDifferencePlus;}
	public BigDecimal getAccountDifferenceMinus(){return accountDifferenceMinus;}
	
	
	// методы ввода сведений о транзакнции	
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
	public void setTransanctionDateFromString(String transanctionDate, String transanctionDateFormat) {
		DateFormat format = new SimpleDateFormat(transanctionDateFormat, Locale.ENGLISH);
		try {
			this.transanctionDate= format.parse(transanctionDate);
			this.hasTransanctionDate=true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
		hasAccountNumber=true;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		hasCardNumber=true;
	}	
	public void setAccountStateCurrency(String accountStateCurrency) {
		this.accountStateCurrency = accountStateCurrency;
		hasAccountStateCurrency=true;
	}
	public void setAccountDifferenceCurrency(String accountDifferenceCurrency) {
		this.accountDifferenceCurrency = accountDifferenceCurrency;
		hasAccountDifferenceCurrency=true;
	}
	public void setAccountStateBefore(BigDecimal accountStateBefore) {
		this.accountStateBefore = accountStateBefore;
		hasAccountStateBefore=true;
		if (hasAccountDifference && !hasAccountStateAfter) {
			this.accountStateAfter = this.accountStateBefore.add(this.accountDifferencePlus);
			hasAccountStateAfter=true;
		}
		if (hasAccountStateAfter && !hasAccountDifference) {
			this.accountDifferencePlus=this.accountStateAfter.subtract(this.accountStateBefore);
			this.accountDifferenceMinus= accountDifferencePlus.negate();
			hasAccountDifference=true;
		}
	}	
	public void setAccountStateAfter(BigDecimal accountStateAfter) {
		this.accountStateAfter = accountStateAfter;
		hasAccountStateAfter=true;
		if (hasAccountDifference && !hasAccountStateBefore) {
			this.accountStateBefore = accountStateAfter.subtract(this.accountDifferencePlus);
			hasAccountStateBefore=true;
		}
		if (hasAccountStateBefore && !hasAccountDifference) {
			this.accountDifferencePlus=this.accountStateAfter.subtract(this.accountStateBefore);
			this.accountDifferenceMinus= this.accountDifferencePlus.negate();
			hasAccountDifference=true;
		}
	}	
	public void setAccountDifferencePlus(BigDecimal accountDifferencePlus) {
		this.accountDifferencePlus = accountDifferencePlus;
		this.accountDifferenceMinus = accountDifferencePlus.negate();
		hasAccountDifference=true;
		if (!hasAccountStateAfter && hasAccountStateBefore) {
			this.accountStateAfter=this.accountStateBefore.add(this.accountDifferencePlus);
			hasAccountStateAfter=true;
		}
		if (hasAccountStateAfter && !hasAccountStateBefore) {
			this.accountStateBefore=this.accountStateAfter.subtract(this.accountDifferencePlus);
			hasAccountStateBefore=true;
		}
	}
	public void setAccountDifferenceMinus(BigDecimal accountDifferenceMinus) {
		setAccountDifferencePlus(accountDifferenceMinus.negate());		
	}
	public void setAccountStateBeforeFromString(String s){
		String cur=getCurrency(s);
		if (!cur.isEmpty()) {
			this.setAccountStateCurrency(cur);
			hasAccountStateCurrency=true;
		}
		setAccountStateBefore(convertFromStringToCurrency(s));
	}
	public void setAccountStateAfterFromString(String s){
		String cur=getCurrency(s);
		if (!cur.isEmpty()) {
			this.setAccountStateCurrency(cur);
			hasAccountStateCurrency=true;
		}		
		setAccountStateAfter(convertFromStringToCurrency(s));
	}
	public void setAccountDifferencePlusFromString(String s){				
		String cur=getCurrency(s);
		if (!cur.isEmpty()) {
			this.setAccountDifferenceCurrency(cur);
			hasAccountDifferenceCurrency=true;
		}
		setAccountDifferencePlus(convertFromStringToCurrency(s));
	}
	public void setAccountDifferenceMinusFromString(String s){
		String cur=getCurrency(s);
		if (!cur.isEmpty()) {
			this.setAccountDifferenceCurrency(cur);
			hasAccountDifferenceCurrency=true;
		}
		setAccountDifferenceMinus(convertFromStringToCurrency(s));
	}
}
