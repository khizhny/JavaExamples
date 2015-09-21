package com.khizhny.ukrsibbanking;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	public static int[] ruleTypes={
			R.drawable.ic_transanction_unknown,
			R.drawable.ic_transanction_plus,
			R.drawable.ic_transanction_minus,
			R.drawable.ic_transanction_transfer_to,
			R.drawable.ic_transanction_transfer_from,
			R.drawable.ic_transanction_pay,
			R.drawable.ic_transanction_failed,
			R.drawable.ic_transanction_missed
			};
	private int id;
	private int bankId;
	private String name;
	private String smsBody;
	private String mask;
	public int wordsCount;
	private int ruleType;
	public boolean[] wordIsSelected; 
	public List<SubRule> subRuleList;
	
	Rule(int bankId, String name){
		this.id=-1;
		this.name=name;
		this.bankId=bankId;
		wordsCount=0;
//		subRules = new ArrayList <SubRule>();
		setRuleType(0);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBankId() {
		return bankId;
	}
	public void setBankId(int bankId) {
		this.bankId = bankId;
	}
	public String getSmsBody() {
		return smsBody;
	}
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
		wordsCount=smsBody.split(" ").length;
		wordIsSelected =new boolean[wordsCount+2]; // adding 2 words for <BEGIN> and <END>
		for (int i=1; i<=wordsCount;i++) {
			wordIsSelected[i]=false;
		}
		wordIsSelected[0]=true; // words <BEGIN> and <END> is always selected
		wordIsSelected[wordsCount+1]=true;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSelectedWords() {
		String r="";
		String delimiter="";
		for (int i=0; i<=wordsCount+1;i++) {
			if (wordIsSelected[i]) {
				r+=delimiter+i;
				delimiter=",";
			}
		}
		return r;
	}
	public void setSelectedWords(String selectedWords) {
		// function sets selected words flags from string.
		String[] a = selectedWords.split(",");
		int selectedWordsCount = a.length;
		// setting all to false
		for (int i=0; i<=wordsCount+1;i++) {
			wordIsSelected[i]=false;
		}
		// setting selected to true
		int k;
		for (int i=0; i<selectedWordsCount;i++) {
			k=Integer.parseInt(a[i]);
			wordIsSelected[k]=true;
		}
		updateMask();
	}
	public void selectWord(int WordIndex){
		wordIsSelected[WordIndex]=true;
		updateMask();
	}
	public void deSelectWord(int WordIndex){
		wordIsSelected[WordIndex]=false;
		updateMask();
	}
	private void updateMask(){
		mask="";
		String delimiter="";
		String[] words=smsBody.split(" ");
		boolean skip_wildcard=false;
		for (int i=1; i<=wordsCount; i++){
			if (wordIsSelected[i]){
				mask+=delimiter+"\\Q"+words[i-1]+"\\E";
				skip_wildcard=false;
			}else{
				if (!skip_wildcard) {
					mask+=delimiter+".*";
					skip_wildcard=true;
				}				
			}
			delimiter=" ";
		}
	}
	public int getRuleType() {
		return ruleType;
	}
	public int getRuleTypeDrawable() {
		return ruleTypes[ruleType];
	}
	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
	}
	public List<String> getConstantPhrases()
	{
		List<String> out = new ArrayList<String>();
		out.add("<BEGIN>");
		int phraseCount=1;
		boolean startNewPhrase=true;
		String[] words=smsBody.split(" ");
		for (int i=1; i<=wordsCount; i++){
			if (wordIsSelected[i]){
				if (startNewPhrase) {
					phraseCount+=1;
					out.add(words[i-1]);
					startNewPhrase=false;
				} else
				{
					out.set(phraseCount-1, out.get(phraseCount-1)+" "+(words[i-1]));
				}
				
			}else{
				startNewPhrase=true;
			}
		}
		out.add("<END>");
		return out;
	}
}

