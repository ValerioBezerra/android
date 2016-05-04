package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.SegmentoAdapter;
import br.com.kingsoft.procureaki.adapter.SpinnerArrayAdapter;
import br.com.kingsoft.procureaki.dialog.ConfirmacaoEnderecoDialog;
import br.com.kingsoft.procureaki.dialog.EnderecoDialog;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Bairro;
import br.com.kingsoft.procureaki.model.Cidade;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.BairroRest;
import br.com.kingsoft.procureaki.webservice.CidadeRest;
import br.com.kingsoft.procureaki.webservice.EnderecoRest;
import br.com.kingsoft.procureaki.webservice.SegmentoRest;

public class SegmentosActivity extends AppCompatActivity {
    private ListView lvSegmentos;
    private TextView txtNenhumEstabelecimentoEncontrado;

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

        lvSegmentos                        = (ListView) findViewById(R.id.lvSegmentos);
        txtNenhumEstabelecimentoEncontrado = (TextView) findViewById(R.id.txtNenhumEstabelecimentoEncontrado);

        Bundle extras = getIntent().getExtras();

        latitude  = extras.getString("latitude");
        longitude = extras.getString("longitude");

        iniciarComponentes();

        lvSegmentos.setOnItemClickListener(onItemClickSegmentos());

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
                    lvSegmentos.setVisibility(View.GONE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.VISIBLE);
                } else {
                    lvSegmentos.setVisibility(View.VISIBLE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
                    segmentoAdapter = new SegmentoAdapter(SegmentosActivity.this, listaSegmentos);
                    lvSegmentos.setAdapter(segmentoAdapter);
                }

                progressoDialog.dismiss();
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

    private AdapterView.OnItemClickListener onItemClickSegmentos() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle extras = new Bundle();
                extras.putSerializable("segmento", listaSegmentos.get(i));
                extras.putString("latitude", latitude);
                extras.putString("longitude", longitude);
                Intent intent = new Intent(SegmentosActivity.this, TiposActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        };
    }

    private void iniciarComponentes() {
        listaSegmentos  = new ArrayList<>();
        segmentoAdapter = new SegmentoAdapter(this, listaSegmentos);
        lvSegmentos.setAdapter(segmentoAdapter);
        txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
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
                    listaSegmentos = segmentoRest.getSegmentos(latitude, longitude, 5);
                    Util.messagem("", handlerCarregarSegmentos);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }


}
