package com.ngbm.joynt.custom_ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.ngbm.joynt.R;

public class MySpinner extends Dialog {

	public static MySpinner show(Context context, CharSequence title,
			CharSequence message) {
		return show(context, title, message, false);
	}

	public static MySpinner show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static MySpinner show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static MySpinner show(Context context, CharSequence title,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		MySpinner dialog = new MySpinner(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		/* The next line will add the ProgressBar to the dialog. */
		// dialog.addContentView(new ProgressBar(context), new LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.setContentView(R.layout.myspinner);
		((TextView)dialog.findViewById(R.id.text)).setText(message);
		dialog.show();

		return dialog;
	}

	public MySpinner(Context context) {
		super(context, R.style.MyProgress);
	}
}
