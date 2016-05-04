package br.com.kingsoft.procureaki.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.EmpresaAdapter;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.model.Empresa;
import br.com.kingsoft.procureaki.model.Tipo;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.EmpresaRest;

public class EmpresasActivity extends AppCompatActivity {
    private ListView lvEmpresas;
    private TextView txtNenhumEstabelecimentoEncontrado;


    private Segmento segmento;
    private Tipo tipo;
    private String latitude;
    private String longitude;

    private List<Empresa> listaEmpresas;
    private EmpresaAdapter empresaAdapter;


    private Handler handlerErros;
    private Handler handlerCarregarEmpresas;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvEmpresas                        = (ListView) findViewById(R.id.lvEmpresas);
        txtNenhumEstabelecimentoEncontrado = (TextView) findViewById(R.id.txtNenhumEstabelecimentoEncontrado);

        Bundle extras = getIntent().getExtras();

        segmento  = (Segmento) extras.getSerializable("segmento");
        tipo      = (Tipo) extras.getSerializable("tipo");
        latitude  = extras.getString("latitude");
        longitude = extras.getString("longitude");

        iniciarComponentes();

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(EmpresasActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerCarregarEmpresas = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (listaEmpresas.isEmpty()) {
                    lvEmpresas.setVisibility(View.GONE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.VISIBLE);
                } else {
                    lvEmpresas.setVisibility(View.VISIBLE);
                    txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
                    empresaAdapter = new EmpresaAdapter(EmpresasActivity.this, listaEmpresas);
                    lvEmpresas.setAdapter(empresaAdapter);
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

    private void iniciarComponentes() {
        setTitle(segmento.getDescricao() + " -> " + tipo.getDescricao());
        listaEmpresas  = new ArrayList<>();
        empresaAdapter = new EmpresaAdapter(this, listaEmpresas);
        lvEmpresas.setAdapter(empresaAdapter);
        txtNenhumEstabelecimentoEncontrado.setVisibility(View.GONE);
        carregarEmpresas();
    }

    private void carregarEmpresas() {
        progressoDialog = new ProgressoDialog(this);
        progressoDialog.setMessage("Aguarde. Carregando empresas...");
        progressoDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EmpresaRest empresaRest = new EmpresaRest();
                try {
                    listaEmpresas = empresaRest.getEmpresas(segmento.getId(), tipo.getId(),  latitude, longitude, 5);
                    Util.messagem("", handlerCarregarEmpresas);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }


}
