package br.com.bonysoft.redesocial_iesb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import br.com.bonysoft.redesocial_iesb.servicos.EnviaPosicaoFireBaseService;
import br.com.bonysoft.redesocial_iesb.servicos.ObtemLocalizacaoContatoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

public class ConfiguracaoActivity extends AppCompatActivity {
    private Switch mSwitchEnvio;
    private Switch mSwitchReceber;
    private String emailRegistro;
    private TextView mTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        mSwitchEnvio   = (Switch) findViewById(R.id.switchEnvioLocalizacao);
        mSwitchReceber = (Switch) findViewById(R.id.switchReceberLocalizacao);
        mTexto = (TextView) findViewById(R.id.txtUsuarioRegistrado);

        SharedPreferences sharedPref = this.getSharedPreferences(Constantes.SERVICO, Context.MODE_PRIVATE);
        boolean envio = sharedPref.getBoolean(Constantes.SERVICO_ENVIO_EXEC,false);
        boolean rec = sharedPref.getBoolean(Constantes.SERVICO_REC_EXEC,false);

        SharedPreferences sharedPrefEmail = getApplicationContext().getSharedPreferences(Constantes.CONTA_REGISTRADA, Context.MODE_PRIVATE);
        emailRegistro = sharedPrefEmail.getString(Constantes.CONTA_REGISTRADA,"");

        mSwitchEnvio.setChecked(envio);
        mSwitchReceber.setChecked(rec);

        mTexto.setText(emailRegistro);

        mSwitchEnvio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(Constantes.TAG_LOG,"Ativando o servico de envio de posicao");
                    SharedPreferences sharedPref = getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean (Constantes.SERVICO_ENVIO_EXEC, true);
                    editor.commit();
                } else {
                    Log.d(Constantes.TAG_LOG,"Desativando o servico de envio de posicao");
                    Intent envioServico = new Intent(getApplicationContext(), EnviaPosicaoFireBaseService.class);
                    getApplicationContext().stopService(envioServico);
                }
            }
        });

        mSwitchReceber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(Constantes.TAG_LOG,"Ativando o servico de recebimento de posicao");
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean (Constantes.SERVICO_REC_EXEC, true);
                    editor.commit();
                } else {
                    Log.d(Constantes.TAG_LOG,"Desativando o servico de recebimento de posicao");
                    Intent receberServico = new Intent(getApplicationContext(), ObtemLocalizacaoContatoService.class);
                    getApplicationContext().stopService(receberServico);
                }
            }
        });
    }
}
