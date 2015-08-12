package br.com.encontredelivery.dialog;


import br.com.encontredelivery.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

public class FiltroPedidoDialog extends Dialog {
	private View view;   
	private RadioButton rbUltimosPedidos;
	private RadioButton rbTodosPedidos;
	private Button btnCancelar;
	
	private android.view.View.OnClickListener clickCancelar = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};
	
	public FiltroPedidoDialog(Context context, int opcao) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_filtro_pedido);
        
        view             = getWindow().getDecorView();    
        rbUltimosPedidos = (RadioButton) findViewById(R.id.rbUltimosPedidos);
        rbTodosPedidos   = (RadioButton) findViewById(R.id.rbTodosPedidos);
        btnCancelar      = (Button) findViewById(R.id.btnCancelar);
        
        rbUltimosPedidos.setChecked(opcao == 0);
        rbTodosPedidos.setChecked(opcao == 1);
        
        view.setBackgroundResource(android.R.color.transparent);    
        
        btnCancelar.setOnClickListener(clickCancelar);
        setCancelable(false);
	}
	
	public int getOpcao() {
		if (rbUltimosPedidos.isChecked()) {
			return 0;
		} 
		
		return 1;
	}

}
