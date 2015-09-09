package com.khizhny.ukrsibbanking;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Rule1Activity extends AppCompatActivity {
	private List<Word> wordButtons;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_rule1);
       FlowLayout myLayout = (FlowLayout) this.findViewById(R.id.rule1_flow_layout);
       Intent intent = getIntent();
       String sms_body = intent.getExtras().getString("sms_body");
       String[] words = sms_body.split(" ");
       wordButtons = new ArrayList <Word>();
       Word W;
       for (int i=0;i<words.length;i++){
    	   //constantWords[i]=false;
    	   W=new Word(this,i,words[i]);
    	   W.setText(words[i]);
    	   W.setBackgroundColor(Color.LTGRAY);
    	   myLayout.addView(W);
    	   W.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	        	 ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
	        	 int wordIndex=((Word) v).getWordIndex();
	        	 Toast.makeText(v.getContext(), "Word # "+wordIndex, Toast.LENGTH_SHORT).show();
	        	 if (buttonColor.getColor() == Color.GRAY) {
	        		v.setBackgroundColor(Color.LTGRAY);
	        		//constantWords[wordIndex]=false;
	        	 } 
	        	 else 
	        	 {
	        		v.setBackgroundColor(Color.GRAY);
	        		//constantWords[wordIndex]=true;
	             }    	            	 
	         }
    	   });
    	   wordButtons.add(W);
       }        
	   
	   Button nextBtn = (Button) this.findViewById(R.id.rule1_next);
	   nextBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
        	   String regexp="";
        	   for (int i=0;i<wordButtons.size();i++){
        		   regexp=regexp+wordButtons.get(i).getWord();
        	   }
           }
	   	});
    }
    
}