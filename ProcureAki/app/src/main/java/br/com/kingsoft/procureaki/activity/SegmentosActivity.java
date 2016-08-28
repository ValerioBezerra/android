package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.SegmentoAdapter;
import br.com.kingsoft.procureaki.adapter.SpinnerArrayAdapter;
import br.com.kingsoft.procureaki.dao.ClienteDao;
import br.com.kingsoft.procureaki.dialog.ConfirmacaoEnderecoDialog;
import br.com.kingsoft.procureaki.dialog.EnderecoDialog;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Bairro;
import br.com.kingsoft.procureaki.model.Cidade;
import br.com.kingsoft.procureaki.model.Cliente;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.BairroRest;
import br.com.kingsoft.procureaki.webservice.CidadeRest;
import br.com.kingsoft.procureaki.webservice.EnderecoRest;
import br.com.kingsoft.procureaki.webservice.SegmentoRest;

public class SegmentosActivity extends AppCompatActivity {
    private TextView txtSaudacao;
    private TextView txtData;
    private EditText edtBuscarProdutos;
    private Button btnPesquisar;
    private GridView gvSegmentos;

    private String latitude;
    private String longitude;
    private List<Segmento> listaSegmentos;
    private SegmentoAdapter segmentoAdapter;


    private Handler handlerErros;
    private Handler handlerCarregarSegmentos;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSaudacao       = (TextView) findViewById(R.id.txtSaudacao);
        txtData           = (TextView) findViewById(R.id.txtData);
        edtBuscarProdutos = (EditText) findViewById(R.id.edtBuscarProdutos);
        btnPesquisar      = (Button) findViewById(R.id.btnPesquisar);
        gvSegmentos       = (GridView) findViewById(R.id.gvSegmentos);


        Bundle extras = getIntent().getExtras();

        latitude  = extras.getString("latitude");
        longitude = extras.getString("longitude");

        iniciarComponentes();

        gvSegmentos.setOnItemClickListener(onItemClickSegmentos());

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(SegmentosActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerCarregarSegmentos = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (listaSegmentos.isEmpty()) {
                    gvSegmentos.setVisibility(View.GONE);
                } else {
                    gvSegmentos.setVisibility(View.VISIBLE);
                    segmentoAdapter = new SegmentoAdapter(SegmentosActivity.this, listaSegmentos);
                    gvSegmentos.setAdapter(segmentoAdapter);
                }

                progressoDialog.dismiss();
            }
        };

        verificarData();

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testarCampos()) {
                    Bundle extras = new Bundle();
                    extras.putString("buscarProduto", edtBuscarProdutos.getText().toString());
                    Intent intent = new Intent(SegmentosActivity.this, BuscarProdutoActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Cliente cliente = new ClienteDao(SegmentosActivity.this).getCliente();

        if (cliente != null)
            getMenuInflater().inflate(R.menu.segmentos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.acao_configuracao) {
            Bundle extras = new Bundle();
            extras.putSerializable("cliente", new ClienteDao(SegmentosActivity.this).getCliente());
            Intent intent = new Intent(SegmentosActivity.this, CadastroActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }

        switch (item.getItemId()) {
            case android.R.id.home: finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private AdapterView.OnItemClickListener onItemClickSegmentos() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle extras = new Bundle();
                extras.putSerializable("segmento", listaSegmentos.get(i));
                extras.putString("latitude", latitude);
                extras.putString("longitude", longitude);
                Intent intent = new Intent(SegmentosActivity.this, EmpresasActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        };
    }

    private void iniciarComponentes() {
        listaSegmentos  = new ArrayList<>();
        segmentoAdapter = new SegmentoAdapter(this, listaSegmentos);
        gvSegmentos.setAdapter(segmentoAdapter);
        carregarSegmentos();
    }

    private void carregarSegmentos() {
        progressoDialog = new ProgressoDialog(this);
        progressoDialog.setMessage("Aguarde. Carregando segmentos...");
        progressoDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SegmentoRest segmentoRest = new SegmentoRest();
                try {
                    listaSegmentos = segmentoRest.getSegmentos();
                    Util.messagem("", handlerCarregarSegmentos);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }

    private void verificarData() {
        Locale locale = new Locale("pt","BR");
        Date date = new Date();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        String dataExtenso = "";

        switch (gregorianCalendar.get(Calendar.DAY_OF_WEEK)) {
            case 1: dataExtenso = "DOMINGO";
                    break;
            case 2: dataExtenso = "SEGUNDA";
                    break;
            case 3: dataExtenso = "TERÇA";
                    break;
            case 4: dataExtenso = "QUARTA";
                    break;
            case 5: dataExtenso = "QUINTA";
                    break;
            case 6: dataExtenso = "SEXTA";
                    break;
            case 7: dataExtenso = "SÁBADO";
                    break;
            default: dataExtenso = "DOMINGO";
                     break;
        }

        DecimalFormat decimalFormat = new DecimalFormat("00");

        dataExtenso = dataExtenso + ", " + new SimpleDateFormat("dd/MM").format(date);
        txtData.setText(dataExtenso);

        int hora = gregorianCalendar.get(Calendar.HOUR_OF_DAY);

        if (hora >= 0 && hora < 12)
          txtSaudacao.setText("BOM DIA!");
        else if (hora >= 12 && hora < 18)
            txtSaudacao.setText("BOA TARDE!");
        else
            txtSaudacao.setText("BOA NOITE!");
    }

    private boolean testarCampos() {
        boolean erros  = false;

        edtBuscarProdutos.setError(null);

        if (edtBuscarProdutos.getText().toString().trim().equals("")) {
            edtBuscarProdutos.setError("Informe o filtro para pesquisar o produto.");
            erros = true;
        }

        return !erros;
    }
}
