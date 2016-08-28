package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.LoginButton;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.dao.ClienteDao;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Cliente;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.ClienteRest;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private TextView txtCadastreAgora;
    private TextView txtEsqueciMinhaSenha;
    private TextView txtEntrarComGoogle;
    private LoginButton btnEntrarFacebook;
    private TextView txtEntrarSemCadastro;

    private Cliente cliente;
    private Cliente clienteFacebook;
    private ClienteDao clienteDao;

    private Handler handlerErros;
    private Handler handlerLoginEmail;
    private Handler handlerVerificarFacebook;

    private ProgressoDialog progressoDialog;
    private ErroAvisoDialog erroAvisoDialog;


    private static final int REQUEST_CADASTRO_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Util.showHashKey(this);


        edtEmail             = (EditText) findViewById(R.id.edtEmail);
        edtSenha             = (EditText) findViewById(R.id.edtSenha);
        btnEntrar            = (Button) findViewById(R.id.btnEntrar);
        txtCadastreAgora     = (TextView) findViewById(R.id.txtCadastreAgora);
        txtEsqueciMinhaSenha = (TextView) findViewById(R.id.txtEsqueciMinhaSenha);
        txtEntrarComGoogle   = (TextView) findViewById(R.id.txtEntrarComGoogle);
        btnEntrarFacebook    = (LoginButton) findViewById(R.id.btnEntrarFacebook);
        txtEntrarSemCadastro = (TextView) findViewById(R.id.txtEntrarSemCadastro);

        cliente    = null;
        clienteDao = new ClienteDao(this);

        progressoDialog = new ProgressoDialog(this);


        btnEntrar.setOnClickListener(onClickEntrar());
        txtCadastreAgora.setOnClickListener(onClickCadastreAgora());
        txtEsqueciMinhaSenha.setOnClickListener(onEsqueciMinhaSenha());
        txtEntrarSemCadastro.setOnClickListener(onClickAcessoSemLogin());

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(LoginActivity.this);
                erroAvisoDialog.setTitle("Erro");
                erroAvisoDialog.setMessage(mensagem);
                erroAvisoDialog.show();
            }
        };

        handlerLoginEmail = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressoDialog.dismiss();
                String mensagem = (String) msg.obj;

                erroAvisoDialog = new ErroAvisoDialog(LoginActivity.this);
                erroAvisoDialog.setTitle("Erro");

                if (!mensagem.trim().equals("")) {
                    erroAvisoDialog.setMessage("Erro ao tentar logar. Tente novamente mais tarde.");
                    erroAvisoDialog.show();
                } else {
                    if (cliente == null) {
                        erroAvisoDialog.setMessage("Email ou senha incorretos.");
                        erroAvisoDialog.show();
                    } else {
                        clienteDao.inserir(cliente);
                        verificarCliente();
                    }
                }
            }
        };

        verificarCliente();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CADASTRO_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                verificarCliente();
            } else {
                Session session = Session.getActiveSession();
                if((session != null) && (session.isOpened())){
                    session.closeAndClearTokenInformation();
                }
            }
        }
    }

    private View.OnClickListener onClickEntrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testarCampos()) {
                    progressoDialog = new ProgressoDialog(LoginActivity.this);
                    progressoDialog.setMessage("Aguarde. Efetuando login...");
                    progressoDialog.show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ClienteRest clienteRest = new ClienteRest();
                            try {
                                cliente = clienteRest.getCliente(edtEmail.getText().toString(), Retorno.getMD5(edtSenha.getText().toString()));
                                Util.messagem("", handlerLoginEmail);
                            } catch (Exception ex) {
                                Util.messagem(ex.getMessage(), handlerErros);
                            }
                        }
                    });

                    thread.start();
                }
            }
        };
    }

    private View.OnClickListener onClickCadastreAgora() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putSerializable("cliente", null);
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                intent.putExtras(extras);
                startActivityForResult(intent, REQUEST_CADASTRO_ACTIVITY);
            }
        };
    }

    private View.OnClickListener onEsqueciMinhaSenha() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, EsqueciMinhaSenhaActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener onClickAcessoSemLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putSerializable("cliente", null);
                Intent intent = new Intent(LoginActivity.this, EncontreMeuEnderecoActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        };
    }

    private void verificarCliente() {
        cliente = clienteDao.getCliente();

        if (cliente != null) {
            Bundle extras = new Bundle();
            extras.putSerializable("cliente", cliente);
            Intent intent = new Intent(LoginActivity.this, EncontreMeuEnderecoActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
            finish();
        }
    }

    private boolean testarCampos() {
        boolean erros = false;

        edtEmail.setError(null);
        edtSenha.setError(null);

        if (edtEmail.getText().toString().trim().equals("")) {
            edtEmail.setError("Email não preenchido.");
            erros = true;
        } else if (!Util.validarEmail(edtEmail.getText().toString().trim())) {
            edtEmail.setError("Email inválido.");
            erros = true;
        }
        if (edtSenha.getText().toString().trim().equals("")) {
            edtSenha.setError("Senha não preenchida.");
            erros = true;
        }

        return !erros;
    }

}
