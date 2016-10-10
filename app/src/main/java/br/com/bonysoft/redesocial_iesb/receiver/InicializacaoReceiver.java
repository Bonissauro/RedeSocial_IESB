package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;
import br.com.bonysoft.redesocial_iesb.utilitarios.Constantes;

/**
 * Created by panar on 03/10/2016.
 */

public class InicializacaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = context.getSharedPreferences(Constantes.SERVICO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean (Constantes.SERVICO_ENVIO_EXEC, true);
        editor.putBoolean (Constantes.SERVICO_REC_EXEC, true);
        editor.commit();

        Intent startServiceIntent = new Intent(context, AlarmeEnvioPosicaoService.class);
        context.startService(startServiceIntent);

    }
}
