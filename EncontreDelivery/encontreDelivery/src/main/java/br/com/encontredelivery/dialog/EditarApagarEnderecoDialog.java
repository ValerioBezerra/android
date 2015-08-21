package br.com.encontredelivery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import br.com.encontredelivery.R;

public class EditarApagarEnderecoDialog extends Dialog {

	private Context context;
	private View view;

	public EditarApagarEnderecoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_editar_apagar_endereco);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
	}

}
