package br.com.kingsoft.procureaki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import br.com.kingsoft.procureaki.R;

public class ConfirmacaoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private TextView txtMensagem;
	private Button btnNao;
	private View view;   
	
	private View.OnClickListener clickNao = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};

	public ConfirmacaoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_confirmacao);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        txtTitulo   = (TextView) findViewById(R.id.txtTitulo);    
        txtMensagem = (TextView) findViewById(R.id.txtMensagem);
        btnNao = (Button) findViewById(R.id.btnNao);
    	
        btnNao.setOnClickListener(clickNao);
        setCancelable(false);
	}
	
	 @Override    
     public void setTitle(CharSequence title) {    
         super.setTitle(title);    
         txtTitulo.setText(title);    
     } 
	 
	 @Override    
     public void setTitle(int titleId) {    
         super.setTitle(titleId);    
         txtTitulo.setText(context.getResources().getString(titleId));    
     }	 
	 
	 public void setMessage(CharSequence message) {    
         txtMensagem.setText(message);    
         txtMensagem.setMovementMethod(ScrollingMovementMethod.getInstance());    
     }
	 
     public void setMessage(int messageId) {    
    	 txtMensagem.setText(context.getResources().getString(messageId));    
    	 txtMensagem.setMovementMethod(ScrollingMovementMethod.getInstance());    
     }	 
     
}
