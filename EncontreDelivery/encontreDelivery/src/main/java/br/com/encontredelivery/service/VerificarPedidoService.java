package br.com.encontredelivery.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import br.com.encontredelivery.R;
import br.com.encontredelivery.activity.DetalhesPedidoActivity;
import br.com.encontredelivery.webservice.PedidoRest;

public class VerificarPedidoService extends Service {
    public boolean recebido;
    public long idPedidoService;

    private Handler handlerErros;
    private Handler handlerVerificarRecebimento;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handlerErros = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                stopSelf();
            }
        };

        handlerVerificarRecebimento = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("VerificarPedidoThread", "Recebido: " + recebido);

                if (!recebido) {
                    Bundle extrasIntent = new Bundle();
                    extrasIntent.putLong("idPedido", idPedidoService);
                    Intent intent = new Intent(VerificarPedidoService.this, DetalhesPedidoActivity.class);
                    intent.putExtras(extrasIntent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    NotificationManager notificationManager = (NotificationManager) VerificarPedidoService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent contentIntent             = PendingIntent.getActivity(VerificarPedidoService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(VerificarPedidoService.this);
                    String mensagem                         = getString(R.string.verificar_pedido_service);

                    notifBuilder.setContentIntent(contentIntent);
                    notifBuilder.setContentTitle(getString(R.string.app_name));
                    notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mensagem));
                    notifBuilder.setColor(getResources().getColor(R.color.primary_500));
                    notifBuilder.setSmallIcon(R.drawable.ic_notification);
                    notifBuilder.setContentText(mensagem);
                    notifBuilder.setDefaults(Notification.DEFAULT_ALL);
                    notifBuilder.setAutoCancel(true);
                    notifBuilder.setWhen(System.currentTimeMillis());
                    notificationManager.notify(0, notifBuilder.build());
                }

                stopSelf();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            VerificarPedidoThread verificarPedido = new VerificarPedidoThread(bundle.getLong("idPedido"));
            verificarPedido.start();
        } else {
            stopSelf();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    private class VerificarPedidoThread extends Thread {
        private final int TEMPO_PEDIDO = 510;
        private int tempo              = 0;
        private long idPedido;

        VerificarPedidoThread(long idPedido) {
            this.idPedido = idPedido;
        }

        @Override
        public void run() {
            while ((tempo < TEMPO_PEDIDO)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                tempo++;
            }

            try {
                idPedidoService       = idPedido;
                PedidoRest pedidoRest = new PedidoRest();
                recebido = pedidoRest.verificarRecebimento(idPedido);
                handlerVerificarRecebimento.sendMessage(new Message());
            } catch (Exception ex) {
                handlerErros.sendMessage(new Message());
            }
        }
    }
}
