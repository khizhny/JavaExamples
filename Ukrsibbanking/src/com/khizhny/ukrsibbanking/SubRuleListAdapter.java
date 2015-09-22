package com.khizhny.ukrsibbanking;

import java.util.List;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
public class SubRuleListAdapter extends ArrayAdapter<SubRule> {

    private final Context context;
    private final List<SubRule> subRuleList;

    private CheckBox ActiveView;
    private Spinner ParameterView;
    private Spinner MethodView;
    private Spinner LeftNView;
    private Spinner RightNView;
    private Spinner LeftPhraseView;
    private Spinner RightPhraseView;
    private TextView ConstantView;
    private TextView ResultView;
    public Rule rule;
    private Spinner SeparatorView;
    private CheckBox NegateView;
    private Spinner IgnoreNLeftView;
    private Spinner IgnoreNRightView;
    private boolean doNotDoEvents;
	
	public SubRuleListAdapter(Context context, List<SubRule> subRuleList) {
		super(context, R.layout.activity_sub_rule_list_row, subRuleList);
		this.context = context;
		this.subRuleList = subRuleList;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(R.layout.activity_sub_rule_list_row, parent, false);
		}
		SubRule sr=subRuleList.get(position);
		
		doNotDoEvents=true;
		
        //========================================================================================
		ActiveView=(CheckBox) rowView.findViewById(R.id.sub_rule_active);
		ActiveView.setChecked(true);
		ActiveView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			   @Override
			   public void onCheckedChanged(CompoundButton v, boolean isChecked) {
				   if (!isChecked) {
					   int subRuleId= subRuleList.get(position).getId();
					   DataBaseHelper db = new DataBaseHelper(v.getContext());
					   db.openDataBase();
					   db.deleteSubRule(subRuleId);
					   db.close();
					   subRuleList.remove(position);
					   notifyDataSetChanged();
				   }				   
			   }
			  } );
        //========================================================================================
		NegateView=(CheckBox) rowView.findViewById(R.id.sub_rule_negate);
		NegateView.setChecked(sr.isNegate());
		NegateView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			   @Override
			   public void onCheckedChanged(CompoundButton v, boolean isChecked) {
				   if (!doNotDoEvents && isChecked!=subRuleList.get(position).isNegate()) {
					   subRuleList.get(position).setNegate(isChecked);
					   notifyDataSetChanged();
				   }				   
			   }
			  });
		
        //========================================================================================
		ParameterView = (Spinner) rowView.findViewById(R.id.sub_rule_parameter);
		ParameterView.setSelection(sr.getExtractedParameter());
		ParameterView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents && subRuleList.get(position).getExtractedParameter()!=selectedPosition) {
            		subRuleList.get(position).setExtractedParameter(selectedPosition);
            		notifyDataSetChanged();
            	}            	
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
 
        //========================================================================================
		MethodView = (Spinner) rowView.findViewById(R.id.sub_rule_method);
		MethodView.setSelection(sr.getExtractionMethod());
		MethodView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents && subRuleList.get(position).getExtractionMethod()!=selectedPosition) {
            		subRuleList.get(position).setExtractionMethod(selectedPosition);
            		notifyDataSetChanged();
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

		//========================================================================================
		LeftNView = (Spinner) rowView.findViewById(R.id.sub_rule_left_n);
		LeftNView.setSelection(sr.getDistanceToLeftPhrase()-1);
		LeftNView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents &&(selectedPosition+1)!=subRuleList.get(position).getDistanceToLeftPhrase()){
            		subRuleList.get(position).setDistanceToLeftPhrase(selectedPosition+1);
                	notifyDataSetChanged();
            	}            	
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
	
        //========================================================================================
		RightNView = (Spinner) rowView.findViewById(R.id.sub_rule_right_n);
		RightNView.setSelection(sr.getDistanceToRightPhrase()-1);
		RightNView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents &&(selectedPosition+1)!=subRuleList.get(position).getDistanceToRightPhrase()){
            		subRuleList.get(position).setDistanceToRightPhrase(selectedPosition+1);
                	notifyDataSetChanged();
            	} 
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
		
        ArrayAdapter<String> PhraseAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, SubRuleListActivity.phrases);
        PhraseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //========================================================================================
        LeftPhraseView = (Spinner) rowView.findViewById(R.id.sub_rule_left_phrase);
        LeftPhraseView.setAdapter(PhraseAdapter);
        LeftPhraseView.setSelection(SubRuleListActivity.phrases.indexOf(sr.getLeftPhrase()));
        LeftPhraseView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents && !SubRuleListActivity.phrases.get(selectedPosition).equals(subRuleList.get(position).getLeftPhrase())){
            		subRuleList.get(position).setLeftPhrase(SubRuleListActivity.phrases.get(selectedPosition));
                	notifyDataSetChanged();
            	}            	
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        
        //========================================================================================
        RightPhraseView = (Spinner) rowView.findViewById(R.id.sub_rule_right_phrase);
        RightPhraseView.setAdapter(PhraseAdapter);
        RightPhraseView.setSelection(SubRuleListActivity.phrases.indexOf(sr.getRightPhrase()));
        RightPhraseView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (!doNotDoEvents && !SubRuleListActivity.phrases.get(selectedPosition).equals(subRuleList.get(position).getRightPhrase())){
            		subRuleList.get(position).setRightPhrase(SubRuleListActivity.phrases.get(selectedPosition));
                	notifyDataSetChanged();
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        
        //========================================================================================
        ConstantView = (TextView) rowView.findViewById(R.id.sub_rule_constant_value);
        if (sr.getExtractionMethod()==3) {
        	ConstantView.setText(sr.getConstantValue());
        }else{
        	ConstantView.setText("");
        }
        ConstantView.setOnEditorActionListener(
    	    new EditText.OnEditorActionListener() {
    	    	@Override
    	    	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	    	    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
    	    	        actionId == EditorInfo.IME_ACTION_DONE ||
    	    	        event.getAction() == KeyEvent.ACTION_DOWN &&
    	    	        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
    	    	            // the user is done typing. 
	    	               	if (subRuleList.get(position).getExtractionMethod()==3) {
	    	            		 subRuleList.get(position).setConstantValue(v.getText().toString());
	    	            		 notifyDataSetChanged();
	    	            	 } 
    	    	           return true; // consume.
            
    	    	    }
    	    	    return false; // pass on to other listeners. 
    	    	}
       });
        
        //========================================================================================
        ResultView=(TextView) rowView.findViewById(R.id.sub_rule_result_value);
        if (sr.getExtractedParameter()==4) { //Transaction currency (text parameter)
        	ResultView.setText(sr.applySubRule(rule.getSmsBody(),true));
        } else { // other parameters (numeric)
        	ResultView.setText(sr.applySubRule(rule.getSmsBody(),false));
        }
        
        //========================================================================================
        SeparatorView=(Spinner) rowView.findViewById(R.id.sub_rule_separator);
        SeparatorView.setSelection(sr.getDecimalSeparator());
        SeparatorView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (subRuleList.get(position).getDecimalSeparator()!=selectedPosition){
            		subRuleList.get(position).setDecimalSeparator(selectedPosition);
                	notifyDataSetChanged();
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        
        //========================================================================================
        IgnoreNLeftView=(Spinner) rowView.findViewById(R.id.sub_rule_ignore_n_first);
        IgnoreNLeftView.setSelection(sr.getTrimLeft());
        IgnoreNLeftView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (subRuleList.get(position).getTrimLeft()!=selectedPosition) {
            		subRuleList.get(position).setTrimLeft(selectedPosition);
            		notifyDataSetChanged();
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        
        //========================================================================================
        IgnoreNRightView=(Spinner) rowView.findViewById(R.id.sub_rule_ignore_n_last);
        IgnoreNRightView.setSelection(sr.getTrimRight());
        IgnoreNRightView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
            	if (subRuleList.get(position).getTrimRight()!=selectedPosition) {
            		subRuleList.get(position).setTrimRight(selectedPosition);
            		notifyDataSetChanged();
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        
        
        // hiding unused views depending on used method
    	switch (sr.getExtractionMethod()){
			case 0: // n-th word after Phrase
				rowView.findViewById(R.id.sub_rule_left_l).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_l).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_left_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_left_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_const_l).setVisibility(View.GONE);
				break;
			case 1: // n-th word before Phrase
				rowView.findViewById(R.id.sub_rule_left_l).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_right_l).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_left_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_left_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_const_l).setVisibility(View.GONE);
				break;
			case 2: // all words between Phrases
				rowView.findViewById(R.id.sub_rule_left_l).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_l).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_left_n).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_right_n).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_left_n_label).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_right_n_label).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_const_l).setVisibility(View.GONE);
				break;
			case 3: // use constant Value
				rowView.findViewById(R.id.sub_rule_left_l).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_right_l).setVisibility(View.GONE);
				rowView.findViewById(R.id.sub_rule_left_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_left_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_right_n_label).setVisibility(View.VISIBLE);
				rowView.findViewById(R.id.sub_rule_const_l).setVisibility(View.VISIBLE);
				break;
    	}
    	doNotDoEvents=false;
      return rowView;
	}
	
}