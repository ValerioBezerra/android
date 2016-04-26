package br.com.kingsoft.procureaki.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.widget.LoginButton;

import br.com.kingsoft.procureaki.R;

public class LoginActivity extends AppCompatActivity {

    private LoginButton btnEntrarFacebook;
    private TextView txtAcessoSemLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnEntrarFacebook = (LoginButton) findViewById(R.id.btnEntrarFacebook);
        txtAcessoSemLogin = (TextView) findViewById(R.id.txtAcessoSemLogin);

        txtAcessoSemLogin.setOnClickListener(onClickAcessoSemLogin());
    }

    private View.OnClickListener onClickAcessoSemLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, EncontreMeuEnderecoActivity.class);
                startActivity(intent);
            }
        };
    }
}
