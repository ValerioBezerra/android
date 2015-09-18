package br.com.encontredelivery.dialog;

import br.com.encontredelivery.R;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ProgressoDialog extends Dialog {
	
	private Context context;    
	private TextView txtMensagem;    
	private View view;    

	public ProgressoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progresso);
        
        view = getWindow().getDecorView();
        view.setBackgroundResource(android.R.color.transparent);
          
        txtMensagem = (TextView) findViewById(R.id.txtMensagem);
        
        setCancelable(false);
	}
	
	 public void setMessage(CharSequence message) {    
         txtMensagem.setText(message);    
//         txtMensagem.setMovementMethod(ScrollingMovementMethod.getInstance());
     }
	 
     public void setMessage(int messageId) {    
    	 txtMensagem.setText(context.getResources().getString(messageId));    
//    	 txtMensagem.setMovementMethod(ScrollingMovementMethod.getInstance());
     }	 

}
