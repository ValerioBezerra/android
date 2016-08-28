package br.com.kingsoft.procureaki.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.ProdutoAdapter;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Produto;
import br.com.kingsoft.procureaki.util.LruBitmapCache;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.ProdutoRest;

public class BuscarProdutoActivity extends AppCompatActivity {
    private ImageButton btnBuscarProdutos;
    private EditText edtBuscarProdutos;
    private ListView lvProdutos;
    private TextView txtNenhumProdutoEncontrado;

    private List<Produto> listaProdutos;
    private ProdutoAdapter produtoAdapter;

    private Handler handlerErros;
    private Handler handlerCarregarProdutos;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_produto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnBuscarProdutos          = (ImageButton) findViewById(R.id.btnBuscarProdutos);
        edtBuscarProdutos          = (EditText) findViewById(R.id.edtBuscarProdutos);
        lvProdutos                 = (ListView) findViewById(R.id.lvProdutos);
        txtNenhumProdutoEncontrado = (TextView) findViewById(R.id.txtNenhumProdutoEncontrado);

        Bundle extras = getIntent().getExtras();
        edtBuscarProdutos.setText(extras.getString("buscarProduto"));

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

                erroAvisoDialog = new ErroAvisoDialog(BuscarProdutoActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerCarregarProdutos = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (listaProdutos.isEmpty()) {
                    lvProdutos.setVisibility(View.GONE);
                    txtNenhumProdutoEncontrado.setVisibility(View.VISIBLE);
                } else {
                    lvProdutos.setVisibility(View.VISIBLE);
                    txtNenhumProdutoEncontrado.setVisibility(View.GONE);
                    produtoAdapter = new ProdutoAdapter(BuscarProdutoActivity.this, listaProdutos, imageLoader);
                    lvProdutos.setAdapter(produtoAdapter);
                }

                progressoDialog.dismiss();
            }
        };

        btnBuscarProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carregarProdutos();
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
        listaProdutos  = new ArrayList<>();
        produtoAdapter = new ProdutoAdapter(this, listaProdutos, imageLoader);
        lvProdutos.setAdapter(produtoAdapter);
        txtNenhumProdutoEncontrado.setVisibility(View.GONE);
        carregarProdutos();
    }

    private void carregarProdutos() {
        if (testarCampos()) {
            progressoDialog = new ProgressoDialog(this);
            progressoDialog.setMessage("Aguarde. Carregando produtos...");
            progressoDialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ProdutoRest produtoRest = new ProdutoRest();
                    try {
                        listaProdutos = produtoRest.getProdutos(edtBuscarProdutos.getText().toString());
                        Util.messagem("", handlerCarregarProdutos);
                    } catch (Exception ex) {
                        Util.messagem(ex.getMessage(), handlerErros);
                    }
                }
            });

            thread.start();
        }
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
