package com.lpi.taches;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lpi.taches.report.Report;
import com.lpi.taches.report.ReportActivity;

public class DialogAPropos
{
	public static void start(@NonNull final Activity activity)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_apropos, null);
		String message = "Application Id:" + BuildConfig.APPLICATION_ID
				+ "\nBuild type:" + BuildConfig.BUILD_TYPE
				+ "\nVersion name:" + BuildConfig.VERSION_NAME
				+ "\nVersion code:" + BuildConfig.VERSION_CODE
				+ "\nDebug:" + BuildConfig.DEBUG;

		TextView tv = dialogView.findViewById(R.id.textViewDescription);
		tv.setText(message);

		Button bReport = dialogView.findViewById(R.id.bReport);
		if (! Report.GENERER_TRACES)
		{
			bReport.setVisibility(View.GONE);
		}
		else
		{
			bReport.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(View view)
				{
					ReportActivity.start(activity);
					dialogBuilder.dismiss();
				}
			});
		}
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
