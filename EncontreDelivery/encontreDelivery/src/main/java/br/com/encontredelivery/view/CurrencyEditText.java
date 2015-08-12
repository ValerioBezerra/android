package br.com.encontredelivery.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import br.com.encontredelivery.adapter.EscolherFormaPagamentoAdapter;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class CurrencyEditText extends EditText {
	private Context context;
	
	private String symbol;
	private int entireAmount;
	private String entireCharacter;
	private int decimalAmount;
	private String decimalCharacter;
	private boolean isUpdating;
	private String lastValue;
	private String newValue;
	private String entireValue;
	private String newEntireValue;
	private String decimalValue;
	private String newDecimalValue;
	private BaseAdapter baseAdapter;

	public CurrencyEditText(Context context) {
		super(context);
    	this.context = context;
		initialize();
	}
	
    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    	this.context = context;
        initialize();
    }
    
    public CurrencyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    	this.context = context;
        initialize();
    }
    
    private void initialize() {
		symbol           = "";
		entireAmount     = 7;
		entireCharacter  = ".";
		decimalAmount    = 2;
		decimalCharacter = ",";
		isUpdating       = false;
		
		setInputType(InputType.TYPE_CLASS_NUMBER);
		setGravity(Gravity.RIGHT);
		
		setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View view, MotionEvent event) {
	            switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	            	setFocusable(true);
	            	requestFocus();
	            	setSelection(CurrencyEditText.this.getText().length());
	                break;
	            case MotionEvent.ACTION_UP:
	            	view.performClick();
	                break;
	            default:
	                break;
	            }
	            
	            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
            	inputMethodManager.showSoftInput(CurrencyEditText.this, 0);
            	
	            return true;
	        }
	    });
		
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (isUpdating) {
                    isUpdating = false;
                    return;
                }
				
				newValue       = lastValue;
				entireValue    = s.toString().split(splitCharacter())[0].replaceAll("[^0-9]", "");
				newEntireValue = entireValue;
				if (decimalAmount > 0) {
					if (s.toString().split(splitCharacter()).length >= 2) {
						decimalValue = s.toString().split(splitCharacter())[1];
					} else {
						decimalValue = "";
					}
					newDecimalValue = "";
				}
				
				if (lastValue.length() <= s.toString().length()) {
					int entireValueLength = entireValue.length();					
					if (decimalAmount == 0) {
						entireValueLength--;
					}
					
					if (entireAmount > entireValueLength) {
						if (entireValueIsZero()) {
							if (decimalAmount > 0) {
								newEntireValue = String.valueOf(decimalValue.charAt(0));
							} else {
								newEntireValue = "0";
							}
						} else {
							if (decimalAmount > 0) {
								newEntireValue += String.valueOf(decimalValue.charAt(0));
			                } else {
			                	newEntireValue = removeLeftZeros(newEntireValue);
			                }						
						}
						
						if (decimalAmount > 0) {
							for (int i = 1; i < decimalValue.length(); i++) {
								newDecimalValue += String.valueOf(decimalValue.charAt(i));
			                }
						}
						
						changeNewValue();	
					}
				} else {
					newEntireValue = "";
					
					if (decimalAmount > 0) {
	                	for (int i = 0; i < (entireValue.length() - 1); i++) {
	                		newEntireValue += String.valueOf(entireValue.charAt(i));
		                }
                	} else {
                		newEntireValue = entireValue;
                	}
					
					if (newEntireValue.equals("")) {
						newEntireValue = "0";
                	}		
					
	                if (decimalAmount > 0) {
	                	newDecimalValue = String.valueOf(entireValue.charAt(entireValue.length() - 1)) + decimalValue;
	                	newDecimalValue = missingZeros() + newDecimalValue;
	                }
	                
	                changeNewValue();
				}
				
                isUpdating = true;
                lastValue  = newValue;
                
                setText(newValue);
                setSelection(newValue.length());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (baseAdapter != null) {
					if (baseAdapter instanceof EscolherFormaPagamentoAdapter) {
						((EscolherFormaPagamentoAdapter) baseAdapter).setTroco(getValue());
					}
				}				
			}
		});
		
		initializeText();
    }
    
    private void initializeText() {
    	isUpdating = true;
    	if (decimalAmount > 0) {
    		setText(symbol + "0" + decimalCharacter + decimalZeros());
    	} else {
    		setText(symbol + "0");
    	}
    	setSelection(getText().toString().length());
		lastValue = getText().toString();
    }
	
	private String decimalZeros() {
		String zeros = "";
		
		for (int i = 0; i < decimalAmount; i++) {
			zeros += "0";
		}
		
		return zeros;
	}
	
	private String missingZeros() {
		String zeros = "";
		
		for (int i = 0; i < (decimalAmount - newDecimalValue.length()); i++) {
			zeros += "0";
		}
		
		return zeros;
	}
	
	private String withoutDecimalCharacter(String value) {
		return value.replaceAll("[" + decimalCharacter + "]", "");
	}
	
	private String removeLeftZeros(String value) {
		String newValue = "";
		boolean found   = false;
		
    	for (int i = 0; i < value.length(); i++) {
    		if (value.charAt(i) != '0') {
    			newValue += String.valueOf(value.charAt(i));
    			found = true;
    		} else {
    			if (found) {
    				newValue += String.valueOf(value.charAt(i));
    			} 
    		}
    	}
    			
    	return newValue;		
	}
	
	private void changeNewValue() {
		newEntireValue = addCharacterEntire(newEntireValue);
		
		if (decimalAmount > 0) {
			newValue = withoutDecimalCharacter(symbol) + newEntireValue + decimalCharacter + newDecimalValue;
        } else {
        	newValue = withoutDecimalCharacter(symbol) + newEntireValue; 
        }	
		
	}
	
	private String addCharacterEntire(String valueEntire) {
		int count             = 0;
		String newValueEntire = "";
		
		for (int i = (valueEntire.length() - 1); i >= 0; i--) {
			if (count == 3) {
				newValueEntire = entireCharacter + newValueEntire;
				count          = 0;
			}
			
			newValueEntire = String.valueOf(valueEntire.charAt(i)) + newValueEntire;
			count++;
		}
		
		return newValueEntire;
	}
	
	private boolean entireValueIsZero() {
		try {
			return (Integer.valueOf(entireValue) == 0);
		} catch (Exception ex) {
			return false;
		}
	}
	
	public void changeParameters(String symbol, int entireAmount, String entireCharacter, int decimalAmount, String decimalCharacter, BaseAdapter baseAdapter) {
		this.symbol           = symbol;
		this.entireAmount     = entireAmount;
		this.entireCharacter  = entireCharacter;
		this.decimalAmount    = decimalAmount;
		this.decimalCharacter = decimalCharacter;
		this.baseAdapter      = baseAdapter;
		
		initializeText();
	}
	
	private String splitCharacter() {
		String splitCharacter = decimalCharacter;
		
		if (splitCharacter.equals(".")) {
			splitCharacter = "\\" + splitCharacter;
		}
		
		return splitCharacter;
	}
	
	public void setValue(double value) {
		NumberFormat decimalFormat = new DecimalFormat("0." + decimalZeros());
		String newValue            = decimalFormat.format(value).replace(",", decimalCharacter);
		
		entireValue = newValue.split(splitCharacter())[0];
		if (decimalAmount > 0) {
			if (newValue.split(splitCharacter()).length >= 2) {
				decimalValue = newValue.split(splitCharacter())[1];
			} else {
				decimalValue = "0";
			}
		}
		
    	isUpdating = true;
    	if (decimalAmount > 0) {
    		setText(symbol + addCharacterEntire(entireValue) + decimalCharacter + decimalValue);
    	} else {
    		setText(symbol + addCharacterEntire(String.valueOf((int) value)));
    	}
    	setSelection(getText().toString().length());
		lastValue = getText().toString();
	}
	
	public double getValue() {
		String value = getText().toString();
		
		value = value.replace(symbol, "");
		value = value.replace(entireCharacter, "");
		value = value.replace(decimalCharacter, ".");
		
		return Double.parseDouble(value);
	}
}
