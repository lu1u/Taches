package com.lpi.taches.utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageBoxUtils
	{
	public static final int BOUTON_OK = 1;
	public static final int BOUTON_CANCEL = 2;

	public static void messageBox(@NonNull Context a, @NonNull String titre, @NonNull String texte, int boutons, final @Nullable Listener listener)
		{
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setTitle(titre);
		builder.setMessage(texte);
		if ((boutons & BOUTON_OK) != 0)
			builder.setPositiveButton(a.getResources().getString(android.R.string.ok), (dialog, id) ->
				{
				if (listener != null)
					listener.onOk();
				});

		if ((boutons & BOUTON_CANCEL) != 0)
			builder.setNegativeButton(a.getResources().getString(android.R.string.cancel), (dialog, id) ->
				{
				if (listener != null)
					listener.onCancel();
				});

		// Create the AlertDialog object and return it
		builder.create().show();
		}

	public interface Listener
		{
		void onOk();
		void onCancel();
		}
	}