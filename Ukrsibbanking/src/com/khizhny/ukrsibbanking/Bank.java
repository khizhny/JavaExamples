package com.khizhny.ukrsibbanking;


public class Bank {
	// Конструктор класса. Задаем дефолтные значения
	Bank(){
		id=-1;
	}
	// Information about your bank account
	private int id;
	private int active;  // indicates that user want to watch this account info in program.
	private String name; 
	private String phone;
	private String dafaultCurrency;

	//=======================================================GETs============================
	public int getId() {return id;}
	public int getActive() {return active;}
	public String getName() {return name;}
	public String getPhone() {return phone;}
	public String getDefaultCurrency() {return dafaultCurrency;}
	//=======================================================SETs============================
	public  void setId(int id){this.id=id;}
	public  void setActive(int active){this.active=active;}
	public  void setName(String name){this.name=name.replaceAll("'","");}
	public  void setPhone(String phone){this.phone=phone.replace("'", "");}
	public  void setDefaultCurrency(String dafaultCurrency){this.dafaultCurrency=dafaultCurrency;}
	//=======================================================Functions=======================
	public boolean isActive() {
		if (active!=0) {return true;}
		else {return false;}}

	public boolean checkPhone(String phone){
		String[] phones=this.phone.split(";");
		boolean rez=false;
		for (int i=0;i<phones.length-1;i++){
			if (phone.equals(phones[i])){				
				rez=true;
			}
		}
		return rez;
	}
}
