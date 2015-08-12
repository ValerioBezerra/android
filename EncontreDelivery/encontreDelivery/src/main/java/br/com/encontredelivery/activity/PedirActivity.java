package br.com.encontredelivery.activity;


import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.dialog.EnderecoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.EnderecoRest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PedirActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
	private Button btnMeusEnderecos;
	
	private Cliente cliente;
	
	private List<Endereco> listaEnderecos;
	
	private ConfirmacaoDialog confirmacaoDialog;
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private EnderecoDialog enderecoDialog;
	
	private Handler handlerErros;
	private Handler handlerCarregarLocalizacoes;
	
	private LocationManager locationManager;
	private Location location;
	
	private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
	
	private static final int REQUEST_LOCALIZE_GPS_ACTIVITY       = 0;
	private static final int REQUEST_ENCONTRAR_ENDERECO_ACTIVITY = 1;
	private static final int REQUEST_MEUS_ENDERECOS_ACTIVITY     = 2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedir);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		location        = null;
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);		
		
		btnMeusEnderecos = (Button) findViewById(R.id.btnMeusEnderecos);
		
		cliente    = new ClienteDao(this).getCliente();
		
		if (cliente == null) {
			btnMeusEnderecos.setVisibility(View.GONE);
		} else {
			btnMeusEnderecos.setVisibility(View.VISIBLE);
		}
		
		
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
		
		handlerCarregarLocalizacoes = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				
				if (listaEnderecos.isEmpty()) {
			        erroAvisoDialog.setTitle("Aviso");
			        erroAvisoDialog.setMessage("- Nenhuma localização encontrada.");
			        erroAvisoDialog.show();
				} else {
					enderecoDialog = new EnderecoDialog(PedirActivity.this);
					enderecoDialog.setTitle("Onde você se encontra?");
					enderecoDialog.setCliente(cliente);
					enderecoDialog.setListaEnderecos(listaEnderecos);
					enderecoDialog.show();
				}
				
				location = null;
			}
		};
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		                   .addApi(LocationServices.API)
						   .addConnectionCallbacks(this)
                           .addOnConnectionFailedListener(this)
                           .build();
		
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);		
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient.isConnected()) {
			startLocationUpdate();
		}
	}	

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
    
    @Override
    public void onConnected(Bundle bundle) {
    	location = getLocation();
    	startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
    	Log.d("result:", "connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	Log.d("result:", "not connected with GoogleApiClient");
    }

    @Override
    public void onLocationChanged(Location location) {
       this.location = location; 
    }	
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == REQUEST_LOCALIZE_GPS_ACTIVITY) {
			clickLocalizeGPS(null);
		}
		
		if (requestCode == REQUEST_ENCONTRAR_ENDERECO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
		
		if (requestCode == REQUEST_MEUS_ENDERECOS_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}
	
	public void clickLocalizeGPS(View view) {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			confirmacaoDialog.setTitle("Aviso");
			confirmacaoDialog.setMessage("Localizaçãoo desativada. Para podermos te encontrar é preciso ativar a localização, " +
					                     "deseja ativá-la?");
			confirmacaoDialog.show();
			
			Button btnSim = (Button) confirmacaoDialog.findViewById(R.id.btnSim);
			
			btnSim.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					confirmacaoDialog.dismiss();
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, REQUEST_LOCALIZE_GPS_ACTIVITY);					
				}
			});			
		} else {
			progressoDialog.setMessage("Aguarde. Carregando possíveis localizações...");
			progressoDialog.show();
			
			new Handler().postDelayed(new Runnable(){           
	            public void run() {
			    	location = getLocation();
			    	
					if (location != null) {
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
						        EnderecoRest enderecoRest = new EnderecoRest();
						        try {
						        	listaEnderecos = enderecoRest.getEnderecosGPS(location.getLatitude(), location.getLongitude());
						        	Util.messagem("", handlerCarregarLocalizacoes);
								} catch (Exception ex) {
									Util.messagem(ex.getMessage(), handlerErros);
								}
							}
				    	});
				    	
				    	thread.start();
					} else {
						progressoDialog.dismiss();
						erroAvisoDialog.setTitle("Erro");
						erroAvisoDialog.setMessage("Não foi possível te localizar no momento. Espere um instante e tente novamente.");
						erroAvisoDialog.show();
					}
	            }
	        }, 1000);  
		}

	}
	
	public void clickEncontrarEndereco(View view) {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", cliente);
		Intent intent = new Intent(this, EncontrarEnderecoActivity.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_ENCONTRAR_ENDERECO_ACTIVITY);
	}	
	
	public void clickMeusEnderecos(View view) {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", cliente);
		Intent intent = new Intent(this, MeusEnderecosActivity.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_MEUS_ENDERECOS_ACTIVITY);
	}
	
    private Location getLocation() {
    	return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
    
    private void startLocationUpdate() {
    	LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    
}
