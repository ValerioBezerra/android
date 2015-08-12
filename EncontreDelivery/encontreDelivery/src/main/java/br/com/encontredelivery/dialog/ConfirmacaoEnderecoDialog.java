package br.com.encontredelivery.dialog;

import br.com.encontredelivery.R;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmacaoEnderecoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private TextView txtCEPLogradouro;
	private TextView txtBairro;
	private TextView txtCidadeUF;
	private Button btnNao;
	private View view;   
	
	private android.view.View.OnClickListener clickNao = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};

	public ConfirmacaoEnderecoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_confirmacao_endereco);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        txtTitulo        = (TextView) findViewById(R.id.txtTitulo);    
        txtCEPLogradouro = (TextView) findViewById(R.id.txtCEPLogradouro);
        txtBairro        = (TextView) findViewById(R.id.txtBairro);
        txtCidadeUF      = (TextView) findViewById(R.id.txtCidadeUF);
        btnNao           = (Button) findViewById(R.id.btnNao);
    	
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
	 
	 public void setCEPLogradouro(CharSequence message) {    
		 txtCEPLogradouro.setText(message);    
		 txtCEPLogradouro.setMovementMethod(ScrollingMovementMethod.getInstance());    
     }
	 
	 public void setBairro(CharSequence message) {    
		 txtBairro.setText(message);    
		 txtBairro.setMovementMethod(ScrollingMovementMethod.getInstance());    
     }	
	 
	 public void setCidadeUF(CharSequence message) {    
		 txtCidadeUF.setText(message);    
		 txtCidadeUF.setMovementMethod(ScrollingMovementMethod.getInstance());    
     }	
	 
}
