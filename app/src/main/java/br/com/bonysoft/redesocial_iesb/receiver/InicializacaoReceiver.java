package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.com.bonysoft.redesocial_iesb.servicos.AlarmeEnvioPosicaoService;

/**
 * Created by panar on 03/10/2016.
 */

public class InicializacaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, AlarmeEnvioPosicaoService.class);
        context.startService(startServiceIntent);
    }
}
