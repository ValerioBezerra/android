package br.com.encontredelivery.activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Configuracao;
import br.com.encontredelivery.model.GCM;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ConfiguracaoRest;
import br.com.encontredelivery.webservice.GCMRest;

public class DashboardActivity extends Activity {
	private Button btnMeusPedidos;
	private Button btnMeusDados;
	private Button btnLoginLogout;
	
	private Cliente cliente;
	private ClienteDao clienteDao;
	
	
	private ConfirmacaoDialog confirmacaoDialog;
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private Handler handlerErros;
	private Handler handlerVerificarVersao;
	private Handler handlerRegistrarGCM;
	
	private Configuracao configuracao;
	private String registroID;
	private int versaoRegistrada;
	private int versaoDispositivo;
	
	private static final String REGISTRO_GCM = "REGISTRO_GCM";
	private static final String VERSAO_APP   = "VERSAO_APP";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		btnMeusPedidos = (Button) findViewById(R.id.btnMeusPedidos);
		btnMeusDados   = (Button) findViewById(R.id.btnMeusDados);
		btnLoginLogout = (Button) findViewById(R.id.btnLoginLogout);
		
		cliente    = null;
		clienteDao = new ClienteDao(this);
		
		progressoDialog   = new ProgressoDialog(this);
		erroAvisoDialog   = new ErroAvisoDialog(this);
		confirmacaoDialog = new ConfirmacaoDialog(this);

		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerVerificarVersao = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				
				if (!configuracao.getMensagemInicial().trim().equals("")) {
					erroAvisoDialog.setTitle("Atenção");
					erroAvisoDialog.setMessage(configuracao.getMensagemInicial());
					Button btnOK = (Button) erroAvisoDialog.findViewById(R.id.btnOK);
					btnOK.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							erroAvisoDialog.dismiss();
							if (configuracao.isBloquear()) {
								finish();
							}
						}
					});
					
					erroAvisoDialog.show();
				} else {
					if (configuracao.getVersao() > versaoDispositivo) {
						confirmacaoDialog.setTitle("Atenção");
						confirmacaoDialog.setMessage("Sua versão do Encontre Delivery está desatualizada. Para continuar usando é necessário realizar uma atualização, " +
								                     "deseja atualizar agora?");
						confirmacaoDialog.show();
						
						Button btnSim = (Button) confirmacaoDialog.findViewById(R.id.btnSim);
						Button btnNao = (Button) confirmacaoDialog.findViewById(R.id.btnNao);
						
						btnSim.setOnClickListener(new OnClickListener() {				
							@Override
							public void onClick(View arg0) {
								final String appPackageName = getPackageName();
								try {
								    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
								} catch (android.content.ActivityNotFoundException anfe) {
								    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
								}
								
								confirmacaoDialog.dismiss();
								finish();
							}
						});	
						
						btnNao.setOnClickListener(new OnClickListener() {				
							@Override
							public void onClick(View arg0) {
								confirmacaoDialog.dismiss();
								finish();
							}
						});		
					}
				}
			}
		};
		
		handlerRegistrarGCM = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String mensagem = (String) msg.obj;
				
				if (!mensagem.trim().equals("n")) {
					SharedPreferences sharedPreferences = getSharedPreferences(DashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
					SharedPreferences.Editor editor     = sharedPreferences.edit();
					editor.putString(REGISTRO_GCM, registroID);
					editor.putInt(VERSAO_APP, versaoDispositivo);
			        editor.commit();
				}
			}
		};
		
		if (savedInstanceState == null) {
			verificarVersao();	
		} 
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("versao", 0);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		verificarClienteLogado();
	}	

	public void clickPedir(View view) {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", cliente);
		Intent intent = new Intent(this, PedirActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
	}
	
	public void clickMeusPedidos(View view) {
		Intent intent = new Intent(this, MeusPedidosActivity.class);
		startActivity(intent);
	}
	
	public void clickMeusDados(View view) {
		Intent intent = new Intent(this, MeusDadosActivity.class);
		startActivity(intent);
	}	
	
	public void clickLoginLogout(View view) {
		if (cliente == null) {
			Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
			startActivity(intent);
		} else {
			cliente = null;
			clienteDao.apagar();
			verificarClienteLogado();
		}
	}
	
			
	private void verificarClienteLogado() {
		cliente = clienteDao.getCliente();
		
		if (cliente == null) {
			btnMeusPedidos.setVisibility(View.GONE);
			btnMeusDados.setVisibility(View.GONE);
			btnLoginLogout.setText(getResources().getString(R.string.login));
		} else {
			btnMeusPedidos.setVisibility(View.VISIBLE);
			btnMeusDados.setVisibility(View.VISIBLE);
			btnLoginLogout.setText(getResources().getString(R.string.logout));
		}
	}
	
	private void registrarGCM() {
		SharedPreferences sharedPreferences = getSharedPreferences(DashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		registroID                          = sharedPreferences.getString(REGISTRO_GCM, "");
		versaoRegistrada                    = sharedPreferences.getInt(VERSAO_APP, Integer.MIN_VALUE);
		versaoDispositivo                   = getAppVersion();
		
		if ((registroID.equals("")) || (versaoRegistrada != versaoDispositivo)) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        try {
			    		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(DashboardActivity.this);
			    		String registroIdAntigo                   = registroID;
			    		registroID                                = googleCloudMessaging.register("370904099416");
			    		
			    		GCM gcm = new GCM();
			    		gcm.setRegId(registroID);
			    		gcm.setCliente(cliente);
			    		
			    		GCMRest gcmRest = new GCMRest();
			    		gcmRest.cadastrarAlterar(gcm, registroIdAntigo);
			    		
			    		Util.messagem("", handlerRegistrarGCM);
					} catch (Exception ex) {
					}
				}
	    	});
	    	thread.start();
		}
    }
	
    private void verificarVersao() {
    	registrarGCM();
    	
		progressoDialog.setMessage("Aguarde. Verificando versão...");
		progressoDialog.show();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        ConfiguracaoRest configuracaoRest = new ConfiguracaoRest();
		        try {
		        	configuracao = configuracaoRest.getConfiguracao();
		        	Util.messagem("", handlerVerificarVersao);
				} catch (Exception ex) {
					Util.messagem("Erro ao verificar a versão: " + ex.getMessage(), handlerErros);
				}
			}
    	});
    	thread.start();    		
    }
    
	private int getAppVersion() {
        try {
        	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    		return pInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
