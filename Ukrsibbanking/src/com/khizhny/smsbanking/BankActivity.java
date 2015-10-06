package com.khizhny.smsbanking;

import java.io.IOException;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BankActivity extends AppCompatActivity {
	private Bank bank;
	private String todo;
	private TextView nameView;
	private TextView phoneView;
	private Spinner currencyView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank);
		
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        // getting Bank id passed with Intent
        DataBaseHelper myDb = new DataBaseHelper(this);
	    try { // IF DB doesn't exists or its version is outdated copy it from assets.
		       myDb.createDataBase();
		 	} catch (IOException ioe) {
		 		throw new Error("Unable to create database");
		 	}
	 	try {	 
	 		myDb.openDataBase();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}
	 	bank=new Bank();
        todo = getIntent().getExtras().getString("todo"); 
	 	nameView = (TextView) findViewById(R.id.name);
	 	phoneView = (TextView) findViewById(R.id.phone);
	 	currencyView = (Spinner) findViewById(R.id.currency);
	 	if (todo.equals("edit")) {
			// filling bank info from DB
		 	bank=myDb.getActiveBank();
		 	nameView.setText(bank.getName());	
		 	phoneView.setText(bank.getPhone());
		 	currencyView.setSelection(getIndex(currencyView, bank.getDefaultCurrency()));
        }
        myDb.close();
        
        Button saveView = (Button) findViewById(R.id.sub_rule_save);
        saveView.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 Toast.makeText(v.getContext(), "Saving changes...", Toast.LENGTH_SHORT).show();
	        	 DataBaseHelper myDb = new DataBaseHelper(v.getContext());
	        	 bank.setName(nameView.getText().toString());
	        	 bank.setPhone(phoneView.getText().toString());
	        	 bank.setDefaultCurrency(currencyView.getSelectedItem().toString().replace("\n", ""));
	        	 myDb.addOrEditBank(bank);
	        	 myDb.close();
	        	 BankActivity.this.finish();
	         }
   	   });
    }

	private int getIndex(Spinner spinner, String myString)
	{
	 int index = 0;
	
	 for (int i=0;i<spinner.getCount();i++){
	  if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
	   index = i;
	   break;
	  }
	 }
	 return index;
	}
}