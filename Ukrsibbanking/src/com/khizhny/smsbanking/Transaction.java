package com.khizhny.smsbanking;

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
	private BigDecimal accountDifference;
	private BigDecimal currencyRate;
	private BigDecimal comission;
	public boolean hasAccountStateCurrency=false;
	public boolean hasAccountDifferenceCurrency=false;
	public boolean hasAccountStateBefore=false;
	public boolean hasAccountStateAfter=false;
	public boolean hasAccountDifference=false;
	public boolean hasTransanctionDate=false;
	
	//=======================================================GETs============================
	//private String getCurrency(String s){return  s.replaceAll("[^A-Za-z]","");}
	public String getBody() {return body;}	
	public String getNumber() {return number;}
	public Date getTransanctionDate() {return transanctionDate;}
	public String getAccountStateCurrency(){return accountStateCurrency;}
	public BigDecimal getAccountStateBefore(){return accountStateBefore;}
	public BigDecimal getAccountStateAfter(){return accountStateAfter;}
	public String getAccountDifferenceCurrency(){return accountDifferenceCurrency;}

	public BigDecimal getCurrencyRate(){return currencyRate;}
	public String getTransanctionDateAsString(String transanctionDateFormat) {
		DateFormat f = new SimpleDateFormat(transanctionDateFormat, Locale.ENGLISH);
		return f.format(transanctionDate);		
	}
	
	public BigDecimal getAccountDifference(){
		return accountDifference;
		}
	public String getAccountDifferenceAsString(boolean hideCurrency,boolean inverseRate){
		// function forms a string that will represent trnsaction difference on screen
		String rez="";
		if (accountStateCurrency.equals(accountDifferenceCurrency)){
			// if transaction has native currency
			if (accountDifference.subtract(comission).signum()==1) {
				rez+="+";
			}
			rez+=accountDifference.subtract(comission).toString();		
			if (!hideCurrency) {
				rez+=accountDifferenceCurrency;
			}	
		}else
		{   // if transaction has foreign currency
			BigDecimal calculated_price = currencyRate.multiply(accountDifference,  MathContext.UNLIMITED).setScale(2, RoundingMode.HALF_UP);
			if (accountDifference.signum()==1) {
				rez+="+";
			}
			rez+=accountDifference.toString()+accountDifferenceCurrency;
			rez+="\n("+calculated_price;
			if (!hideCurrency) {
				rez+=accountStateCurrency;
			}
			rez+=")";
			if (!inverseRate){
				rez+="\n(rate "+currencyRate.toString()+")";
			}else{
				rez+="\n(rate "+(new BigDecimal(1).setScale(3)).divide(currencyRate,RoundingMode.HALF_UP).toString()+")";
			}
			
		}
		return rez;
	}
	public String getAccountStateBeforeAsString(boolean hideCurrency){
		if (!hideCurrency) {
			return accountStateBefore.toString()+accountStateCurrency;
		}else{
			return accountStateBefore.toString();
		}
	}
	public String getAccountStateAfterAsString(boolean hideCurrency){
		if (!hideCurrency) {
			return accountStateAfter.toString()+accountStateCurrency;
		}else{
			return accountStateAfter.toString();
		}		
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
		hasTransanctionDate=true;
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
	public void setAccountDifference(BigDecimal accountDifference) {
		this.accountDifference = accountDifference;
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
	public void setAccountDifferenceFromString(String s){				
		try{
		setAccountDifference(new BigDecimal(s.replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_UP));
		}catch (Exception e) {}
	}
	//=================================================Comission=============================
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
	public String getComissionAsString(boolean hideCurrency){
		if (comission.signum()!=0) 
		{
			if (!hideCurrency) {
				return comission.toString()+accountStateCurrency;
			}else{
				return comission.toString();
			}
		}else{
			return "";
		}
		
	}
	
	public void calculateMissedData(){
		// Calculating accountStateAfter if possible
		if (!hasAccountStateAfter && hasAccountStateBefore && hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountStateAfter=accountStateBefore.add(accountDifference).subtract(comission);
				hasAccountStateAfter=true;
			}
		}
		// Calculating accountStateBefore if possible
		if (!hasAccountStateBefore && hasAccountStateAfter && hasAccountDifference) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountStateBefore=accountStateAfter.subtract(accountDifference).add(comission);
				hasAccountStateBefore=true;
			}
		}
		// Calculating account difference if possible
		if (!hasAccountDifference && hasAccountStateBefore && hasAccountStateAfter) {
			if (accountDifferenceCurrency.equals(accountStateCurrency)) {
				accountDifference=accountStateAfter.subtract(accountStateBefore).add(comission);
				hasAccountDifference=true;
			}
		}		
	}
}
