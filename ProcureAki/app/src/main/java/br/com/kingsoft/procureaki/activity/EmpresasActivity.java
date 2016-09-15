package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.EmpresaAdapter;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.model.Empresa;
import br.com.kingsoft.procureaki.model.Tipo;
import br.com.kingsoft.procureaki.util.LruBitmapCache;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.EmpresaRest;

public class EmpresasActivity extends AppCompatActivity {
    private TextView txtSegmento;
    private SeekBar sbDistancia;
    private Button btnVerPromocoes;
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

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private Integer[] arrayDistanciaKm = new Integer[]{2, 5, 10, 30, 50};
    private int distanciaKm = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSegmento                        = (TextView) findViewById(R.id.txtSegmento);
        sbDistancia                        = (SeekBar) findViewById(R.id.sbDistancia);
        btnVerPromocoes                    = (Button) findViewById(R.id.btnVerPromocoes);
        lvEmpresas                         = (ListView) findViewById(R.id.lvEmpresas);
        txtNenhumEstabelecimentoEncontrado = (TextView) findViewById(R.id.txtNenhumEstabelecimentoEncontrado);

        sbDistancia.setProgress(1);

        sbDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("onProgressChanged", "i: " + i);
                distanciaKm = arrayDistanciaKm[i];

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("onStopTrackingTouch", "distanciaKm: " + distanciaKm);
                carregarEmpresas();
            }
        });

        Bundle extras = getIntent().getExtras();

        segmento  = (Segmento) extras.getSerializable("segmento");
        latitude  = extras.getString("latitude");
        longitude = extras.getString("longitude");

        requestQueue = Volley.newRequestQueue(this);
        imageLoader  = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruBitmapCache lruCache = new LruBitmapCache();

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url, bitmap);
            }

            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }
        });


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
                    empresaAdapter = new EmpresaAdapter(EmpresasActivity.this, listaEmpresas, imageLoader);
                    lvEmpresas.setAdapter(empresaAdapter);
                }

                progressoDialog.dismiss();
            }
        };

        lvEmpresas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle extras = new Bundle();
                extras.putSerializable("empresa", listaEmpresas.get(i));
                Intent intent = new Intent(EmpresasActivity.this, DetalheEmpresaActivity.class);
                intent.putExtras(extras);
                startActivity(intent);

            }
        });

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
        txtSegmento.setText(segmento.getDescricao());
        listaEmpresas  = new ArrayList<>();
        empresaAdapter = new EmpresaAdapter(this, listaEmpresas, imageLoader);
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
                    listaEmpresas = empresaRest.getEmpresas(segmento.getId(),  latitude, longitude, distanciaKm);
                    Util.messagem("", handlerCarregarEmpresas);
                } catch (Exception ex) {
                    Util.messagem(ex.getMessage(), handlerErros);
                }
            }
        });

        thread.start();
    }
}
