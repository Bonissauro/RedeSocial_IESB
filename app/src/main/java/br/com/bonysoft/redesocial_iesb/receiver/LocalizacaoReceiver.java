package br.com.bonysoft.redesocial_iesb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.com.bonysoft.redesocial_iesb.servicos.EnviaPosicaoService;

/**
 * Created by carlospanarello on 28/09/16.
 */

public class LocalizacaoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, EnviaPosicaoService.class);
        context.startService(startServiceIntent);
    }
}
