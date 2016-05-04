package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.TipoAdapter;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.model.Tipo;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.TipoRest;

public class TiposActivity extends AppCompatActivity {
    private ListView lvTipos;
    private TextView txtNenhumEstabelecimentoEncontrado;

    private Segmento segmento;
    private String latitude;
    private String longitude;

    private List<Tipo> listaTipos;
    private TipoAdapter tipoAdapter;


    private Handler handlerErros;
    private Handler handlerCarregarTipos;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvTipos                        = (ListView) findViewById(R.id.lvTipos);
        txtNenhumEstabelecimentoEncontrado = (TextView) findViewById(R.id.txtNenhumEstabelecimentoEncontrado);

        Bundle extras = getIntent().getExtras();

        segmento  = (Segmento) extras.getSerializable("segmento");
        latitude  = extras.getString("latitude");
        longitude = extras.getString("longitude");

        iniciarComponentes();

        lvTipos.setOnItemClickListener(onItemClickTipos());

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(TiposActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerCarregarTipos = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (listaTipos.isEmpty()) {
                    lvTipos.setVisibility(View.GONE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.VISIBLE);
                } else {
                    lvTipos.setVisibility(View.VISIBLE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
                    tipoAdapter = new TipoAdapter(TiposActivity.this, listaTipos);
                    lvTipos.setAdapter(tipoAdapter);
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

    private AdapterView.OnItemClickListener onItemClickTipos() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle extras = new Bundle();
                extras.putSerializable("segmento", segmento);
                extras.putSerializable("tipo", listaTipos.get(i));
                extras.putString("latitude", latitude);
                extras.putString("longitude", longitude);
                Intent intent = new Intent(TiposActivity.this, EmpresasActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        };
    }


    private void iniciarComponentes() {
        setTitle(segmento.getDescricao());
        listaTipos  = new ArrayList<>();
        tipoAdapter = new TipoAdapter(this, listaTipos);
        lvTipos.setAdapter(tipoAdapter);
        txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
        carregarTipos();
    }

    private void carregarTipos() {
        progressoDialog = new ProgressoDialog(this);
        progressoDialog.setMessage("Aguarde. Carregando tipos...");
        progressoDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TipoRest tipoRest = new TipoRest();
                try {
                    listaTipos = tipoRest.getTipos(segmento.getId(), latitude, longitude, 5);
                    Util.messagem("", handlerCarregarTipos);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }


}
