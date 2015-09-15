package com.khizhny.ukrsibbanking;

import java.math.BigDecimal;

public class SubRule {
	private int id;
	private int ruleId;
	private int distanceToLeftPhrase;
	private int distanceToRightPhrase;
	private String leftPhrase;
	private String rightPhrase;
	private BigDecimal constantValue;
	private int extractedParameter;
	private final String[] extractedParameterDescription=
		{"Account state before transaction",
		 "Account state after transaction",
		 "Account difference (+)",
		 "Account difference (-)",
		 "Transaction commission",
		 "Transaction currency",
		 "Use constant Value"};
	private int extractionMethod;
	private final String[] extractionMethodDescription=
		{"n-th word after Left Phrase",
		 "n-th word before Right Phrase",
		 "all words between LeftPhrase and RightPhrase",
		 "Use constant Value"};
	
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
	public BigDecimal getConstantValue() {
		return constantValue;
	}
	public void setConstantValue(BigDecimal constantValue) {
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
