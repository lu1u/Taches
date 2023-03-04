package com.lpi.taches;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.lpi.taches.utils.DateUtilitaires;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditAlarmeFragment extends Fragment
{


	private Animation _zoomIn;
	private Animation _zoomOut;

	public void setAlarmeActive(boolean active)
	{
		_swActive.setChecked(active);
		updateUI(false);
	}

	public void setAlarme(Calendar calendar)
	{
		final int y = calendar.get(Calendar.YEAR);
		final int mo = calendar.get(Calendar.MONTH);
		final int d = calendar.get(Calendar.DAY_OF_MONTH);
		final int h = calendar.get(Calendar.HOUR_OF_DAY);
		final int mi = calendar.get(Calendar.MINUTE);
		setDate(y, mo, d, true);
		setTime(h, mi);
		updateUI(false);
	}

	public interface Listener
	{
		void changeActive(boolean active);

		void changeDate(int y, int m, int j);

		void changeHeure(int h, int m);
	}

	private Listener _listener;


	private TextView _tvDate;
	private TextView _tvHeure;
	private DatePicker _pDate;
	private TimePicker _pHeure;
	private Switch _swActive;

	public EditAlarmeFragment()
	{
		// Required empty public constructor
	}

	public void setListener(@Nullable Listener l)
	{
		_listener = l;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		_zoomIn = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
		_zoomOut = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);

//		if (getArguments() != null)
//		{
//			mParam1 = getArguments().getString(ARG_PARAM1);
//			mParam2 = getArguments().getString(ARG_PARAM2);
//		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_edit_alarme, container, false);

		// Retrouver les controles
		_swActive = v.findViewById(R.id.swActive);
		LinearLayout lBoutons = v.findViewById(R.id.idLayoutBoutons);
		_tvDate = v.findViewById(R.id.tvDate);
		_tvHeure = v.findViewById(R.id.tvHeure);
		_pDate = v.findViewById(R.id.pDate2);
		_pHeure = v.findViewById(R.id.pHeure2);
		// Bouton actif: montrer/cacher les boutons date/heure
		_swActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override public void onCheckedChanged(CompoundButton compoundButton, boolean b)
			{
				if (b)
					montre(lBoutons);
				else
					cache(lBoutons);

				if (_listener != null)
					_listener.changeActive(b);

				//updateUI(true);
			}
		});

		// Date
		_tvDate.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				_pDate.setVisibility(View.VISIBLE);
				_pHeure.setVisibility(View.GONE);
			}
		});

		// Heure
		_tvHeure.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				_pDate.setVisibility(View.GONE);
				_pHeure.setVisibility(View.VISIBLE);
			}
		});

		// Date picker
		_pDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener()
		{
			@Override public void onDateChanged(DatePicker datePicker, int y, int m, int d)
			{
				setDate(y, m, d, false);
				if (_listener != null)
					_listener.changeDate(y, m, d);
			}
		});

		// Time picker
		_pHeure.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getActivity()));
		_pHeure.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
		{
			@Override public void onTimeChanged(TimePicker timePicker, int h, int m)
			{
				setTime(h, m);
				if (_listener != null)
					_listener.changeHeure(h, m);
			}
		});

		v.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override public void onFocusChange(View view, boolean b)
			{
				updateUI(b);
			}
		});
		return v;
	}

	/***
	 * Mise a jour des controles de l'interface en fonction de l'etat de l'alarme et du focus
	 * @param focus
	 */
	public void updateUI(boolean focus)
	{
		//if (focus)
		{
			hideSoftKeyboard(getActivity());
			// Montrer uniquement les champs texte DATE et HEURE
			_pDate.setVisibility(View.GONE);
			_pHeure.setVisibility(View.GONE);
			if (_swActive.isChecked())
			{
				cache(_tvHeure);
				cache(_tvDate);

			}
			else
			{
				montre(_tvHeure);
				montre(_tvDate);
			}
		}
//		else
//		{
//			// Montrer uniquement les champs texte DATE et HEURE
//			_pDate.setVisibility(View.GONE);
//			_pHeure.setVisibility(View.GONE);
//			if (_swActive.isChecked())
//			{
//				_tvHeure.setVisibility(View.VISIBLE);
//				_tvDate.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				_tvHeure.setVisibility(View.GONE);
//				_tvDate.setVisibility(View.GONE);
//
//			}
//		}
	}

	private void cache(View view)
	{
		Transition transition = new Fade();
		transition.setDuration(600);
		transition.addTarget(view);

		TransitionManager.beginDelayedTransition( (ViewGroup)view.getParent(), transition);
		view.setVisibility( View.GONE);
	}

	private void montre(View view)
	{
		Transition transition = new Fade();
		transition.setDuration(600);
		transition.addTarget(view);

		TransitionManager.beginDelayedTransition( (ViewGroup)view.getParent(), transition);
		view.setVisibility( View.VISIBLE);
	}

	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isAcceptingText())
		{
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	/***
	 * Met le texte de la date dans le bouton Date
	 * @param y
	 * @param m
	 * @param d
	 */
	private void setDate(int y, int m, int d, boolean updatePicker)
	{
		if (_tvDate != null)
		{
			_tvDate.setText(DateUtilitaires.getDateShort(y, m, d, DateFormat.MEDIUM));
			if (updatePicker)
				_pDate.updateDate(y, m, d);
		}
	}

	/***
	 * Met le texte de la date dans le bouton Date
	 * @param h
	 * @param m
	 */
	private void setTime(int h, int m)
	{
		if (_tvHeure != null)
		{
			_tvHeure.setText(DateUtilitaires.getTimeShort(h, m, DateFormat.SHORT));
			_pHeure.setHour(h);
			_pHeure.setMinute(m);
		}
	}
}