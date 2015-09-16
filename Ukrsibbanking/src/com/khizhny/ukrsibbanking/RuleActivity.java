package com.khizhny.ukrsibbanking;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RuleActivity extends AppCompatActivity {
	private List<Button> wordButtons;
	private Rule rule;
	private Bank bank;
	private TextView ruleNameView;
	private Spinner ruleTypeView;
	private ImageView imageView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule);
		FlowLayout myLayout = (FlowLayout) this.findViewById(R.id.rule1_flow_layout);
		Intent intent = getIntent();
		DataBaseHelper db = new DataBaseHelper(this);
		db.openDataBase();
		bank=db.getActiveBank();		
		String todo = intent.getExtras().getString("todo");
		if (todo.equals("add")){
			// adding new rule
		   rule = new Rule(bank.getId(),"New rule");
		   rule.setSmsBody(intent.getExtras().getString("sms_body"));
		} else
			// loading existing rule for editing.
		{
			rule=db.getRule(intent.getExtras().getInt("rule_id"));
		}
		db.close();
		
		imageView = (ImageView) this.findViewById(R.id.image);
		
		ruleNameView =  (TextView) this.findViewById(R.id.rule_name);
		ruleNameView.setText(rule.getName());
       
		ruleTypeView = (Spinner) this.findViewById(R.id.transaction_type);
		ruleTypeView.setSelection(rule.getRuleType());
		ruleTypeView.setOnItemSelectedListener(new OnItemSelectedListener()
	       {
	    	  @Override
	         public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
	    		  imageView.setImageResource(Rule.ruleTypes[position]);
	              rule.setRuleType(position);
	    	  }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				   imageView.setImageResource(Rule.ruleTypes[0]);
				   rule.setRuleType(0);/**/
			}
	       });
		
		
		imageView.setImageResource(rule.getRuleTypeDrawable());
		
		// Creating "word buttons" on Flow Layout
		String[] words = rule.getSmsBody().split(" ");
		wordButtons = new ArrayList <Button>();
		Button W;
	   
	    W = new Button(this);
	    W.setText("<BEGIN>");
	    W.setBackgroundColor(Color.GRAY);
	    wordButtons.add(W);
	    myLayout.addView(W);
	   
	   for (int i=1;i<=words.length;i++){
		   W=new Button(this);
		   W.setText(words[i-1]);
		   W.setBackgroundColor(Color.LTGRAY);
		   W.setOnClickListener(new View.OnClickListener() {
			     public void onClick(View v) {			    	 
			    	 ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
			    	 int wordIndex=wordButtons.indexOf(v);			    	 
					 if (buttonColor.getColor() == Color.GRAY) {
						v.setBackgroundColor(Color.LTGRAY);
						rule.deSelectWord(wordIndex);
					 } 
					 else 
					 {
						v.setBackgroundColor(Color.GRAY);
						rule.selectWord(wordIndex);
				     }
			     }
			   });
		   wordButtons.add(W);
		   myLayout.addView(W);
	   }
	   
	   W=new Button(this);
	   W.setText("<END>");
	   W.setBackgroundColor(Color.GRAY);
	   wordButtons.add(W);
	   myLayout.addView(W);
	   
	   // Making "word buttons" colored acording to selected words parameter of the rule
	   for (int i=1; i<=rule.wordsCount;i++){
		   if (rule.wordIsSelected[i]){
			   wordButtons.get(i).setBackgroundColor(Color.GRAY);
		   }
	   }

	   // Adding Next button click handler
	   Button nextBtn = (Button) this.findViewById(R.id.rule1_next);
	   nextBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
        	   	rule.setName(ruleNameView.getText().toString());
        	   	// Saving or Updating Rule in DB.
        	   	DataBaseHelper db = new DataBaseHelper(v.getContext());
	    		db.openDataBase();	    		
	    		rule.setId(db.addOrEditRule(rule)); 	    	
	    		db.close();
	    		RuleActivity.this.finish();
	    		Toast.makeText(v.getContext(), "New rule saved.", Toast.LENGTH_SHORT).show();
	    		Intent intent = new Intent(v.getContext(), SubRuleListActivity.class);
				intent.putExtra("rule_id", rule.getId());
			    startActivity(intent);		    
           }
	   	});
    }
    
}