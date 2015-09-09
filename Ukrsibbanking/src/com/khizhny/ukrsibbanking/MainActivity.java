package com.khizhny.ukrsibbanking;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.khizhny.ukrsibbanking.cSMS;
public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener{

	public static int banksCount=2;
	public List<cBanks> bankList;
	public List<cSMS> smsList; 
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
      		cSMS sms = (cSMS)listView.getItemAtPosition(position);
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
		
		smsList = new ArrayList<cSMS>();
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c= getContentResolver().query(uri, null, null ,null,null);
		String sms_body;
		String sms_sender;
		Date   sms_date_sent;
		String date_string;
		if(c.moveToFirst()) {
			for(int i=0; i < c.getCount(); i++) {
				cSMS sms = new cSMS();
				sms.accountStateCurrency=AccountCurrency;
				sms_body=c.getString(c.getColumnIndexOrThrow("body")).toString();
				sms_sender=c.getString(c.getColumnIndexOrThrow("address")).toString();
				sms_date_sent= new Date(c.getLong(c.getColumnIndexOrThrow("date")));
				sms.setTransanctionDate(sms_date_sent);
				
				if(sms_sender.equals(phoneNumber)) { 
					sms.setBody(sms_body);
					sms.setNumber(sms_sender);			
					//Popovnennya rakhunku: 12.08.2015 09:12:21, rakhunok 26251009142308 na sumu 3,000.00UAH. Dostupniy zalyshok 3062.13UAH.
					if (sms_body.matches("Popovnennya rakhunku: .*, rakhunok .* na sumu .*\\. Dostupniy zalyshok .*\\.")) {
						date_string = sms.getWordBetween("Popovnennya rakhunku: ",", rakhunok ");
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm:ss");
						sms.setCardNumber(sms.getWordBetween(" rakhunok ", " na sumu "));
						sms.setAccountNumber(sms.getWordAfter(", rakhunok ", " "));
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" na sumu ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupniy zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_plus;
					}
					//Otrymannia gotivky: A0309839 UKRSIBBANK, UA 19.08.2015 19:16 kartka 5169***4839 na sumu 4200.00UAH. Dostupnyi zalyshok 45.60UAH.
					if (sms_body.matches("Otrymannia gotivky: .* kartka .* na sumu .*\\. Dostupnyi zalyshok .*\\.")) {
						sms.setCardNumber(sms.getWordAfter(" kartka ", " "));						
						date_string = sms.getWordBefore("kartka"," ", 2);
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm");						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" na sumu ", ". Dostupnyi zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_minus;
					}
					//25.06.2015 19:25:37 perekaz 5,000.00UAH z nakopychuvalnoho rakhunku 26206120002800 na 26251009142308. Balans nakopychuvalnogo rakhunku 62,279.87UAH.
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku .* na .*\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
					}
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku .* na .*\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.setAccountStateAfterFromString(sms.getWordAfter("Balans nakopychuvalnogo rakhunku ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
					}
					//Neuspishne otrymannia gotivky po karti 5169***4839. Nedostatno koshtiv na rahunku. Dostupnyi zalyshok 41.08UAH. Dovidka: 729.
					if (sms_body.matches("Neuspishne otrymannia gotivky po karti .*. Nedostatno koshtiv na rahunku\\. Dostupnyi zalyshok .*\\. Dovidka: 729\\.")) {						
						sms.setTransanctionDate(sms_date_sent);						
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", ". Dovidka:"));
						sms.setAccountDifferenceMinusFromString("0");
						sms.transanctionType=R.drawable.ic_transanction_failed;
					}
					//Perekaz koshtiv: 25.06.2015 12:14:10, z rakhunku 26251009142308 na rakhunok 26206120002800 suma 5,000.00UAH. Dostupniy zalyshok 41.08UAH.
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku 26251009142308 na rakhunok .* suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setTransanctionDate(sms_date_sent);
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
					}
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku .* na rakhunok .* suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
					}
					//Oplata tovariv: S1K20K60 27TOVNAFTAPETROLIUM, UA 22.06.2015 09:13 kartka 5169***4839 na sumu 923.29UAH. Dostupnyi zalyshok 200.76UAH.
					//Oplata tovariv: 29744826 ALIBABA.COM, GB 20.06.2015 16:44 kartka 5169***4839 na sumu 22.56USD. Dostupnyi zalyshok 1124.05UAH.
					if (sms_body.matches("Oplata tovariv: .* kartka .* na sumu .*\\. Dostupnyi zalyshok .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" kartka "," ", 2), "dd.MM.yyyy HH:mm");
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" na sumu ", ". Dostupnyi zalyshok"));
						sms.setAccountStateAfterFromString(sms.getWordAfter("Dostupnyi zalyshok","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_pay;
					}	
					smsList.add(sms);
				}
				c.moveToNext();
			}
		}
		c.close();
		
		// Sorting SMS list by transanction date
		Collections.sort(smsList);

		// Adding virtual transanctions instead of missing sms.
		int transanctionsCount=smsList.size();
		if (transanctionsCount>1) {
			for (int i=transanctionsCount-1;i>=1;i--){
				if (smsList.get(i).hasAccountStateAfter && smsList.get(i-1).hasAccountStateBefore ) {
					if (!smsList.get(i).getAccountStateAfter().equals(smsList.get(i-1).getAccountStateBefore())){
						cSMS new_sms = new cSMS();
						new_sms.setBody("SMS missed");
						new_sms.setNumber(phoneNumber);
						new_sms.setAccountDifferenceCurrency(AccountCurrency);
						new_sms.setTransanctionDate(new Date((smsList.get(i-1).getTransanctionDate().getTime()+smsList.get(i).getTransanctionDate().getTime())/2));
						new_sms.accountStateCurrency=AccountCurrency;
						new_sms.transanctionType=R.drawable.ic_transanction_missed;
						new_sms.setAccountStateBefore(smsList.get(i).getAccountStateAfter());
						new_sms.setAccountStateAfter(smsList.get(i-1).getAccountStateBefore());
						smsList.add(new_sms);
					}
				}
				// Adding transanctions to view currency rates if currency differs from account currency
				if (smsList.get(i-1).hasAccountDifference &&
					smsList.get(i-1).hasAccountDifferenceCurrency &&
					smsList.get(i).hasAccountStateAfter && 
					smsList.get(i-1).hasAccountStateAfter) {
					if (!smsList.get(i-1).getAccountDifferenceCurrency().equals(AccountCurrency) &&
						 smsList.get(i-1).getAccountDifferencePlus().signum()!=0)	{							
						 //smsList.get(i-1).setCurrencyRate((smsList.get(i).getAccountStateAfter().subtract(smsList.get(i-1).getAccountStateAfter()).divide(smsList.get(i-1).getAccountDifferenceMinus())));
						BigDecimal uah_price= smsList.get(i).getAccountStateAfter().subtract(smsList.get(i-1).getAccountStateAfter());
						BigDecimal rate = uah_price.divide(smsList.get(i-1).getAccountDifferenceMinus(),3,RoundingMode.HALF_UP);
						smsList.get(i-1).setCurrencyRate(rate);
					}
				}
			}
			Collections.sort(smsList);
		}
		
		// Set smsList in the ListAdapter
		listView.setAdapter(new TransanctionListAdapter(this,smsList));
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
		if (id == R.id.action_quit) {
			this.finish();
			System.exit(0);			
			return true;
		}
		if (id == R.id.action_bank_list) {
			Intent intent = new Intent(this, BankListActivity.class);
		    startActivity(intent);		
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_new_template:
			//Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, Rule1Activity.class);
			intent.putExtra("sms_body", selectedSMS);
		    startActivity(intent);		    
			return true;
		case R.id.item_new_rule:
			Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
    
}
