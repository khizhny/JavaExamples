package com.khizhny.ukrsibbanking;

import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

public class PrefPartCommon extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // Load the preferences from an XML resource
       addPreferencesFromResource(R.xml.preference_fragment_common);
       
       PreferenceScreen root = this.getPreferenceScreen();
      /* ListPreference list = (ListPreference) root.findPreference("bank_list");
       CharSequence[] banks={};
       for (int i=0;i<MainActivity.banks.size()-1;i++){
    	   banks[i]=MainActivity.banks.get(i);
       }/**/
       //list.setEntries(banks);
       //list.setEntryValues(banks);
    }

}
