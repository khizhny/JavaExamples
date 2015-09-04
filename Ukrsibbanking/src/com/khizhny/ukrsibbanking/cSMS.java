package com.khizhny.ukrsibbanking;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class cSMS implements Comparable<cSMS> {
	 @Override  
	  public int compareTo(cSMS o) {
	    int rez=0;
		try{rez=o.getTransanctionDate().compareTo(getTransanctionDate());}
	    catch (Exception e)  { }
		 return rez;
	  }
	// Конструктор класса. Задаем дефолтные значения
	cSMS(){
		this.transanctionType=R.drawable.ic_transanction_unknown;
		this.currencyRate=new BigDecimal(1);
		this.currencyRate.setScale(3, RoundingMode.HALF_UP);
		}
	// Сведения о транзакнции
	public int transanctionType;
	private String body;
	private String number;	
	private Date transanctionDate;
	private String accountNumber;
	private String cardNumber;
	public String accountStateCurrency;
	private String accountDifferenceCurrency;
	private BigDecimal accountStateBefore;
	private BigDecimal accountStateAfter;
	private BigDecimal accountDifferencePlus;
	private BigDecimal accountDifferenceMinus;
	private BigDecimal currencyRate;
	
	public boolean hasTransanctionDate=false;
	public boolean hasAccountNumber=false;
	public boolean hasCardNumber=false;
	public boolean hasAccountStateCurrency=false;
	public boolean hasAccountDifferenceCurrency=false;
	public boolean hasAccountStateBefore=false;
	public boolean hasAccountStateAfter=false;
	public boolean hasAccountDifference=false;

	
	//=======================================================GETs============================
	private String getCurrency(String s){return  s.replaceAll("[^A-Za-z]","");}
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
			rez="0.00"+accountDifferencePlus.toString();
			break;
		case 1: 
			rez="+"+accountDifferencePlus.toString()+accountDifferenceCurrency;
			break;
		}
		if (!currencyRate.equals(new BigDecimal(1))) {
					rez+="\n("+currencyRate.multiply(accountDifferencePlus,  MathContext.DECIMAL32).setScale(2)+accountStateCurrency+")";
					rez+="\n(rate "+currencyRate.toString()+")";
		}
		return rez;
	}
	public String getAccountStateBeforeAsString(){return accountStateBefore.toString()+accountStateCurrency;}
	public String getAccountStateAfterAsString(){return accountStateAfter.toString()+accountStateCurrency;}
	
	// методы ввода сведений о транзакнции
	public void setCurrencyRate(BigDecimal currencyRate){this.currencyRate=currencyRate;}
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
			if (accountDifferenceCurrency.equals(accountDifferenceCurrency)) {
				this.accountStateAfter = this.accountStateBefore.add(this.accountDifferencePlus);
				hasAccountStateAfter=true;
			}
		}
		if (hasAccountStateAfter && !hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountDifferenceCurrency)) {
				this.accountDifferencePlus=this.accountStateAfter.subtract(this.accountStateBefore);
				this.accountDifferenceMinus= accountDifferencePlus.negate();
				hasAccountDifference=true;
			}
		}
	}	
	public void setAccountStateAfter(BigDecimal accountStateAfter) {
		this.accountStateAfter = accountStateAfter;
		hasAccountStateAfter=true;
		if (hasAccountDifference && !hasAccountStateBefore) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				this.accountStateBefore = accountStateAfter.subtract(this.accountDifferencePlus);
				hasAccountStateBefore=true;
			}
		}
		if (hasAccountStateBefore && !hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				this.accountDifferencePlus=this.accountStateAfter.subtract(this.accountStateBefore);
				this.accountDifferenceMinus= this.accountDifferencePlus.negate();
				hasAccountDifference=true;
			}
		}
	}	
	public void setAccountDifferencePlus(BigDecimal accountDifferencePlus) {
		this.accountDifferencePlus = accountDifferencePlus;
		this.accountDifferenceMinus = accountDifferencePlus.negate();
		hasAccountDifference=true;
		if (!hasAccountStateAfter && hasAccountStateBefore) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				this.accountStateAfter=this.accountStateBefore.add(this.accountDifferencePlus);
				hasAccountStateAfter=true;
			}
		}
		if (hasAccountStateAfter && !hasAccountStateBefore) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				this.accountStateBefore=this.accountStateAfter.subtract(this.accountDifferencePlus);
				hasAccountStateBefore=true;
			}
		}
	}
	public void setAccountDifferenceMinus(BigDecimal accountDifferenceMinus) {
		setAccountDifferencePlus(accountDifferenceMinus.negate());		
	}
	public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
	}

	public String getWordBetween(String splittingWord1, String splittingWord2) {
		return body.split(splittingWord1)[1].split(splittingWord2)[0];
	}
	public String getWordAfter( String splittingWord1, String wordSplitter) {
		return body.split(splittingWord1)[1].split(wordSplitter)[0];
	}
	public String getWordBefore(String searchedWord, String wordSplitter,  int wordCount) {
		String temp = body.split(searchedWord)[0];
		String[] arr=temp.split(wordSplitter);
		int wordsCount = arr.length; 
		return (arr[wordsCount-2]+wordSplitter+arr[wordsCount-1]);
	}
	public void setAccountStateBeforeFromString(String s){
		String cur=getCurrency(s);
		if (cur.isEmpty()) {
			cur=accountStateCurrency;
		}
		setAccountStateCurrency(cur);
		hasAccountStateCurrency=true;
		setAccountStateBefore(extractValue(s));
	}
	public void setAccountStateAfterFromString(String s){
		String cur=getCurrency(s);
		if (cur.isEmpty()) {
			cur=accountStateCurrency;
		}		
		setAccountStateCurrency(cur);
		hasAccountStateCurrency=true;
		setAccountStateAfter(extractValue(s));
	}
	public void setAccountDifferencePlusFromString(String s){				
		String cur=getCurrency(s);
		if (cur.isEmpty()) {
			cur=accountStateCurrency;
		}
		setAccountDifferenceCurrency(cur);
		hasAccountDifferenceCurrency=true;
		setAccountDifferencePlus(extractValue(s));
	}
	public void setAccountDifferenceMinusFromString(String s){
		String cur=getCurrency(s);
		if (cur.isEmpty()) {
			cur=accountStateCurrency;
		}
		setAccountDifferenceCurrency(cur);
		hasAccountDifferenceCurrency=true;
		setAccountDifferenceMinus(extractValue(s));
	}
	public BigDecimal extractValue(String s){
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
}
