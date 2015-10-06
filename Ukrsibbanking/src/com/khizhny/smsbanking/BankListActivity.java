package com.khizhny.smsbanking;
import android.content.Intent;
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
	private List<Bank> bankList;
	private BankListAdapter adapter;
	private int selected_row;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_list);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {              
            	// Marking selected bank as Active in DB
            	selected_row=position;
            	Bank bank = (Bank)listView.getItemAtPosition(position);
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
        bankList=new ArrayList<Bank>();
    	DataBaseHelper myDbHelper = new DataBaseHelper(this);
	 	try {	 
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){throw sqle;}
	 	bankList=myDbHelper.getAllBanks();
	 	for (int i=0;i<bankList.size();i++) {
	 		if (bankList.get(i).isActive()) {
	 			selected_row=i;
	 		}
	 	}
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
		Intent intent = new Intent(this, BankActivity.class);
		switch (item.getItemId()) {
		case R.id.bank_add:
			// Calligng Bank Activity to add new Bank
			intent.putExtra("todo", "add");
		    startActivity(intent);
		    adapter.notifyDataSetChanged();
			return true;
		case R.id.bank_edit:
			intent.putExtra("todo", "edit");
		    startActivity(intent);
		    adapter.notifyDataSetChanged();
			return true;
		case R.id.bank_delete:
			DataBaseHelper db = new DataBaseHelper(this);
		 	try {	 
		 		db.openDataBase();
		 	} catch(SQLException sqle){throw sqle;}
			 db.deleteActiveBank();
			 db.setActiveAnyBank();
			 db.close();
			 bankList.remove(selected_row);
			 adapter.notifyDataSetChanged();
		    return true;
		}
		return false;
	}
}
