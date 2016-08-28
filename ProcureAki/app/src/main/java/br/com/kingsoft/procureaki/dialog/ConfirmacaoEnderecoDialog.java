package br.com.kingsoft.procureaki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import br.com.kingsoft.procureaki.R;

public class ConfirmacaoEnderecoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private TextView txtCEPLogradouro;
	private TextView txtBairro;
	private TextView txtCidadeUF;
	private TextView txtComplementoReferencia;
	private Button btnNao;
	private View view;

	private View.OnClickListener clickNao = new View.OnClickListener() {
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
          
        txtTitulo                = (TextView) findViewById(R.id.txtTitulo);
        txtCEPLogradouro         = (TextView) findViewById(R.id.txtCEPLogradouro);
        txtBairro                = (TextView) findViewById(R.id.txtBairro);
        txtCidadeUF              = (TextView) findViewById(R.id.txtCidadeUF);
		txtComplementoReferencia = (TextView) findViewById(R.id.txtComplementoReferencia);
        btnNao                   = (Button) findViewById(R.id.btnNao);

		txtComplementoReferencia.setVisibility(View.GONE);
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

	public void setComplementoReferencia(CharSequence message) {
		if (!message.toString().trim().equals("")) {
			txtComplementoReferencia.setVisibility(View.VISIBLE);
			txtComplementoReferencia.setText("Ref.: " + message);
			txtComplementoReferencia.setMovementMethod(ScrollingMovementMethod.getInstance());
		} else {
			txtComplementoReferencia.setVisibility(View.GONE);
		}
	}


}