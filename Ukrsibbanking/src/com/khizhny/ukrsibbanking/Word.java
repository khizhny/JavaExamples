package com.khizhny.ukrsibbanking;

import android.content.Context;
import android.widget.Button;

public class Word extends Button {
		private int wordIndex;
		private boolean wordIsStatic;
		private String word;
		public Word(Context context, int wordIndex,String word) {
	        super(context);
	        wordIsStatic=false;
	        this.wordIndex=wordIndex;
	        this.word = word;
	    }
		public int getWordIndex() {
			return wordIndex;
		}
		public void setWordIndex(int wordIndex) {
			this.wordIndex = wordIndex;
		}
		public boolean isWordIsStatic() {
			return wordIsStatic;
		}
		public void setWordIsStatic(boolean wordIsStatic) {
			this.wordIsStatic = wordIsStatic;
		}
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
	}
