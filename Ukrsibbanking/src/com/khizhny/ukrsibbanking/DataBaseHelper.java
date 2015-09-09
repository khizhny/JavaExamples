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
public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
     //private static String DB_PATH = "/data/data/com.khizhny.ukrsibbanking/databases/";
     private static  String DB_PATH;
     private static String DB_NAME = "database.db";
  // increment dbLastVersion if db structure is changing. Also increment  "version.version" field in DB.
     private static int dbLastVersion=1;  
     
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
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
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
	public List<cBanks> getAllBanks () {
		 List<cBanks> bankList = new ArrayList<cBanks>();
	        // Select All Query
		 String selectQuery = "SELECT _id, name, phone, active, default_currency FROM banks";
	 
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	 
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                cBanks bank = new cBanks();
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
 
}