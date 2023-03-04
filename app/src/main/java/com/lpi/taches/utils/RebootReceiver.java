package com.lpi.taches.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.lpi.taches.alarmes.NotificationsManager;
import com.lpi.taches.report.Report;


/***
 * Evite de remettre l'application en activite si l'option"Reactiver après redémarrage" n'est pas cochée
 */
public class RebootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(@NonNull final Context context, @NonNull  final Intent intent)
	{
		Report r = Report.getInstance(context);
		r.log(Report.DEBUG, "RebootReceiver.onReceive " + intent.getAction());

		if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			NotificationsManager.getInstance(context).onBoot();
		}
		else
			r.log(Report.WARNING, "Action inconnue dans RebootReceiver.onReceive " + intent.getAction());
	}
}
