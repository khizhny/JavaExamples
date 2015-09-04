package com.khizhny.ukrsibbanking;

import android.app.ListActivity;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class BankListActivity extends ListActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
    @Override
    protected void onResume() {
        super.onResume();
        List<cBanks> bankList= new ArrayList<cBanks>();
	 	// Restore bank list from DB
    	DataBaseHelper myDbHelper = new DataBaseHelper(this);
	 	try {	 
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){throw sqle;}
	 	bankList=myDbHelper.getAllBanks();
		setListAdapter(new BankListAdapter(this, bankList));
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
		case R.id.action_bank_list_add:
			Toast.makeText(this, "Addind banks not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.bank_phone_edit:
			Toast.makeText(this, "Phone  editing not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.bank_delete:
			Toast.makeText(this, "Deleting banks not yet ready", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
		//return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		cBanks bank = (cBanks)getListAdapter().getItem(position);
		/*int j=getListAdapter().getCount();
		RadioButton RadioButtonView;
		for (int  i=0; i<j;i++){
			RadioButtonView = (RadioButton) l.getAdapter().getView(i, null, l).findViewById(R.id.active);
			//RadioButtonView = (RadioButton) l.getRootView().findViewById(R.id.active);
			if (i==position) {
				RadioButtonView.setChecked(false);
			}
			else
			{
				RadioButtonView.setChecked(false);	
			}
		}/**/
		Toast.makeText(this, bank.getName()+" is set as active", Toast.LENGTH_SHORT).show();
		DataBaseHelper myDb = new DataBaseHelper(this);
    	myDb.openDataBase();
    	myDb.setActiveBank(bank.getId());
    	myDb.close();
    	this.onResume();
		//setListAdapter(new BankListAdapter(this, bankList));
	}
}
