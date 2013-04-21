package com.ngbm.joynt.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class SettingsButton extends LinearLayout implements
		View.OnClickListener {
	protected View.OnClickListener a;

	public SettingsButton(Context paramContext) {
		this(paramContext, null);
	}

	public SettingsButton(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	@Override
	public void onClick(View v) {
		if (this.a != null)
			this.a.onClick(this);
	}
}
