package com.khizhny.ukrsibbanking;

import android.preference.PreferenceFragment;
import android.os.Bundle;

public class PrefPartBanks extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_fragment_banks);
 //       PreferenceScreen root = this.getPreferenceScreen();
        for (int i=0; i<MainActivity.banksCount;i++) {
        	
        	
        }
       /* EditTextPreference pref=root.createEditTextPreference(this,"title","summary","serverANme", new Preference.OnPreferenceChangeListener(){
            public boolean onPreferenceChange(Preference pref,    Object newValue){
              String v=((String)newValue).trim();
              mProfile.setServerName(v);
              setSummary(pref,R.string.vpn_vpn_server,v);
              return true;
            }
          }*/

    }
}
