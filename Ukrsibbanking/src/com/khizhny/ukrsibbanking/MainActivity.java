package com.khizhny.ukrsibbanking;

//import android.support.v7.app.ActionBarActivity;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.khizhny.ukrsibbanking.cSMS;
public class MainActivity extends ListActivity {

	public static String[][] CURRENCY={{"UAH","UAH","EUR","USD","USD"},  // Задаем возможные обозначение валюты в СМС
	                                   {"UAH","грн","EUR","USD","$"}};
	public static String VAR_TRANCANCTION_DATE_DD_MM_YYYY_HH_MM_SS = "##TRANCANCTION_DATE_DD_MM_YYYY_HH_MM_SS##";
	public static String VAR_ACCOUNT_NUMBER = "##ACCOUNT_NUMBER##";
	public static String VAR_ACCOUNT_STATE_BEFORE = "##ACCOUNT_STATE_BEFORE##";
	public static String VAR_ACCOUNT_STATE_AFTER = "##ACCOUNT_STATE_AFTER##";
	public static String[][] SMS_TEMPLATES={{"729"}, // шаблоны распознаваемых СМС
            {"Popovnennya rakhunku: .*, rakhunok .* na sumu .*\\. Dostupniy zalyshok .*\\."}
            };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		List<cSMS> smsList = new ArrayList<cSMS>();
		
		Uri uri = Uri.parse("content://sms/inbox");
		Cursor c= getContentResolver().query(uri, null, null ,null,null);
		//startManagingCursor(c);
		String sms_body;
		String sms_sender;
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
				sms_body=c.getString(c.getColumnIndexOrThrow("body")).toString();
				sms_sender=c.getString(c.getColumnIndexOrThrow("address")).toString();
				if(sms_sender.equals("729")) { 
					sms.setBody(sms_body);
					sms.setNumber(sms_sender);			
					if (sms_body.matches("Popovnennya rakhunku: .*, rakhunok .* na sumu .*\\. Dostupniy zalyshok .*\\.")) {
						date_string = sms.getWordBetween("Popovnennya rakhunku: ",", rakhunok ");
						sms.setTransanctionDateFromString(date_string, "dd.MM.yyyy HH:mm:ss");
						sms.setAccountNumber(sms.getWordAfter(", rakhunok ", " "));
						sms.setAccountDifferencePlusFromString(sms.getWordBetween(" na sumu ", ". Dostupniy zalyshok "));
						sms.setAccountStateAfterFromString(sms.getWordBetween("Dostupniy zalyshok ", "Anystring"));
					}
					smsList.add(sms);
				}
				c.moveToNext();
			}
		}
		c.close();
		
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		cSMS sms = (cSMS)getListAdapter().getItem(position);
		
		Toast.makeText(getApplicationContext(), sms.getBody(), Toast.LENGTH_LONG).show();
		
	}
}
