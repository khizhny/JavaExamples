package com.khizhny.ukrsibbanking;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
     //private static String DB_PATH = "/data/data/com.khizhny.ukrsibbanking/databases/";
     private static  String DB_PATH;
     private static String DB_NAME = "database.db";
  // increment dbLastVersion if db structure is changing. Also increment  "version.version" field in DB.
     private static int dbLastVersion=3;  
     
     private SQLiteDatabase myDataBase; 
     private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
     	super(context, DB_NAME, null, 1);
        this.myContext = context;        
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        PackageInfo p=null;
		try {
			p = m.getPackageInfo(s, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        s = p.applicationInfo.dataDir;   
        DB_PATH = s+"/databases/";
    }
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
     	boolean dbExist = checkDataBase();
     	if(dbExist){
    		//do nothing - database already exist
    	}else{
     		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
         	try {
     			copyDataBase();
     		} catch (IOException e) {
         		throw new Error("Error copying database");
         	}
    	}
     }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	int dbVersion=0;
    	SQLiteDatabase checkDB = null;
    	//String DB_PATH = Context.getFilesDir().getPath();
    	try{
    		String myPath = DB_PATH + DB_NAME;	
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
   		 	// if db version changed recopy is from assets
	       // SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = checkDB.rawQuery("SELECT version FROM version", null);
    		cursor.moveToFirst();
    		dbVersion= Integer.parseInt(cursor.getString(0));
     	}catch(SQLiteException e){
     		//database does't exist yet.
     	}
     	if(checkDB != null){
     		//database exists
     		checkDB.close();
     	}
     	if (dbLastVersion!=dbVersion) {
     		checkDB=null;
     	}
     	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close(); 
    }
 
    public void openDataBase() throws SQLException{
     	//Open the database
        String myPath = DB_PATH + DB_NAME;
        try {
        	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } 
        catch(SQLException sqle)
        {
        	throw sqle;
        }
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close(); 
    	    super.close(); 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	//===============================custom methods===========================
	public List<Bank> getAllBanks () {
		 List<Bank> bankList = new ArrayList<Bank>();
	        // Select All Query
		 String selectQuery = "SELECT _id, name, phone, active, default_currency FROM banks";
	 
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	 
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                Bank bank = new Bank();
	                bank.setId(Integer.parseInt(cursor.getString(0)));
	                bank.setName(cursor.getString(1));
	                bank.setPhone(cursor.getString(2));
	                bank.setActive(Integer.parseInt(cursor.getString(3)));
	                bank.setDefaultCurrency(cursor.getString(4));
	                // Adding contact to list
	                bankList.add(bank);
	            } while (cursor.moveToNext());
	        }
	        return bankList;
    }
	public void setActiveBank (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE banks SET active=0");
        db.execSQL("UPDATE banks SET active=1 WHERE _id="+id);
   }
public void setActiveAnyBank () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE banks SET active=0");
        db.execSQL("UPDATE banks SET active=1 WHERE _id=(SELECT MAX(_id) FROM banks)");
   }
	public String getActiveBankPhone () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone FROM banks WHERE active=1", null);
        String phone="";
        if (cursor.moveToFirst()) {
        	phone=cursor.getString(0);
        }
        return phone;
   }
	public String getActiveBankCurrency () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT default_currency FROM banks WHERE active=1", null);
        String rez="";
        if (cursor.moveToFirst()) {
        	rez=cursor.getString(0);
        }
        return rez;
   }
	public Bank getActiveBank () {
		Bank b = new Bank();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, name, phone, active, default_currency FROM banks WHERE active=1", null);
        if (cursor.moveToFirst()) {
        	b.setId(cursor.getInt(0));
        	b.setName(cursor.getString(1));
        	b.setPhone(cursor.getString(2));
        	b.setActive(cursor.getInt(3));
        	b.setDefaultCurrency(cursor.getString(4));
        }
        return b;
	}
	public void deleteActiveBank () {
        SQLiteDatabase db = this.getWritableDatabase();
        // deleting all subrules and rules of active bank
        db.execSQL("DELETE FROM subrules WHERE rule_id=(SELECT _id FROM rules WHERE bank_id=(SELECT _id FROM banks WHERE active=1))");
        db.execSQL("DELETE FROM rules WHERE bank_id=(SELECT _id FROM banks WHERE active=1)");
        db.execSQL("DELETE FROM banks WHERE active=1");
   }
	public void addOrEditBank (Bank b) {
        SQLiteDatabase db = this.getWritableDatabase();
		if (b.getId()<0){
			// Adding new bank
			String name = b.getName();
			String phone = b.getPhone();
			String default_currency = b.getDefaultCurrency();
			db.execSQL("UPDATE banks SET active=0");
			db.execSQL("INSERT INTO banks (name, phone, active, default_currency) VALUES('"+name+"','"+phone+"',1,'"+default_currency+"')");
		}else
		{	// Updating bank info
			int id = b.getId();
			String name = b.getName();
			String phone = b.getPhone();
			String default_currency = b.getDefaultCurrency();
			db.execSQL("UPDATE banks SET name='"+name+"', phone='"+phone+"', default_currency='"+default_currency+"' WHERE _id="+id);
		}
	}
	public List<Rule> getAllRules(){
		 List<Rule> ruleList = new ArrayList<Rule>();
		 String selectQuery = "SELECT _id, name, sms_body, mask, selected_words, bank_id, type FROM rules WHERE bank_id=(SELECT _id FROM banks WHERE active=1)";
		 SQLiteDatabase db = this.getWritableDatabase();
	     Cursor cursor = db.rawQuery(selectQuery, null);
	     // looping through all rows and adding to list
	     if (cursor.moveToFirst()) {
	     	do {
                Rule r = new Rule(cursor.getInt(5),cursor.getString(1));
                r.setId(Integer.parseInt(cursor.getString(0)));
                r.setSmsBody(cursor.getString(2));
                r.setMask(cursor.getString(3));
                r.setSelectedWords(cursor.getString(4));
                r.setRuleType(cursor.getInt(6));
                // Adding contact to list
                ruleList.add(r);
            } while (cursor.moveToNext());
        }
        return ruleList;
	}
	public int addOrEditRule(Rule r){
		 SQLiteDatabase db = this.getWritableDatabase();
			String name = r.getName();
			String sms_body = r.getSmsBody();
			String mask = r.getMask();
			String selectedWords=r.getSelectedWords();
			int bankId = r.getBankId();
			int type = r.getRuleType();
			int id = r.getId();
			if (id<0){
				// Adding new Rule
				db.execSQL("INSERT INTO rules (name, sms_body, mask, selected_words, bank_id, type) VALUES('"+name+"','"+sms_body+"','"+mask+"','"+selectedWords+"',"+bankId+","+type+")");
				Cursor c=db.rawQuery("SELECT MAX(_id) FROM rules",null);
				if (c.moveToFirst()) {
					id= c.getInt(0);
				}
			}else
			{	// Updating Rule info
				db.execSQL("UPDATE rules SET name='"+name+"', sms_body='"+sms_body+"', mask='"+mask+"', selected_words='"+selectedWords+"',bank_id="+bankId+",type="+type+" WHERE _id="+id);
			}
			return id;
	}
	public Rule getRule(int ruleId){
		 String selectQuery = "SELECT _id, name, sms_body, mask, selected_words, bank_id, type FROM rules WHERE _id="+ruleId;
		 SQLiteDatabase db = this.getWritableDatabase();
		 Cursor cursor = db.rawQuery(selectQuery, null);
		 Rule r;
		 if (cursor.moveToFirst()) {
			 r = new Rule(cursor.getInt(5),cursor.getString(1));
	         r.setId(cursor.getInt(0));
	         r.setSmsBody(cursor.getString(2));
	         r.setMask(cursor.getString(3));
	         r.setSelectedWords(cursor.getString(4));
	         r.setRuleType(cursor.getInt(6));
		 }
		 else {
			 Log.e("DatabaseHelper.getRule", "rule id duplicated or not found.");
			 return null;
		 }
         return r;
	}
	public void deleteRule (int ruleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // deleting all subrules and rule
        db.execSQL("DELETE FROM subrules WHERE rule_id="+ruleId);
        db.execSQL("DELETE FROM rules WHERE _id="+ruleId);
   }
	public List<SubRule> getSubRules(int ruleId){
		 List<SubRule> subRuleList = new ArrayList<SubRule>();
		 String selectQuery = "SELECT _id, left_phrase,right_phrase, distance_to_left_phrase, distance_to_right_phrase, constant_value, extracted_parameter,extraction_method,decimal_separator,trim_left,trim_right  FROM subrules WHERE rule_id="+ruleId;
		 SQLiteDatabase db = this.getWritableDatabase();
	     Cursor cursor = db.rawQuery(selectQuery, null);
	     // looping through all rows and adding to list
	     if (cursor.moveToFirst()) {
	     	do {
               SubRule r = new SubRule(ruleId);
               r.setId(Integer.parseInt(cursor.getString(0)));
               r.setLeftPhrase(cursor.getString(1));
               r.setRightPhrase(cursor.getString(2));
               r.setDistanceToLeftPhrase(cursor.getInt(3));
               r.setDistanceToRightPhrase(cursor.getInt(4));
               r.setConstantValue(cursor.getString(5));
               r.setExtractedParameter(cursor.getInt(6));
               r.setExtractionMethod(cursor.getInt(7));
               r.setDecimalSeparator(cursor.getInt(8));
               r.setTrimLeft(cursor.getInt(9));
               r.setTrimRight(cursor.getInt(10));
               // Adding contact to list
               subRuleList.add(r);
           } while (cursor.moveToNext());
       }
       return subRuleList;
	}
	public int addOrEditSubRule(SubRule sr){
		SQLiteDatabase db = this.getWritableDatabase();
		int id = sr.getId();
		int ruleId=sr.getRuleId();
		String LeftPhrase=sr.getLeftPhrase();
		String RightPhrase=sr.getRightPhrase();
		int leftN = sr.getDistanceToLeftPhrase();
		int rightN = sr.getDistanceToRightPhrase();
		String constanValue=sr.getConstantValue();
		int extractionParameter=sr.getExtractedParameter();
		int extractionMethod=sr.getExtractionMethod();
		int decimalSeparator = sr.getDecimalSeparator();
		int trimLeft = sr.getTrimLeft();
		int trimRight = sr.getTrimRight();
		
		if (id<0){
			// Adding new SubRule
			db.execSQL("INSERT INTO subrules (rule_id,left_phrase,right_phrase,distance_to_left_phrase,distance_to_right_phrase,constant_value,extracted_parameter,extraction_method,decimal_separator,trim_left,trim_right) "
					+ "VALUES("+ruleId+",'"+LeftPhrase+"','"+RightPhrase+"',"+leftN+","+rightN+",'"+constanValue+"',"+extractionParameter+","+extractionMethod+","+decimalSeparator+","+trimLeft+","+trimRight+")");
			Cursor c=db.rawQuery("SELECT MAX(_id) FROM subrules",null);
			if (c.moveToFirst()) {
				id= c.getInt(0);
			}
		}else
		{	// Updating Rule info
			db.execSQL("UPDATE subrules SET "+
					"rule_id="+ruleId+","
					+"left_phrase='"+LeftPhrase+"',"
					+"right_phrase='"+	RightPhrase+"',"
					+"distance_to_left_phrase="+leftN+","
					+"distance_to_right_phrase="+rightN+","
					+"constant_value='"+constanValue+"',"
					+"extracted_parameter="+extractionParameter+","
					+"extraction_method="+extractionMethod+","
					+"decimal_separator="+decimalSeparator+","
					+"trim_left="+trimLeft+","
					+"trim_right="+trimRight
					+" WHERE _id="+id);
		}
		return id;
	}
	public void deleteSubRule (int subRuleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM subrules WHERE _id="+subRuleId);
   }
}