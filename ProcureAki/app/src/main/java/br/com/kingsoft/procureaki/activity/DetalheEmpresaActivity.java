package br.com.kingsoft.procureaki.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.adapter.EmpresaAdapter;
import br.com.kingsoft.procureaki.adapter.FormaPagamentoAdapter;
import br.com.kingsoft.procureaki.adapter.HorarioAdapter;
import br.com.kingsoft.procureaki.adapter.ItemAdapter;
import br.com.kingsoft.procureaki.dialog.EnderecoDialog;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.HorarioFormaPagamentoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Empresa;
import br.com.kingsoft.procureaki.model.FormaPagamento;
import br.com.kingsoft.procureaki.model.Horario;
import br.com.kingsoft.procureaki.model.Item;
import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.model.Tipo;
import br.com.kingsoft.procureaki.util.LruBitmapCache;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.view.NoScrollGridView;
import br.com.kingsoft.procureaki.webservice.EmpresaRest;

public class DetalheEmpresaActivity extends AppCompatActivity {
    private TextView txtNomeEmpresa;
    private TextView txtRuaNumero;
    private TextView txtBairroCidade;
    private Button btnSeguir;
    private NetworkImageView nivImagem;
    private TextView txtSobre;
    private NoScrollGridView gvItens;
    private ImageView imgFone;
    private ImageView imgCompartilhar;
    private ImageView imgSite;
    private Button btnFormaPagamentoHorario;


    private Empresa empresa;
    private List<Item> listaItens;
    private ItemAdapter itemAdapter;
    private List<Horario> listaHorarios;
    private List<FormaPagamento> listaFormasPagamento;

    private Handler handlerErros;
    private Handler handlerCarregarEmpresas;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_empresa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNomeEmpresa   = (TextView) findViewById(R.id.txtNomeEmpresa);
        txtRuaNumero     = (TextView) findViewById(R.id.txtRuaNumero);
        txtBairroCidade  = (TextView) findViewById(R.id.txtBairroCidade);
        btnSeguir        = (Button) findViewById(R.id.btnSeguir);
        nivImagem        = (NetworkImageView) findViewById(R.id.nivImagem);
        txtSobre         = (TextView) findViewById(R.id.txtSobre);
        gvItens          = (NoScrollGridView) findViewById(R.id.gvItens);
        btnFormaPagamentoHorario  = (Button) findViewById(R.id.btnFormaPagamentoHorario);

        Bundle extras = getIntent().getExtras();
        empresa       = (Empresa) extras.getSerializable("empresa");

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

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(DetalheEmpresaActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        txtNomeEmpresa.setText(empresa.getNome());
        txtRuaNumero.setText(empresa.getEndereco().getLogradouro() + ", " + empresa.getEndereco().getNumero());
        txtBairroCidade.setText(empresa.getEndereco().getBairro().getNome() + ". " +
                                empresa.getEndereco().getBairro().getCidade().getNome() + ", " +
                                empresa.getEndereco().getBairro().getCidade().getUf());
        txtSobre.setText(empresa.getDetalhamento());

        nivImagem.setVisibility(View.VISIBLE);
        nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
        nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
        nivImagem.setErrorImageResId(R.drawable.ic_launcher);

        txtNomeEmpresa.setShadowLayer(2, 0, 0, Color.BLACK);
        txtRuaNumero.setShadowLayer(2, 0, 0, Color.BLACK);
        txtBairroCidade.setShadowLayer(2, 0, 0, Color.BLACK);

        carregarItens();
        carregarHorarios();
        carregarFormasPagamento();

        btnFormaPagamentoHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HorarioFormaPagamentoDialog horarioFormaPagamentoDialog = new HorarioFormaPagamentoDialog(DetalheEmpresaActivity.this);
                horarioFormaPagamentoDialog.setListaHorarios(listaHorarios);
                horarioFormaPagamentoDialog.setListaFormaPagamento(listaFormasPagamento);
                horarioFormaPagamentoDialog.show();
            }
        });
    }

    private void carregarItens() {
        listaItens = new ArrayList<>();

        Item item = new Item();
        item.setId(1);
        item.setDescricao("Wifi");
        listaItens.add(item);

        item = new Item();
        item.setId(2);
        item.setDescricao("Reserva");
        listaItens.add(item);

        item = new Item();
        item.setId(3);
        item.setDescricao("Delivery");
        listaItens.add(item);

        itemAdapter = new ItemAdapter(this, listaItens);
        gvItens.setAdapter(itemAdapter);

    }

    private void carregarHorarios() {
        listaHorarios = new ArrayList<>();

        Horario horario = new Horario();
        horario.setCodigo(0);
        horario.setHoraInicial("18:00");
        horario.setHoraFinal("02:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(1);
        horario.setHoraInicial("12:00");
        horario.setHoraFinal("22:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(2);
        horario.setHoraInicial("12:00");
        horario.setHoraFinal("22:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(3);
        horario.setHoraInicial("12:00");
        horario.setHoraFinal("22:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(4);
        horario.setHoraInicial("12:00");
        horario.setHoraFinal("22:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(5);
        horario.setHoraInicial("12:00");
        horario.setHoraFinal("22:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(6);
        horario.setHoraInicial("17:00");
        horario.setHoraFinal("02:00");
        listaHorarios.add(horario);

        horario = new Horario();
        horario.setCodigo(7);
        horario.setHoraInicial("18:00");
        horario.setHoraFinal("03:00");
        listaHorarios.add(horario);
    }

    private void carregarFormasPagamento() {
        listaFormasPagamento = new ArrayList<>();

        FormaPagamento formaPagamento = new FormaPagamento();
        formaPagamento.setId(1);
        formaPagamento.setDescricao("Dinheiro");
        formaPagamento.setIcone("paki_iconnota");
        listaFormasPagamento.add(formaPagamento);

        formaPagamento = new FormaPagamento();
        formaPagamento.setId(2);
        formaPagamento.setDescricao("Cartão Master");
        formaPagamento.setIcone("paki_iconmaster");
        listaFormasPagamento.add(formaPagamento);

        formaPagamento = new FormaPagamento();
        formaPagamento.setId(3);
        formaPagamento.setDescricao("Cartão Visa");
        formaPagamento.setIcone("paki_iconvisa");
        listaFormasPagamento.add(formaPagamento);

        formaPagamento = new FormaPagamento();
        formaPagamento.setId(3);
        formaPagamento.setDescricao("Cartão Hiper");
        formaPagamento.setIcone("paki_iconhiper");
        listaFormasPagamento.add(formaPagamento);
    }

}
