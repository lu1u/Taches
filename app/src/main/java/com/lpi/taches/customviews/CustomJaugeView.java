package com.lpi.taches.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.R;


/**
 * Vue personnalisée pour afficher une jauge avec le pourcentage affiché au milieu
 */
public class CustomJaugeView extends View
{
	//Attributs
	private int _valeur;
	private int _minimum;
	private int _maximum;
	private TextPaint _paintText;
	private Drawable _drawableFond;
	private Drawable _drawableJauge;
	private int _paddingLeft;
	private int _paddingTop;
	private int _paddingRight;
	private int _paddingBottom;
	private Float _paddingJauge;

	public CustomJaugeView(Context context)
	{
		super(context);
		init(null, 0);
	}

	public CustomJaugeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public CustomJaugeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomJaugeView, defStyle, 0);
		_minimum = a.getInt(R.styleable.CustomJaugeView_CJVMinimum, 0);
		_maximum = a.getInt(R.styleable.CustomJaugeView_CJVMaximum, 100);
		_valeur = a.getInt(R.styleable.CustomJaugeView_CJVValeur, 50);
		_drawableFond = getDrawable(a, R.styleable.CustomJaugeView_CJVDrawableFond);
		_drawableJauge = getDrawable(a, R.styleable.CustomJaugeView_CJVDrawableJauge);
		_paddingJauge = a.getDimension(R.styleable.CustomJaugeView_CJVPaddingJauge, 0);
		float tailleTexte = a.getFloat(R.styleable.CustomJaugeView_CVJTailleTexte, 20);
		int couleurTexte = a.getColor(R.styleable.CustomJaugeView_CVJCouleurTexte, Color.BLACK);
		a.recycle();
		verifieValeurs();

		// Set up a default TextPaint object
		_paintText = new TextPaint();
		_paintText.setColor(couleurTexte);
		_paintText.setTextSize(tailleTexte);
		_paintText.setStyle(Paint.Style.STROKE);
		_paintText.setAntiAlias(true);

		_paddingLeft = getPaddingLeft();
		_paddingTop = getPaddingTop();
		_paddingRight = getPaddingRight();
		_paddingBottom = getPaddingBottom();
	}

	/***
	 * S'assurer que les valeurs, min et max sont correctes
	 */
	private void verifieValeurs()
	{
		if (_minimum >= _maximum)
			_maximum = _minimum + 1;
		if (_valeur < _minimum)
			_valeur = _minimum;
		if (_valeur > _maximum)
			_valeur = _maximum;
	}

	/***
	 * Charge un drawable depuis les ressources
	 * @param a
	 * @param id
	 * @return
	 */
	private Drawable getDrawable(@NonNull TypedArray a, @IdRes int id)
	{
		if (a.hasValue(id))
		{
			Drawable d = a.getDrawable(id);
			if (d != null)
				d.setCallback(null);
			return d;
		}

		return null;
	}


	/***
	 * Dessine le controle
	 * @param canvas
	 */
	@Override	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);


		int contentWidth = getWidth() - _paddingLeft - _paddingRight;
		int contentHeight = getHeight() - _paddingTop - _paddingBottom;

		drawDrawable(canvas, _paddingLeft, _paddingTop, _paddingLeft + contentWidth, _paddingTop + contentHeight, _drawableFond);

		float jaugeL = contentWidth - _paddingJauge * 2.0f;
		float jaugeH = contentHeight - _paddingJauge * 2.0f;
		float ratio = (float) (_valeur - _minimum) / (float) (_maximum - _minimum);
		drawDrawable(canvas, (int) (_paddingLeft + _paddingJauge), (int) (_paddingTop + _paddingJauge), (int) (_paddingLeft + _paddingJauge + (jaugeL * ratio)), (int) (_paddingTop + _paddingJauge + jaugeH), _drawableJauge);
		afficheTextCentre(canvas, _paintText, _valeur + "%", _paddingLeft, _paddingTop, _paddingLeft + contentWidth, _paddingTop + contentHeight);
	}

	/***********************************************************************************************
	 * Dessine un texte centré dans un rectangle
	 * @param canvas
	 * @param paint
	 * @param text
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 ***********************************************************************************************/
	private static void afficheTextCentre(@NonNull Canvas canvas, @NonNull Paint paint, @NonNull String text, int left, int top, int right, int bottom)
	{
		Rect r = new Rect(left, top, right, bottom);
		final int cHeight = r.height();
		final int cWidth = r.width();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.getTextBounds(text, 0, text.length(), r);
		final float x = cWidth / 2f - r.width() / 2f - r.left;
		final float y = cHeight / 2f + r.height() / 2f - r.bottom;
		canvas.drawText(text, left + x, top + y, paint);
	}

	/***
	 * Dessiner un drawable
	 * @param canvas
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param drawable
	 */
	private void drawDrawable(@NonNull final Canvas canvas, int left, int top, int right, int bottom, @Nullable final Drawable drawable)
	{
		if (drawable == null)
			return;

		drawable.setBounds(left, top, right, bottom);
		drawable.draw(canvas);
	}


	public void setMinimum(int min)
	{
		_minimum = min;
		verifieValeurs();
		invalidate();
	}


	public void setMaximum(int max)
	{
		_maximum = max;
		verifieValeurs();
		invalidate();
	}


	public void setValeur(int val)
	{
		_valeur = val;
		verifieValeurs();
		invalidate();
	}
}