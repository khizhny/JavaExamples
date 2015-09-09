package com.khizhny.ukrsibbanking;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class BankListActivity extends AppCompatActivity{
	private ListView listView;
	private List<cBanks> bankList;
	private BankListAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {              
	    		cBanks bank = (cBanks)listView.getItemAtPosition(position);
	    		Toast.makeText(BankListActivity.this, bank.getName()+" is set as active", Toast.LENGTH_SHORT).show();
	    		DataBaseHelper myDb = new DataBaseHelper(BankListActivity.this);
	        	myDb.openDataBase();
	        	myDb.setActiveBank(bank.getId());
	        	myDb.close();
	        	bankList.clear();
	        	bankList.addAll(myDb.getAllBanks());
	        	adapter.notifyDataSetChanged();
            }
       }); 
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        bankList=new ArrayList<cBanks>();
    	DataBaseHelper myDbHelper = new DataBaseHelper(this);
	 	try {	 
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){throw sqle;}
	 	bankList=myDbHelper.getAllBanks();
	 	adapter  = new BankListAdapter(this, bankList);
		listView.setAdapter(adapter);		
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu if Munu button pressed
		getMenuInflater().inflate(R.menu.banks, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handling Menu item Clicks
		switch (item.getItemId()) {
		case R.id.bank_add:
			Toast.makeText(this, "Addind banks not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.bank_edit:
			Toast.makeText(this, "Phone  editing not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.bank_delete:
			Toast.makeText(this, "Deleting banks not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
}
