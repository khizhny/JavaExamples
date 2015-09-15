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
	 	bankList=myDb.getAllBanks();
	 	phoneNumber= myDb.getActiveBankPhone();
	 	AccountCurrency=myDb.getActiveBankCurrency();
	 	myDb.close();
	 	
		// Restore preferences	     
	 	SharedPreferences settings =PreferenceManager.getDefaultSharedPreferences(this);
		settings.getBoolean("hide_messages",false);
		Boolean hide_messages= settings.getBoolean("hide_messages",false);	
		
	 	transactionList = new ArrayList<Transaction>();
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c= getContentResolver().query(uri, null, null ,null,null);
		String sms_body;
		String sms_sender;
		Date   sms_date_sent;
		boolean ruleWasApplied;
		String date_string;
		if(c.moveToFirst()) {
			for(int i=0; i < c.getCount(); i++) {
				sms_sender=c.getString(c.getColumnIndexOrThrow("address")).toString();
				if(sms_sender.equals(phoneNumber)) { 
					Transaction sms = new Transaction();
					sms.accountStateCurrency=AccountCurrency;
					sms_body=c.getString(c.getColumnIndexOrThrow("body")).toString();
					ruleWasApplied=false;
					sms_date_sent= new Date(c.getLong(c.getColumnIndexOrThrow("date")));
					sms.setBody(sms_body);
					sms.setNumber(sms_sender);
					sms.setTransanctionDate(sms_date_sent);
					//Popovnennya rakhunku: 12.08.2015 09:12:21, rakhunok 26251009142308 na sumu 3,000.00UAH. Dostupniy zalyshok 3062.13UAH.
					if (sms_body.matches("Popovnennya rakhunku: .*, rakhunok .* na sumu .*\\. Dostupniy zalyshok .*\\.")) {
						date_string = sms.getWordBetween("Popovnennya rakhunku: ",", rakhunok ");
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm:ss");
						sms.setCardNumber(sms.getWordBetween(" rakhunok ", " na sumu "));
						sms.setAccountNumber(sms.getWordAfter(", rakhunok ", " "));
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" na sumu ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupniy zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_plus;
						ruleWasApplied=true;
					}
					//Otrymannia gotivky: A0309839 UKRSIBBANK, UA 19.08.2015 19:16 kartka 5169***4839 na sumu 4200.00UAH. Dostupnyi zalyshok 45.60UAH.
					if (sms_body.matches("Otrymannia gotivky: .* kartka .* na sumu .*\\. Dostupnyi zalyshok .*\\.")) {
						sms.setCardNumber(sms.getWordAfter(" kartka ", " "));						
						date_string = sms.getWordBefore("kartka"," ", 2);
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm");						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" na sumu ", ". Dostupnyi zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_minus;
						ruleWasApplied=true;
					}
					//25.06.2015 19:25:37 perekaz 5,000.00UAH z nakopychuvalnoho rakhunku 26206120002800 na 26251009142308. Balans nakopychuvalnogo rakhunku 62,279.87UAH.
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku .* na .*\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
						ruleWasApplied=true;
					}
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku .* na .*\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.setAccountStateAfterFromString(sms.getWordAfter("Balans nakopychuvalnogo rakhunku ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
						ruleWasApplied=true;
					}
					//Neuspishne otrymannia gotivky po karti 5169***4839. Nedostatno koshtiv na rahunku. Dostupnyi zalyshok 41.08UAH. Dovidka: 729.
					if (sms_body.matches("Neuspishne otrymannia gotivky po karti .*. Nedostatno koshtiv na rahunku\\. Dostupnyi zalyshok .*\\. Dovidka: 729\\.")) {						
						sms.setTransanctionDate(sms_date_sent);						
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", ". Dovidka:"));
						sms.setAccountDifferenceMinusFromString("0");
						sms.transanctionType=R.drawable.ic_transanction_failed;
						ruleWasApplied=true;
					}
					//Perekaz koshtiv: 25.06.2015 12:14:10, z rakhunku 26251009142308 na rakhunok 26206120002800 suma 5,000.00UAH. Dostupniy zalyshok 41.08UAH.
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku 26251009142308 na rakhunok .* suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setTransanctionDate(sms_date_sent);
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
						ruleWasApplied=true;
					}
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku .* na rakhunok .* suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
						ruleWasApplied=true;
					}
					//Oplata tovariv: S1K20K60 27TOVNAFTAPETROLIUM, UA 22.06.2015 09:13 kartka 5169***4839 na sumu 923.29UAH. Dostupnyi zalyshok 200.76UAH.
					//Oplata tovariv: 29744826 ALIBABA.COM, GB 20.06.2015 16:44 kartka 5169***4839 na sumu 22.56USD. Dostupnyi zalyshok 1124.05UAH.
					if (sms_body.matches("Oplata tovariv: .* kartka .* na sumu .*\\. Dostupnyi zalyshok .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" kartka "," ", 2), "dd.MM.yyyy HH:mm");
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" na sumu ", ". Dostupnyi zalyshok"));
						sms.setAccountStateAfterFromString(sms.getWordAfter("Dostupnyi zalyshok","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_pay;
						ruleWasApplied=true;
					}
					//Vidmina operatsii z oplaty tovariv: 10333015 ali*aliexpress.com, CN 10.09.2015 01:40 kartka 5169***4839 na sumu 28.01USD. Dostupnyi zalyshok 22718.15UAH.
					if (ruleWasApplied || !hide_messages) {transactionList.add(sms);}
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
						new_transaction.setBody("<No messages about transaction(s)>");
						new_transaction.setNumber(phoneNumber);
						new_transaction.setAccountDifferenceCurrency(AccountCurrency);
						new_transaction.setTransanctionDate(new Date((transactionList.get(i-1).getTransanctionDate().getTime()+transactionList.get(i).getTransanctionDate().getTime())/2));
						new_transaction.accountStateCurrency=AccountCurrency;
						new_transaction.transanctionType=R.drawable.ic_transanction_missed;
						new_transaction.setAccountStateBefore(transactionList.get(i).getAccountStateAfter());
						new_transaction.setAccountStateAfter(transactionList.get(i-1).getAccountStateBefore());
						transactionList.add(new_transaction);
					}
				}
				// Adding transanctions to view currency rates if currency differs from account currency
				if (transactionList.get(i-1).hasAccountDifference &&
						transactionList.get(i-1).hasAccountDifferenceCurrency &&
						transactionList.get(i).hasAccountStateAfter && 
						transactionList.get(i-1).hasAccountStateAfter) {
					if (!transactionList.get(i-1).getAccountDifferenceCurrency().equals(AccountCurrency) &&
							transactionList.get(i-1).getAccountDifferencePlus().signum()!=0)	{							
						 //smsList.get(i-1).setCurrencyRate((smsList.get(i).getAccountStateAfter().subtract(smsList.get(i-1).getAccountStateAfter()).divide(smsList.get(i-1).getAccountDifferenceMinus())));
						BigDecimal uah_price= transactionList.get(i).getAccountStateAfter().subtract(transactionList.get(i-1).getAccountStateAfter());
						BigDecimal rate = uah_price.divide(transactionList.get(i-1).getAccountDifferenceMinus(),3,RoundingMode.HALF_UP);
						transactionList.get(i-1).setCurrencyRate(rate);
					}
				}
			}
			Collections.sort(transactionList);
		}
		
		// Set smsList in the ListAdapter
		listView.setAdapter(new TransanctionListAdapter(this,transactionList));
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
