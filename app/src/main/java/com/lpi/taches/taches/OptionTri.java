package com.lpi.taches.taches;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.lpi.taches.R;


public class OptionTri
	{
	public static final int OPTION_TRI_NOM = 0;
	public static final int OPTION_TRI_CREATION = 1;
	public static final int OPTION_TRI_PRIORITE = 2;
	public static final int OPTION_TRI_ACHEVEMENT = 3;

	public interface Listener
		{
		void onSelection(int selection);
		}


	public static void Start(@NonNull final Activity activity, int optionSelectionnee, @NonNull final Listener listener)
		{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.option_tri, null);

		int id = getIdFromOption(optionSelectionnee);

		RadioGroup rGroup = dialogView.findViewById(R.id.rgOptionsTri);
		rGroup.check(id);

		setListener(dialogBuilder, dialogView, R.id.rbTriNom, OPTION_TRI_NOM, listener);
		setListener(dialogBuilder, dialogView, R.id.rbTriPriorite, OPTION_TRI_PRIORITE, listener);
		setListener(dialogBuilder, dialogView, R.id.rbTriDateCreation, OPTION_TRI_CREATION, listener);
		setListener(dialogBuilder, dialogView, R.id.rbTriAchevement, OPTION_TRI_ACHEVEMENT, listener);

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
		}

	private static void setListener(@NonNull final AlertDialog alertDialog, @NonNull final View dialogView, int id, int optionTri, Listener listener)
		{
		RadioButton rb = dialogView.findViewById(id);
		rb.setOnCheckedChangeListener((compoundButton, b) ->
			                              {
										  if ( b)
											  {
											  listener.onSelection(optionTri);
											  alertDialog.dismiss();
											  }
			                              });
		}

	private static int getIdFromOption(int optionSelectionnee)
		{
		switch (optionSelectionnee)
			{
			case OPTION_TRI_CREATION:
				return R.id.rbTriDateCreation;
			case OPTION_TRI_PRIORITE:
				return R.id.rbTriPriorite;
			case OPTION_TRI_ACHEVEMENT:
				return R.id.rbTriAchevement;
			default:
				return R.id.rbTriNom;
			}
		}
	}
