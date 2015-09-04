package com.khizhny.ukrsibbanking;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class MainActivity extends ListActivity implements OnMenuItemClickListener{
	public static int banksCount=2;
	public static List<cBanks> bankList;
	public static int currencyCount=3;
	private String AccountCurrency;
	private String cardNumber1;
	private String cardNumber2;
	private static final String PREFS_MAIN_SETTINGS = "MainSettings";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
	}
    @Override
    protected void onResume() {
        super.onResume();
	    
	 	// Restore preferences	     
    	DataBaseHelper myDbHelper = new DataBaseHelper(this);
    	myDbHelper.openDataBase();
/*	    try {
	       myDbHelper.createDataBase();
	 	} catch (IOException ioe) {
	 		throw new Error("Unable to create database");
	 	}
	 
	 	try {	 
	 		myDbHelper.openDataBase();
	 	}catch(SQLException sqle){
	 		throw sqle;
	 	}/**/
	 	bankList=myDbHelper.getAllBanks();
	 	
	 	SharedPreferences settings = getSharedPreferences(PREFS_MAIN_SETTINGS, 0);
		String phoneNumber= settings.getString("PhoneNumber","729");
		String accountNumber= settings.getString("AccountNumber",".*");
		this.AccountCurrency=settings.getString("AccountCurrency","UAH");
		this.cardNumber1= settings.getString("CardNumber1",".*");
		this.cardNumber2= settings.getString("CardNumber2",".*");
		String cardNumber1_regexp=cardNumber1;
		String cardNumber2_regexp=cardNumber2;
		if (!this.cardNumber1.equals(".*")) {
			cardNumber1_regexp=cardNumber1.replace("*", "\\*");
		}
		if (!this.cardNumber2.equals(".*")) {
			cardNumber2_regexp=cardNumber2.replace("*", "\\*");
		}
		
		List<cSMS> smsList = new ArrayList<cSMS>();
		
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c= getContentResolver().query(uri, null, null ,null,null);
		//startManagingCursor(c);
		String sms_body;
		String sms_sender;
		Date   sms_date_sent;
		String date_string;
		/*
		 * Letter  Date or Time Component  Presentation        Examples
			------  ----------------------  ------------------  -------------------------------------
			G       Era designator          Text                AD
			y       Year                    Year                1996; 96
			M       Month in year           Month               July; Jul; 07
			w       Week in year            Number              27
			W       Week in month           Number              2
			D       Day in year             Number              189
			d       Day in month            Number              10
			F       Day of week in month    Number              2
			E       Day in week             Text                Tuesday; Tue
			u       Day number of week      Number              1
			a       Am/pm marker            Text                PM
			H       Hour in day (0-23)      Number              0
			k       Hour in day (1-24)      Number              24
			K       Hour in am/pm (0-11)    Number              0
			h       Hour in am/pm (1-12)    Number              12
			m       Minute in hour          Number              30
			s       Second in minute        Number              55
			S       Millisecond             Number              978
			z       Time zone               General time zone   Pacific Standard Time; PST; GMT-08:00
			Z       Time zone               RFC 822 time zone   -0800
			X       Time zone               ISO 8601 time zone  -08; -0800; -08:00
		 * */
		//String[] temp;
		// Read the SMS data and store it in the list
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
					if (sms_body.matches("Popovnennya rakhunku: .*, rakhunok "+accountNumber+" na sumu .*\\. Dostupniy zalyshok .*\\.")) {
						date_string = sms.getWordBetween("Popovnennya rakhunku: ",", rakhunok ");
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm:ss");
						sms.setCardNumber(sms.getWordBetween(" rakhunok ", " na sumu "));
						sms.setAccountNumber(sms.getWordAfter(", rakhunok ", " "));
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" na sumu ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupniy zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_plus;
					}
					//Otrymannia gotivky: A0309839 UKRSIBBANK, UA 19.08.2015 19:16 kartka 5169***4839 na sumu 4200.00UAH. Dostupnyi zalyshok 45.60UAH.
					if (sms_body.matches("Otrymannia gotivky: .* kartka "+cardNumber1_regexp+" na sumu .*\\. Dostupnyi zalyshok .*\\.") ||
						sms_body.matches("Otrymannia gotivky: .* kartka "+cardNumber2_regexp+" na sumu .*\\. Dostupnyi zalyshok .*\\.")) {
						sms.setCardNumber(sms.getWordAfter(" kartka ", " "));						
						date_string = sms.getWordBefore("kartka"," ", 2);
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm");						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" na sumu ", ". Dostupnyi zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_minus;
					}
					//25.06.2015 19:25:37 perekaz 5,000.00UAH z nakopychuvalnoho rakhunku 26206120002800 na 26251009142308. Balans nakopychuvalnogo rakhunku 62,279.87UAH.
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku .* na "+ accountNumber+"\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
					}
					if (sms_body.matches(".* perekaz .* z nakopychuvalnoho rakhunku "+ accountNumber+" na .*\\. Balans nakopychuvalnogo rakhunku .*\\.")) {						
						sms.setTransanctionDateFromString(sms.getWordBefore(" perekaz "," ", 2), "dd.MM.yyyy HH:mm:ss");
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" perekaz ", " z nakopychuvalnoho rakhunku "));
						sms.setAccountStateAfterFromString(sms.getWordAfter("Balans nakopychuvalnogo rakhunku ", "Anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
					}
					//Neuspishne otrymannia gotivky po karti 5169***4839. Nedostatno koshtiv na rahunku. Dostupnyi zalyshok 41.08UAH. Dovidka: 729.
					if (sms_body.matches("Neuspishne otrymannia gotivky po karti "+cardNumber1_regexp+". Nedostatno koshtiv na rahunku\\. Dostupnyi zalyshok .*\\. Dovidka: 729\\.")) {						
						sms.setTransanctionDate(sms_date_sent);						
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupnyi zalyshok ", ". Dovidka:"));
						sms.setAccountDifferenceMinusFromString("0");
						sms.transanctionType=R.drawable.ic_transanction_failed;
					}
					//Perekaz koshtiv: 25.06.2015 12:14:10, z rakhunku 26251009142308 na rakhunok 26206120002800 suma 5,000.00UAH. Dostupniy zalyshok 41.08UAH.
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku 26251009142308 na rakhunok "+ accountNumber+" suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setTransanctionDate(sms_date_sent);
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_to;
					}
					if (sms_body.matches("Perekaz koshtiv: .*, z rakhunku "+ accountNumber+" na rakhunok .* suma .*. Dostupniy zalyshok .*\\.")) {						
						sms.setAccountDifferenceMinusFromString(sms.getWordBetween(" suma ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordAfter(" Dostupniy zalyshok ","anystring"));
						sms.transanctionType=R.drawable.ic_transanction_transfer_from;
					}
					//Oplata tovariv: S1K20K60 27TOVNAFTAPETROLIUM, UA 22.06.2015 09:13 kartka 5169***4839 na sumu 923.29UAH. Dostupnyi zalyshok 200.76UAH.
					//Oplata tovariv: 29744826 ALIBABA.COM, GB 20.06.2015 16:44 kartka 5169***4839 na sumu 22.56USD. Dostupnyi zalyshok 1124.05UAH.
					if (sms_body.matches("Oplata tovariv: .* kartka "+cardNumber1_regexp+" na sumu .*\\. Dostupnyi zalyshok .*\\.")) {						
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
						new_sms.setAccountNumber(accountNumber);
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
		setListAdapter(new ListAdapter(this, smsList));
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
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
		    startActivity(intent);
			return true;
		}
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
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		cSMS sms = (cSMS)getListAdapter().getItem(position);
		
		//Toast.makeText(getApplicationContext(), sms.getBody(), Toast.LENGTH_LONG).show();
		PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
		popupMenu.setOnMenuItemClickListener(MainActivity.this);
		popupMenu.inflate(R.menu.popup_menu);
		popupMenu.show();
		
	}
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_new_template:
			Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.item_new_rule:
			Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
    
}
