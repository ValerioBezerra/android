package br.com.encontredelivery.service;


import br.com.encontredelivery.R;
import br.com.encontredelivery.activity.DetalhesPedidoActivity;
import br.com.encontredelivery.activity.DashboardActivity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends IntentService {
	
	public GCMIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras            = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String tipoMsg           = gcm.getMessageType(intent);
		
        if (!extras.isEmpty()) { 
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(tipoMsg)) {
                enviarNotification("Send error: " + extras.toString(), "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(tipoMsg)) {
                enviarNotification("Deleted messages on server: " + extras.toString(), "");
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(tipoMsg)) {
                enviarNotification(extras.getString("msg"), extras.getString("idPedido"));
                Log.i("TAG", "Receiver: " + extras.toString());
            }
        }
	}
	
	private void enviarNotification(String msg, String idPedido) {
        if (idPedido != null) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = null;
            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);


            if (idPedido.equals("")) {
                contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, DashboardActivity.class), 0);
            } else {
                Bundle extrasIntent = new Bundle();
                extrasIntent.putLong("idPedido", Long.parseLong(idPedido));

                Intent intent = new Intent(this, DetalhesPedidoActivity.class);
                intent.putExtras(extrasIntent);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            notifBuilder.setContentIntent(contentIntent);
            notifBuilder.setContentTitle(getString(R.string.app_name));
            notifBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
            notifBuilder.setColor(getResources().getColor(R.color.primary_500));
            notifBuilder.setSmallIcon(R.drawable.ic_notification);
            notifBuilder.setContentText(msg);
            notifBuilder.setDefaults(Notification.DEFAULT_ALL);
            notifBuilder.setAutoCancel(true);
            notifBuilder.setWhen(System.currentTimeMillis());
            notificationManager.notify(0, notifBuilder.build());
        }
    }

}
