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

public class RuleActivity extends AppCompatActivity {
	private List<Word> wordButtons;
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
		   rule = new Rule(bank.getId(),"new rule");
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
		ruleTypeView.setOnItemSelectedListener(new OnItemSelectedListener()
	       {
	    	  @Override
	         public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long id) {
	    		  imageView.setImageResource(Rule.ruleTypes[position]);
	              rule.setRuleType(position);
	    	  }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				   /*imageView.setImageResource(Rule.ruleTypes[0]);
				   rule.setRuleType(0);/**/
			}
	       });
		String[] words = rule.getSmsBody().split(" ");
		wordButtons = new ArrayList <Word>();
		Word W;
	   
	   W=new Word(this,0,"<BEGIN>");
	   wordButtons.add(W);
	   myLayout.addView(W);
	   
	   for (int i=1;i<=words.length;i++){
		   W=new Word(this,i,words[i-1]);
		   W.setBackgroundColor(Color.LTGRAY);
		   W.setOnClickListener(new View.OnClickListener() {
			     public void onClick(View v) {
			    	 ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
			    	 int wordIndex=((Word) v).getWordIndex();
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
	   
	   W=new Word(this,words.length+1,"<END>");
	   wordButtons.add(W);
	   myLayout.addView(W);

	   
	   Button nextBtn = (Button) this.findViewById(R.id.rule1_next);
	   nextBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
        	   	rule.setName(ruleNameView.getText().toString());
	       		DataBaseHelper db = new DataBaseHelper(v.getContext());
	    		db.openDataBase();
	    		db.addOrEditRule(rule);
	    		db.close();
           }
	   	});
    }
    
}