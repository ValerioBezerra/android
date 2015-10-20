package br.com.encontredelivery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.fragment.RestaurantesFragment;
import br.com.encontredelivery.fragment.MeusDadosFragment;
import br.com.encontredelivery.fragment.MeusPedidosFragment;
import br.com.encontredelivery.fragment.MeusVouchersFragment;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.view.ScrimInsetsFrameLayout;
import br.com.encontredelivery.view.UserAvatar;

public class NavigationDrawerActivity extends ActionBarActivity implements View.OnClickListener {
	private DrawerLayout drawerLayout;
	private LinearLayout llEntriesRootView;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private ScrimInsetsFrameLayout scrimInsetsFrameLayout;

	private UserAvatar imgFacebook;
	private TextView txtNome;
	private TextView txtEnderecoNumero;
	private TextView txtBairroCidadeUF;
	private LinearLayout llComplementoReferencia;
	private TextView txtComplementoReferencia;
	private FrameLayout flRestaurantes;
	private FrameLayout flMeusPedidos;
	private FrameLayout flMeusDados;
	private FrameLayout flMeusVouchers;
	private FrameLayout flInicio;
	private FrameLayout flLogin;
	private FrameLayout flLogout;

	private RestaurantesFragment estabelecimentosFragment;
	private MeusPedidosFragment meusPedidosFragment;
	private MeusDadosFragment meusDadosFragment;
	private MeusVouchersFragment meusVouchersFragment;

	public static Endereco endereco;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

	public static final int REQUEST_LOGIN_ACTIVITY                      = 0;
	public static final int REQUEST_ESTABELECIMENTOS_CATEGORIA_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_drawer);

		if (savedInstanceState == null) {
			initialise();
			initialiseAccountView(true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NavigationDrawerActivity.REQUEST_LOGIN_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				initialiseAccountView(false);
				getSupportActionBar().setTitle(R.string.restaurantes);
				flRestaurantes.setSelected(true);
			}
		} else if (requestCode == NavigationDrawerActivity.REQUEST_ESTABELECIMENTOS_CATEGORIA_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				long idPedido =  data.getExtras().getLong("idPedido");
				if (idPedido != -1) {
					Intent intent = new Intent(this, PedidoEnviadoActivity.class);
					intent.putExtras(data.getExtras());
					startActivity(intent);
					finish();
				}
			}
		}
	}

	private void initialise() {
		Bundle extras = getIntent().getExtras();
		endereco 	  = (Endereco) extras.getSerializable("endereco");

		final Toolbar TOOLBAR = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(TOOLBAR);

		llEntriesRootView = (LinearLayout)findViewById(R.id.llEntriesRootView);

		imgFacebook              = (UserAvatar) findViewById(R.id.imgFacebook);
		txtNome                  = (TextView) findViewById(R.id.txtNome);
		txtEnderecoNumero        = (TextView) findViewById(R.id.txtEnderecoNumero);
		txtBairroCidadeUF        = (TextView) findViewById(R.id.txtBairroCidadeUF);
		llComplementoReferencia  = (LinearLayout) findViewById(R.id.llComplementoReferencia);
		txtComplementoReferencia = (TextView) findViewById(R.id.txtComplementoReferencia);

		flRestaurantes = (FrameLayout) findViewById(R.id.flRestaurantes);
		flMeusPedidos  = (FrameLayout) findViewById(R.id.flMeusPedidos);
		flMeusDados    = (FrameLayout) findViewById(R.id.flMeusDados);
		flMeusVouchers = (FrameLayout) findViewById(R.id.flMeusVouchers);
		flInicio       = (FrameLayout) findViewById(R.id.flInicio);
		flLogin        = (FrameLayout) findViewById(R.id.flLogin);
		flLogout       = (FrameLayout) findViewById(R.id.flLogout);

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

		drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_700));

		scrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);
		actionBarDrawerToggle  = new ActionBarDrawerToggle(this, drawerLayout, TOOLBAR, R.string.app_name, R.string.app_name) {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, 0);
			}
		};

		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}

		actionBarDrawerToggle.syncState();

		flRestaurantes.setOnClickListener(this);
		flMeusPedidos.setOnClickListener(this);
		flMeusDados.setOnClickListener(this);
		flMeusVouchers.setOnClickListener(this);
		flInicio.setOnClickListener(this);
		flLogin.setOnClickListener(this);
		flLogout.setOnClickListener(this);


		estabelecimentosFragment = new RestaurantesFragment();
		meusPedidosFragment      = new MeusPedidosFragment();
		meusDadosFragment        = new MeusDadosFragment();
		meusVouchersFragment     = new MeusVouchersFragment();

		getSupportActionBar().setTitle(R.string.restaurantes);
		flRestaurantes.setSelected(true);

		getSupportFragmentManager().beginTransaction().add(R.id.content_frame, estabelecimentosFragment).commit();
	}

	private void initialiseAccountView(boolean create) {
		Cliente cliente = new ClienteDao(this).getCliente();

		if ((endereco.getNumero() != null) && (!endereco.getNumero().equals("")))
			txtEnderecoNumero.setText(endereco.getLogradouro() + ", " + endereco.getNumero());
		else
			txtEnderecoNumero.setText(endereco.getLogradouro());

		txtBairroCidadeUF.setText(endereco.getBairro().getNome() + ". " + endereco.getBairro().getCidade().getNome() + "-" + endereco.getBairro().getCidade().getUf());

		if (endereco.getComplemento().trim().equals(""))
			llComplementoReferencia.setVisibility(View.GONE);
		else
			txtComplementoReferencia.setText(endereco.getComplemento());

		flMeusPedidos.setVisibility(View.GONE);
		flMeusDados.setVisibility(View.GONE);
		flMeusVouchers.setVisibility(View.GONE);
		flLogin.setVisibility(View.GONE);
		flLogout.setVisibility(View.GONE);

		if (cliente == null) {
			txtNome.setText("Olá, Visitante.");

			flLogin.setVisibility(View.VISIBLE);
		} else {
			if ((cliente.getIdFacebook() != null) && (!cliente.getIdFacebook().equals(""))) {
				imgFacebook.setImageUrl("https://graph.facebook.com/" + cliente.getIdFacebook() + "/picture?type=large", imageLoader);
				imgFacebook.setDefaultImageResId(R.drawable.ic_launcher);
				imgFacebook.setErrorImageResId(R.drawable.ic_launcher);
			}

			txtNome.setText(cliente.getNome());

			flMeusPedidos.setVisibility(View.VISIBLE);
			flMeusDados.setVisibility(View.VISIBLE);
			flMeusVouchers.setVisibility(View.VISIBLE);
			flLogout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.flAccountView) {
			drawerLayout.closeDrawer(GravityCompat.START);
		} else {
			if (!v.isSelected()) {
				onRowPressed((FrameLayout) v);

				switch (v.getId()) {
					case R.id.flRestaurantes: {
						if (getSupportActionBar() != null)
							getSupportActionBar().setTitle(R.string.restaurantes);

						v.setSelected(true);
						getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, estabelecimentosFragment).commit();
						break;
					}
					case R.id.flMeusPedidos: {
						if (getSupportActionBar() != null)
							getSupportActionBar().setTitle(R.string.meus_pedidos);

						v.setSelected(true);
						getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, meusPedidosFragment).commit();
						break;
					}
					case R.id.flMeusDados: {
						if (getSupportActionBar() != null)
							getSupportActionBar().setTitle(R.string.meus_dados);

						v.setSelected(true);
						getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, meusDadosFragment).commit();
						break;
					}
					case R.id.flMeusVouchers: {
						if (getSupportActionBar() != null)
							getSupportActionBar().setTitle(R.string.meus_vouchers);

						v.setSelected(true);
						getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, meusVouchersFragment).commit();
						break;
					}
					case R.id.flInicio: {
						finish();
						break;
					}
					case R.id.flLogin: {
						Intent intent = new Intent(this, LoginActivity.class);
						startActivityForResult(intent, REQUEST_LOGIN_ACTIVITY);
						break;
					}
					case R.id.flLogout: {
						ClienteDao clienteDao = new ClienteDao(NavigationDrawerActivity.this);
						clienteDao.apagar();
						finish();
						break;
					}

					default: break;
				}
			} else {
				drawerLayout.closeDrawer(GravityCompat.START);
			}
		}
	}

	private void onRowPressed(FrameLayout pressedRow)  {
		if (pressedRow.getTag() != getResources().getString(R.string.tag_nav_drawer_special_entry)) {
			for (int i = 0; i < llEntriesRootView.getChildCount(); i++)  {
				View currentView               = llEntriesRootView.getChildAt(i);
				boolean currentViewIsMainEntry = currentView.getTag() == getResources().getString(R.string.tag_nav_drawer_main_entry);

				if (currentViewIsMainEntry) {
					if (currentView == pressedRow)
						currentView.setSelected(true);
					else
						currentView.setSelected(false);
				}
			}
		}

		drawerLayout.closeDrawer(GravityCompat.START);
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		else
			super.onBackPressed();
	}
}
