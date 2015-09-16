package com.khizhny.ukrsibbanking;

import java.math.BigDecimal;
import java.util.ArrayList;

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
	
	SubRule(int ruleId){
		this.ruleId=ruleId;
		this.id=-1;
		distanceToLeftPhrase=1;
		distanceToRightPhrase=1;
		extractedParameter=0;
		extractionMethod=0;
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
}
