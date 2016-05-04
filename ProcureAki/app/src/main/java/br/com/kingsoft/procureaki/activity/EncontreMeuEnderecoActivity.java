package br.com.kingsoft.procureaki.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.SpinnerArrayAdapter;
import br.com.kingsoft.procureaki.dialog.ConfirmacaoDialog;
import br.com.kingsoft.procureaki.dialog.ConfirmacaoEnderecoDialog;
import br.com.kingsoft.procureaki.dialog.EnderecoDialog;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Bairro;
import br.com.kingsoft.procureaki.model.Cidade;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.BairroRest;
import br.com.kingsoft.procureaki.webservice.CidadeRest;
import br.com.kingsoft.procureaki.webservice.EnderecoRest;

public class EncontreMeuEnderecoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private RadioGroup rgOpcao;
    private RadioButton rbEndereco;
    private RadioButton rbCep;
    private RelativeLayout rlEndereco;
    private Spinner spnCidades;
    private Spinner spnBairros;
    private EditText edtLogradouro;
    private RelativeLayout rlCep;
    private EditText edtCEP;
    private Button btnEncontrar;

    private Cidade cidade;
    private List<Cidade> listaCidades;
    private Bairro bairro;
    private List<Bairro> listaBairros;
    private Endereco endereco;
    private List<Endereco> listaEnderecos;


    private Handler handlerErros;
    private Handler handlerCarregarCidades;
    private Handler handlerCarregarBairros;
    private Handler handlerCarregarEnderecos;
    private Handler handlerCarregarEnderecoCEP;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;
    private EnderecoDialog enderecoDialog;
    private ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog;

    private LocationManager locationManager;
    private Location location;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int REQUEST_LOCALIZE_GPS_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encontre_meu_endereco);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        location        = null;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        rgOpcao       = (RadioGroup) findViewById(R.id.rgOpcao);
        rbEndereco    = (RadioButton) findViewById(R.id.rbEndereco);
        rbCep         = (RadioButton) findViewById(R.id.rbCep);
        rlEndereco    = (RelativeLayout) findViewById(R.id.rlEndereco);
        spnCidades    = (Spinner) findViewById(R.id.spnCidades);
        spnBairros    = (Spinner) findViewById(R.id.spnBairros);
        edtLogradouro = (EditText) findViewById(R.id.edtLogradouro);
        rlCep         = (RelativeLayout) findViewById(R.id.rlCep);
        edtCEP        = (EditText) findViewById(R.id.edtCEP);
        btnEncontrar  = (Button) findViewById(R.id.btnEncontrar);

        iniciarComponentes();

        rgOpcao.setOnCheckedChangeListener(onCheckChangeOpcao());
        spnCidades.setOnItemSelectedListener(onItemSelectedCidade());
        spnBairros.setOnItemSelectedListener(onItemSelectedBairro());
        btnEncontrar.setOnClickListener(onClickEncontrar());



        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(EncontreMeuEnderecoActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerCarregarCidades = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SpinnerArrayAdapter<Cidade> cidadeAdapter = new SpinnerArrayAdapter<Cidade>(EncontreMeuEnderecoActivity.this, android.R.layout.simple_spinner_dropdown_item, listaCidades);
                cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnCidades.setAdapter(cidadeAdapter);

                progressoDialog.dismiss();
            }
        };

        handlerCarregarBairros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SpinnerArrayAdapter<Bairro> bairroAdapter = new SpinnerArrayAdapter<Bairro>(EncontreMeuEnderecoActivity.this, android.R.layout.simple_spinner_dropdown_item, listaBairros);
                bairroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnBairros.setAdapter(bairroAdapter);

                progressoDialog.dismiss();
            }
        };

        handlerCarregarEnderecos = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();

                if (listaEnderecos.isEmpty()) {
                    erroAvisoDialog.setTitle("Aviso");
                    erroAvisoDialog.setMessage("- Nenhum endereço encontrado.");
                    erroAvisoDialog.show();
                } else {
                    enderecoDialog = new EnderecoDialog(EncontreMeuEnderecoActivity.this);
                    enderecoDialog.setListaEnderecos(listaEnderecos);
                    enderecoDialog.show();
                }
            }
        };

        handlerCarregarEnderecoCEP = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();

                if (endereco == null) {
                    erroAvisoDialog = new ErroAvisoDialog(EncontreMeuEnderecoActivity.this);
                    erroAvisoDialog.setTitle("Aviso");
                    erroAvisoDialog.setMessage("- Nenhum endereço encontrado.");
                    erroAvisoDialog.show();
                } else {
                    confirmacaoEnderecoDialog = new ConfirmacaoEnderecoDialog(EncontreMeuEnderecoActivity.this);
                    confirmacaoEnderecoDialog.setTitle(getResources().getString(R.string.confirmar_endereco));
                    confirmacaoEnderecoDialog.setCEPLogradouro("(" + Retorno.getMascaraCep(endereco.getCep()) + ") " + endereco.getLogradouro());
                    confirmacaoEnderecoDialog.setBairro(endereco.getBairro().getNome());
                    confirmacaoEnderecoDialog.setCidadeUF(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
                    confirmacaoEnderecoDialog.show();

                    Button btnSim = (Button) confirmacaoEnderecoDialog.findViewById(R.id.btnSim);

                    btnSim.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            confirmacaoEnderecoDialog.dismiss();
                            Bundle extras = new Bundle();
                            extras.putString("latitude", endereco.getLatitude());
                            extras.putString("longitude", endereco.getLongitude());
                            Intent intent = new Intent(EncontreMeuEnderecoActivity.this, SegmentosActivity.class);
                            intent.putExtras(extras);
                            startActivity(intent);
                            iniciarComponentes();
                        }
                    });
                }
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

        ativarGPS();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
            ativarGPS();
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckChangeOpcao() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                if (checkedID == R.id.rbEndereco) {
                    rlEndereco.setVisibility(View.VISIBLE);
                    rlCep.setVisibility(View.GONE);
                    edtLogradouro.requestFocus();
                } else {
                    rlEndereco.setVisibility(View.GONE);
                    rlCep.setVisibility(View.VISIBLE);
                    edtCEP.requestFocus();
                }
            }
        };
    }

    private AdapterView.OnItemSelectedListener onItemSelectedCidade() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cidade = (Cidade) adapterView.getSelectedItem();

                carregarBairros(false, 0);

                if (cidade != null) {
                    if (cidade.getId() != 0) {
                        carregarBairros(true, cidade.getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    private AdapterView.OnItemSelectedListener onItemSelectedBairro() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bairro = (Bairro) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
    }

    private View.OnClickListener onClickEncontrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (testarDados()) {
                    if (rgOpcao.getCheckedRadioButtonId() == R.id.rbEndereco) {
                        carregarEnderecos();
                    } else {
                        carregarEnderecoCEP();
                    }
                }
            }
        };
    }


    public void iniciarComponentes() {
        rbEndereco.setChecked(true);
        rlEndereco.setVisibility(View.VISIBLE);
        edtLogradouro.setText("");
        rlCep.setVisibility(View.GONE);
        edtCEP.setText("");
    }

    private void carregarCidades(final boolean webservice) {
        listaCidades = new ArrayList<Cidade>();

        Cidade cidade = new Cidade();
        cidade.setNome("Cidade");
        listaCidades.add(cidade);

        SpinnerArrayAdapter<Cidade> cidadeAdapter = new SpinnerArrayAdapter<Cidade>(this, android.R.layout.simple_spinner_dropdown_item, listaCidades);
        cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCidades.setAdapter(cidadeAdapter);

        if (webservice) {
            progressoDialog = new ProgressoDialog(this);
            progressoDialog.setMessage("Aguarde. Carregando cidades...");
            progressoDialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CidadeRest cidadeRest = new CidadeRest();
                    try {
                        listaCidades = cidadeRest.getCidades();
                        Util.messagem("", handlerCarregarCidades);
                    } catch (Exception ex) {
                        Util.messagem(ex.getMessage(), handlerErros);
                    }
                }
            });

            thread.start();
        }
    }

    private void carregarBairros(final boolean webservice, final int idCidade) {
        listaBairros = new ArrayList<Bairro>();

        Bairro bairro = new Bairro();
        bairro.setNome("Bairro");
        listaBairros.add(bairro);

        SpinnerArrayAdapter<Bairro> bairroAdapter = new SpinnerArrayAdapter<Bairro>(this, android.R.layout.simple_spinner_dropdown_item, listaBairros);
        bairroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBairros.setAdapter(bairroAdapter);

        if (webservice) {
            progressoDialog = new ProgressoDialog(this);
            progressoDialog.setMessage("Aguarde. Carregando bairros...");
            progressoDialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BairroRest bairroRest = new BairroRest();
                    try {
                        listaBairros = bairroRest.getBairros(idCidade);
                        Util.messagem("", handlerCarregarBairros);
                    } catch (Exception ex) {
                        Util.messagem(ex.getMessage(), handlerErros);
                    }
                }
            });

            thread.start();
        }
    }

    private boolean testarDados() {
        boolean erros   = false;
        String msgErros = "";
        String separador = "";

        if (rgOpcao.getCheckedRadioButtonId() == R.id.rbEndereco) {
            if ((cidade == null) || (cidade.getId() == 0)) {
                erros = true;
                msgErros += separador + "- Escolha uma cidade";
                separador = "\n";
            }

            if ((bairro == null) || (bairro.getId() == 0)) {
                erros = true;
                msgErros += separador + "- Escolha uma bairro";
                separador = "\n";
            }

            edtLogradouro.setError(null);

            if (edtLogradouro.getText().toString().trim().equals("")) {
                edtLogradouro.setError("Logradouro não informado.");
                erros = true;
            }

            if (!msgErros.trim().equals("")) {
                erroAvisoDialog = new ErroAvisoDialog(this);
                erroAvisoDialog.setTitle("Inconsistência(s)");
                erroAvisoDialog.setMessage(msgErros);
                erroAvisoDialog.show();
            }
        } else {
            edtCEP.setError(null);

            if (edtCEP.getText().toString().trim().equals("")) {
                edtCEP.setError("CEP não informado.");
                erros = true;
            } else {
                if (edtCEP.getText().toString().trim().length() < 8) {
                    edtCEP.setError("CEP inválido.");
                    erros = true;
                }
            }
        }

        return !erros;
    }

    private void carregarEnderecos() {
        progressoDialog = new ProgressoDialog(this);
        progressoDialog.setMessage("Aguarde. Carregando enderecos...");
        progressoDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EnderecoRest enderecoRest = new EnderecoRest();
                try {
                    listaEnderecos = enderecoRest.getEnderecos(cidade.getId(), bairro.getId(), edtLogradouro.getText().toString());
                    Util.messagem("", handlerCarregarEnderecos);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }

    private void carregarEnderecoCEP() {
        progressoDialog = new ProgressoDialog(this);
        progressoDialog.setMessage("Aguarde. Encontrando endereco...");
        progressoDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EnderecoRest enderecoRest = new EnderecoRest();
                try {
                    endereco = enderecoRest.getEndereco(edtCEP.getText().toString());
                    Util.messagem("", handlerCarregarEnderecoCEP);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }

    private Location getLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void ativarGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final ConfirmacaoDialog confirmacaoDialog = new ConfirmacaoDialog(this);
            confirmacaoDialog.setTitle("Aviso");
            confirmacaoDialog.setMessage("Localização desativada. Para podermos te encontrar é preciso ativar a localização, " +
                    "deseja ativá-la?");
            confirmacaoDialog.show();

            Button btnSim = (Button) confirmacaoDialog.findViewById(R.id.btnSim);

            btnSim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    confirmacaoDialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_LOCALIZE_GPS_ACTIVITY);
                }
            });
        } else {
            progressoDialog = new ProgressoDialog(this);
            progressoDialog.setMessage("Aguarde. Carregando sua localização...");
            progressoDialog.show();

            new Handler().postDelayed(new Runnable(){
                public void run() {
                    location = getLocation();
                    progressoDialog.dismiss();

                    carregarCidades(true);
                    carregarBairros(false, 0);

                    if (location != null) {
                        Bundle extras = new Bundle();
                        extras.putString("latitude", String.valueOf(location.getLatitude()));
                        extras.putString("longitude", String.valueOf(location.getLongitude()));
                        Intent intent = new Intent(EncontreMeuEnderecoActivity.this, SegmentosActivity.class);
                        intent.putExtras(extras);
                        startActivity(intent);
                        iniciarComponentes();
                    }
                }
            }, 1000);
        }

    }


}
