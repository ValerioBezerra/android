package br.com.encontredelivery.dialog;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.MeusVouchersAdapter;
import br.com.encontredelivery.model.Voucher;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class VoucherDialog extends Dialog {
	private View view;
	
	private ListView lvVouchers;
	private LinearLayout llVazioVouchers;
	private Button btnCancelar;
	
	private Context context;
	private List<Voucher> listaVouchers;
	private MeusVouchersAdapter meusVouchersAdapter;
	
	
	public VoucherDialog(Context context, List<Voucher> listaVouchers) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_voucher);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
        
        lvVouchers      = (ListView) view.findViewById(R.id.lvVouchers); 
		llVazioVouchers = (LinearLayout) view.findViewById(R.id.llVazioVouchers); 
        btnCancelar     = (Button) findViewById(R.id.btnCancelar);
        
        this.context       = context;
        this.listaVouchers = listaVouchers;
        
        btnCancelar.setOnClickListener(new android.view.View.OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			dismiss();
    		}
    	});
        
        carregarVouchers();
        setCancelable(false);
	}
	
	private void carregarVouchers() {
		meusVouchersAdapter = new MeusVouchersAdapter(context, listaVouchers);
		lvVouchers.setAdapter(meusVouchersAdapter);
		
		if (listaVouchers.isEmpty()) {
			llVazioVouchers.setVisibility(View.VISIBLE);
			lvVouchers.setVisibility(View.GONE);
		} else {
			llVazioVouchers.setVisibility(View.GONE);
			lvVouchers.setVisibility(View.VISIBLE);
		}
	}
}
