package com.khizhny.ukrsibbanking;

import android.preference.PreferenceActivity;

import java.util.List;
import android.os.Bundle;
import android.widget.Button;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add a button to the header list.
        if (hasHeaders()) {
            Button button = new Button(this);
            button.setText("Add Bank");
            setListFooter(button);
        }
    }
    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
        
}
