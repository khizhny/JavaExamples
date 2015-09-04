package com.khizhny.ukrsibbanking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity  {
	public static final String PREFS_MAIN_SETTINGS = "MainSettings";
	private void saveParameters() {
		// Saving my new settings
	    SharedPreferences settings = getSharedPreferences(PREFS_MAIN_SETTINGS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    TextView phoneNumberView = (TextView) this.findViewById(R.id.phoneNumber);
	    editor.putString("PhoneNumber", phoneNumberView.getText().toString());
	    
	    TextView accountNumberView = (TextView) this.findViewById(R.id.accountNumber);
	    editor.putString("AccountNumber", accountNumberView.getText().toString());
	    
	    TextView cardNumberView1 = (TextView) this.findViewById(R.id.cardNumber1);
	    editor.putString("CardNumber1", cardNumberView1.getText().toString());
	    
	    TextView cardNumberView2 = (TextView) this.findViewById(R.id.cardNumber2);
	    editor.putString("CardNumber2", cardNumberView2.getText().toString());

	    TextView cardCurrencyView = (TextView) this.findViewById(R.id.accountCurrency);
	    editor.putString("CardCurrency", cardCurrencyView.getText().toString());
	    editor.commit();
	}
	private void loadParameters() {
		// Restore preferences	     
		SharedPreferences settings = getSharedPreferences(PREFS_MAIN_SETTINGS, 0);
    
	    TextView phoneNumberView = (TextView) this.findViewById(R.id.phoneNumber);
	    phoneNumberView.setText(settings.getString("PhoneNumber","729"));
  
	    TextView accountNumberView = (TextView) this.findViewById(R.id.accountNumber);
	    accountNumberView.setText(settings.getString("AccountNumber","26251009142308"));
    
	    TextView cardNumberView1 = (TextView) this.findViewById(R.id.cardNumber1);
	    cardNumberView1.setText(settings.getString("CardNumber1","5169***4839"));
	    
	    TextView cardNumberView2 = (TextView) this.findViewById(R.id.cardNumber2);
	    cardNumberView2.setText(settings.getString("CardNumber2","5169***4839"));

	    TextView accountCurrencyView = (TextView) this.findViewById(R.id.accountCurrency);
	    accountCurrencyView.setText(settings.getString("AccountCurrency","UAH"));
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		this.loadParameters();
	}
	@Override
	protected void onPause() {
		this.saveParameters();
		super.onPause();				  
	}
}
