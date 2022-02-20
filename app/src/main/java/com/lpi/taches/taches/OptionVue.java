package com.lpi.taches.taches;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.lpi.taches.R;


public class OptionVue
	{
	public static final int OPTION_VUE_TOUTES = 0;
	public static final int OPTION_VUE_COMPLETES = 1;
	public static final int OPTION_VUE_INCOMPLETES = 2;

	public interface Listener
		{
		void onSelection(int selection);
		}


	public static void Start(@NonNull final Activity activity, int optionSelectionnee, @NonNull final Listener listener)
		{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.option_vue, null);

		int id = getIdFromOption(optionSelectionnee);

		RadioGroup rGroup = dialogView.findViewById(R.id.rgVue);
		rGroup.check(id);

		setListener(dialogBuilder, dialogView, R.id.rbVueToutes, OPTION_VUE_TOUTES, listener);
		setListener(dialogBuilder, dialogView, R.id.rbVueCompletes, OPTION_VUE_COMPLETES, listener);
		setListener(dialogBuilder, dialogView, R.id.rbVueIncompletes, OPTION_VUE_INCOMPLETES, listener);

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
			case OPTION_VUE_COMPLETES:
				return R.id.rbVueCompletes;
			case OPTION_VUE_INCOMPLETES:
				return R.id.rbVueIncompletes;
			default:
				return R.id.rbVueToutes;
			}
		}
	}
