package com.khizhny.ukrsibbanking;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SubRuleListActivity extends AppCompatActivity {
	private ListView listView;
	private List<SubRule> subRuleList;
	private SubRuleListAdapter adapter;
	private Rule rule;
	protected static List<String> phrases;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub_rule);
		
	}
	@Override
	protected void onPause() {
	    super.onPause();
		// Saving all then subrules in our list.
	    	DataBaseHelper db = new DataBaseHelper(this);
	    	db.openDataBase();	    
	    	int subRulesCount = adapter.getCount();
	    	for (int i=0; i<subRulesCount;i++){
    		db.addOrEditSubRule(adapter.getItem(i));
	    	}
	    	db.close();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        
     // getting Rule ID from Intent
        Intent intent = getIntent();
        int ruleId = intent.getExtras().getInt("rule_id");		
        
		// getting subRules from db to array list
		subRuleList=new ArrayList<SubRule>();
    	DataBaseHelper db = new DataBaseHelper(this);
	 	db.openDataBase();
	 	rule=db.getRule(ruleId);
	 	subRuleList=db.getSubRules(ruleId);
	 	db.close();
	 	//
	 	phrases=rule.getConstantPhrases();
	 			
	 	// inflating list view with subrules
	 	adapter  = new SubRuleListAdapter(this, subRuleList);
	 	adapter.rule=rule;
	 	listView = (ListView) findViewById(R.id.sub_rule_list);
	 	listView.setAdapter(adapter);	
	 	
	 	TextView smsTextView=(TextView) findViewById(R.id.sms_text);
	 	smsTextView.setText("<BEGIN>"+rule.getSmsBody()+"<END>");
			
	 	Button addView = (Button)  findViewById(R.id.sub_rule_add);
	 	addView.setOnClickListener(new View.OnClickListener() {
	           public void onClick(View v) {
	        	   	// addin new SubRule to the list and db.
	        	   	DataBaseHelper db = new DataBaseHelper(v.getContext());
		    		db.openDataBase();	    		
		    		SubRule sr = new SubRule(rule.getId());
		    		sr.setId(db.addOrEditSubRule(sr));
		    		subRuleList.add(sr);
		    		db.close();
		    		adapter.notifyDataSetChanged();
	           }
		   	});
    }

}
