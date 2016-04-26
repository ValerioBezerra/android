package br.com.kingsoft.procureaki.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.SpinnerArrayAdapter;
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

public class EncontreMeuEnderecoActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encontre_meu_endereco);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
//                            Bundle extras = new Bundle();
//                            extras.putSerializable("cliente", (Cliente) getActivity().getIntent().getExtras().getSerializable("cliente"));
//                            extras.putSerializable("endereco", endereco);
//
//                            if (getActivity().getIntent().getExtras().getBoolean("adicionar")) {
//                                extras.putBoolean("adicionar", true);
//                                Intent intent = new Intent(getActivity(), ConfirmarEnderecoActivity.class);
//                                intent.putExtras(extras);
//                                startActivityForResult(intent, REQUEST_CONFIRMAR_ENDERECO_ACTIVITY);
//                            } else {
//                                Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
//                                intent.putExtras(extras);
//                                getActivity().startActivity(intent);
//                                getActivity().setResult(Activity.RESULT_OK);
//                                getActivity().finish();
//                            }
                        }
                    });
                }
            }
        };


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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


    private void iniciarComponentes() {
        rbEndereco.setChecked(true);
        rlEndereco.setVisibility(View.VISIBLE);
        rlCep.setVisibility(View.GONE);
        carregarCidades(true);
        carregarBairros(false, 0);
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


}
