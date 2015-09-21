package com.khizhny.ukrsibbanking;

public class SubRule {
	private int id;
	private int ruleId;
	private int distanceToLeftPhrase;
	private int distanceToRightPhrase;
	private String leftPhrase;
	private String rightPhrase;
	private String constantValue;
	private int extractedParameter;
	private int extractionMethod;
	private int decimalSeparator;
	private String separator;
	private int trimLeft;
	private int trimRight;
	
	SubRule(int ruleId){
		this.ruleId=ruleId;
		this.id=-1;
		distanceToLeftPhrase=1;
		distanceToRightPhrase=1;
		extractedParameter=0;
		extractionMethod=0;
		decimalSeparator=0;
		separator=".";
		trimLeft=0;
		trimRight=0;
		constantValue="0";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getDistanceToLeftPhrase() {
		return distanceToLeftPhrase;
	}
	public void setDistanceToLeftPhrase(int distanceToLeftPhrase) {
		this.distanceToLeftPhrase = distanceToLeftPhrase;
	}
	public int getDistanceToRightPhrase() {
		return distanceToRightPhrase;
	}
	public void setDistanceToRightPhrase(int distanceToRightPhrase) {
		this.distanceToRightPhrase = distanceToRightPhrase;
	}
	public String getLeftPhrase() {
		return leftPhrase;
	}
	public void setLeftPhrase(String leftPhrase) {
		this.leftPhrase = leftPhrase;
	}
	public String getRightPhrase() {
		return rightPhrase;
	}
	public void setRightPhrase(String rightPhrase) {
		this.rightPhrase = rightPhrase;
	}
	public int getExtractionMethod() {
		return extractionMethod;
	}
	public void setExtractionMethod(int extractionMethod) {
		this.extractionMethod = extractionMethod;
	}
	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}
	public int getExtractedParameter() {
		return extractedParameter;
	}
	public void setExtractedParameter(int extractedParameter) {
		this.extractedParameter = extractedParameter;
	}
	public int getRuleId() {
		return ruleId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public int getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(int decimalSeparator) {
		if (decimalSeparator==0) {
			separator=".";
		} else
		{
			separator=",";
		}
		this.decimalSeparator = decimalSeparator;
	}

	public int getTrimLeft() {
		return trimLeft;
	}

	public void setTrimLeft(int trimLeft) {
		this.trimLeft = trimLeft;
	}

	public int getTrimRight() {
		return trimRight;
	}

	public void setTrimRight(int trimRight) {
		this.trimRight = trimRight;
	}
	
	public String applySubRule(String msg, boolean isText){
		msg="<BEGIN> "+msg+" <END>";
		String temp="";
		try{
			switch (extractionMethod) {
			case 0:  // n-th word after Left Phrase
				temp= msg.split(leftPhrase)[1].split(" ")[distanceToLeftPhrase];
				break;
			case 1: // n-th word before Right Phrase
				temp = msg.split(rightPhrase)[0];
				String[] arr=temp.split(" ");
				int wordsCount = arr.length; 
				temp=(arr[wordsCount-distanceToRightPhrase]);
				break;
			case 2:// all words between Phrases
				temp=msg.split(leftPhrase)[1].split(rightPhrase)[0];
				break;
			case 3: // const
				temp=constantValue;
			}
		}
		catch (Exception e){
			 temp="error";
		}
		// trimming characters if needed
		if (trimRight>0 || trimLeft>0){
			int temp_len=temp.length();
			temp=temp.substring(trimLeft, temp_len-trimRight-1);
		}	
		
		if (isText) {  // return only text
			return temp.replaceAll("[^A-Za-z]", "");
		} else
		{ // return only Numbers
			try {
			return temp.replaceAll("[^0-9"+separator+"]", "");
			}catch (Exception e){
				return "0";
			}
		}			
	}
}
