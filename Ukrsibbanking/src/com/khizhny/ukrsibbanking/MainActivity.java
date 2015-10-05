package com.khizhny.ukrsibbanking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.khizhny.ukrsibbanking.Transaction;
public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener{

	public List<Bank> bankList;
	public List<Transaction> transactionList;
	public List<Rule> ruleList;
	private String AccountCurrency;
	private String phoneNumber;
	private ListView listView;
	private String selectedSMS;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {              
      		Transaction sms = (Transaction)listView.getItemAtPosition(position);
    		//Toast.makeText(getApplicationContext(), sms.getBody(), Toast.LENGTH_LONG).show();
            selectedSMS=sms.getBody();
    		PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
    		popupMenu.setOnMenuItemClickListener(MainActivity.this);
    		popupMenu.inflate(R.menu.popup_menu);
    		popupMenu.show();
            }

       }); 
	}
	
    @Override
    protected void onResume() {
        super.onResume();
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
	 	// reading data from db
	 	bankList=myDb.getAllBanks();
	 	ruleList=myDb.getAllRules();
	 	int rule_count=ruleList.size();
	 	for (int i=0; i<rule_count;i++){
	 		ruleList.get(i).subRuleList=myDb.getSubRules(ruleList.get(i).getId());
	 	}
	 	phoneNumber= myDb.getActiveBankPhone();
	 	AccountCurrency=myDb.getActiveBankCurrency();
	 	myDb.close();
	 	
		// Restore preferences	     
	 	SharedPreferences settings =PreferenceManager.getDefaultSharedPreferences(this);
		//settings.getBoolean("hide_matched_messages",false);
		Boolean hideMatchedMessages= settings.getBoolean("hide_matched_messages",false);
		//settings.getBoolean("hide_not_matched_messages",false);
		Boolean hideNotMatchedMessages= settings.getBoolean("hide_not_matched_messages",false);
		//settings.getBoolean("hide_currency",false);
		Boolean hideCurrency= settings.getBoolean("hide_currency",false);
		//settings.getBoolean("inverse_rate",false);
		Boolean inverseRate= settings.getBoolean("inverse_rate",false);
		
	 	transactionList = new ArrayList<Transaction>();
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c= getContentResolver().query(uri, null, "address='" + phoneNumber+"'" ,null,"date DESC");
		//Cursor c= getContentResolver().query(uri, null, null,null,"date DESC");
		String sms_body;
		boolean ruleWasApplied;
		int subRuleCount;
		Rule rule;
		SubRule subRule;
		boolean skipMessage;
		int msgCount=c.getCount();
		if(c.moveToFirst()) {
			for(int ii=0; ii < msgCount; ii++) {
				sms_body=c.getString(c.getColumnIndexOrThrow("body")).toString();
				ruleWasApplied=false;
				Transaction sms = new Transaction();
				sms.accountStateCurrency=AccountCurrency;
				sms.setTransanctionDate(new Date(c.getLong(c.getColumnIndexOrThrow("date"))));
				sms_body=sms_body.replace("'", "").replace("\n", " ");
				sms.setBody(sms_body);
				sms.setNumber(phoneNumber);
				sms.setAccountDifferenceCurrency(AccountCurrency);
				skipMessage=false;
				for (int i=0; i<rule_count;i++){
					rule=ruleList.get(i);
					if (sms_body.matches(rule.getMask()) && !skipMessage) {
						if (rule.getRuleType()!=8) {// not Ignore type
							sms.transanctionType=ruleList.get(i).getRuleTypeDrawable();
							subRuleCount=rule.subRuleList.size();
							for (int j=0; j<subRuleCount; j++){
								subRule=rule.subRuleList.get(j);
								switch (subRule.getExtractedParameter()){
								case 0 : //Account state before transaction
									sms.setAccountStateBeforeFromString(subRule.applySubRule(sms_body, false));
									break;
								case 1 : //Account state after transaction
									sms.setAccountStateAfterFromString(subRule.applySubRule(sms_body, false));
									break;
								case 2 : //Account difference
									sms.setAccountDifferenceFromString(subRule.applySubRule(sms_body, false));
									break;
								case 3 : //Transaction commission
									sms.setComissionFromString(subRule.applySubRule(sms_body, false));
									break;
								case 4 : //Transaction currency
									sms.setAccountDifferenceCurrency(subRule.applySubRule(sms_body, true));
									break;
								}								
							}
							ruleWasApplied=true;					
						}else{
							skipMessage=true;
						}
					}
				}
				if (!skipMessage) {
					if (ruleWasApplied){
						if (!hideMatchedMessages){
							sms.calculateMissedData();
							transactionList.add(sms);
						}					
					}else{
						if (!hideNotMatchedMessages) {
							sms.calculateMissedData();
							transactionList.add(sms);
						}
					}
				}
				c.moveToNext();
			}
		}
		c.close();
		
		// removing duplicates
		HashSet<Transaction> se =new HashSet<Transaction>(transactionList);
		transactionList.clear();
		transactionList = new ArrayList<Transaction>(se);
		//Sorting by date
		Collections.sort(transactionList);

		// Adding virtual transanctions instead of missing sms.
		int transanctionsCount=transactionList.size();
		if (transanctionsCount>1) {
			for (int i=transanctionsCount-1;i>=1;i--){
				if (transactionList.get(i).hasAccountStateAfter && transactionList.get(i-1).hasAccountStateBefore ) {
					if (!transactionList.get(i).getAccountStateAfter().equals(transactionList.get(i-1).getAccountStateBefore())){
						Transaction new_transaction = new Transaction();
						new_transaction.setBody("");
						new_transaction.setNumber(phoneNumber);
						new_transaction.setAccountDifferenceCurrency(AccountCurrency);
						new_transaction.setTransanctionDate(new Date((transactionList.get(i-1).getTransanctionDate().getTime()+transactionList.get(i).getTransanctionDate().getTime())/2));
						new_transaction.accountStateCurrency=AccountCurrency;
						new_transaction.transanctionType=R.drawable.ic_transanction_missed;
						new_transaction.setAccountStateBefore(transactionList.get(i).getAccountStateAfter());
						new_transaction.setAccountStateAfter(transactionList.get(i-1).getAccountStateBefore());
						new_transaction.calculateMissedData();
						transactionList.add(new_transaction);
					}
				}
				// Adding info to transanctions with foreign currency. (calculating exchange rates if it is possible).
				if (transactionList.get(i-1).hasAccountDifference &&
						transactionList.get(i-1).hasAccountDifferenceCurrency &&
						transactionList.get(i).hasAccountStateAfter && 
						transactionList.get(i-1).hasAccountStateAfter) {
					if (!transactionList.get(i-1).getAccountDifferenceCurrency().equals(AccountCurrency) &&
							transactionList.get(i-1).getAccountDifference().signum()!=0)	{							
						 //smsList.get(i-1).setCurrencyRate((smsList.get(i).getAccountStateAfter().subtract(smsList.get(i-1).getAccountStateAfter()).divide(smsList.get(i-1).getAccountDifferenceMinus())));
						BigDecimal uah_price= transactionList.get(i).getAccountStateAfter().subtract(transactionList.get(i-1).getAccountStateAfter());
						BigDecimal rate = uah_price.divide(transactionList.get(i-1).getAccountDifference().negate(),3,RoundingMode.HALF_UP);
						transactionList.get(i-1).setCurrencyRate(rate);
						
						if (!transactionList.get(i-1).hasAccountStateBefore) {
							transactionList.get(i-1).setAccountStateBefore(transactionList.get(i).getAccountStateAfter());
						}
					}
				}
			}
			Collections.sort(transactionList);
		}
		
		// Set smsList in the ListAdapter
		listView.setAdapter(new TransanctionListAdapter(this,transactionList,hideCurrency,inverseRate));
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_preferences) {
			Intent intent = new Intent(this, PreferencesActivity.class);
		    startActivity(intent);
			return true;
		}
		if (id == R.id.action_bank_list) {
			Intent intent = new Intent(this, BankListActivity.class);
		    startActivity(intent);		
			return true;
		}
		if (id == R.id.action_rule_list) {
			Intent intent = new Intent(this, RuleListActivity.class);
		    startActivity(intent);		
			return true;
		}
		if (id == R.id.action_quit) {
			this.finish();
			System.exit(0);			
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_new_rule:
			//Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, RuleActivity.class);
			intent.putExtra("sms_body", selectedSMS);
			intent.putExtra("todo", "add");
		    startActivity(intent);		    
			return true;
		}
		return false;
	}
    
}
