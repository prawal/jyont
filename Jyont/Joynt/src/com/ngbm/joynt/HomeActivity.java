package com.ngbm.joynt;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeActivity extends BaseScreen {

	private static String TAG = FriendList.class.getCanonicalName();

	private final INgnSipService mSipService;
	private BroadcastReceiver mSipBroadCastRecv;
	private final INgnConfigurationService mConfigurationService;

	public final static String SIP_DOMAIN = "sip2sip.info";
	private final static String USERNAME = "pawan1";
	private final static String SIP_USERNAME = "pawan1";
	private final static String SIP_PASSWORD = "9jsm3d9lhw";
	private final static String SIP_SERVER_PROXY = "proxy.sipthor.net";
	private final static int SIP_SERVER_PORT = 5060;
	
	public HomeActivity() {

		super(SCREEN_TYPE.HOME_T, TAG);
		mSipService = getEngine().getSipService();
		this.mConfigurationService = getEngine().getConfigurationService();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,
				USERNAME);
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU,
				String.format("sip:%s@%s", SIP_USERNAME, SIP_DOMAIN));
		mConfigurationService.putString(
				NgnConfigurationEntry.IDENTITY_PASSWORD, SIP_PASSWORD);
		mConfigurationService.putString(
				NgnConfigurationEntry.NETWORK_PCSCF_HOST, SIP_SERVER_PROXY);
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,
				SIP_SERVER_PORT);
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM,
				SIP_DOMAIN);
		mConfigurationService.putBoolean(
				NgnConfigurationEntry.NETWORK_USE_WIFI, true);
		mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G,
				true);

		// Compute
		if (!mConfigurationService.commit()) {
			Log.e(TAG, "Failed to commit() configuration");
		}
		if (!getEngine().getSipService().isRegistered())
			getEngine().getSipService().register(this);

		((TextView) findViewById(R.id.header_name)).setText(R.string.homeTitle);

		findViewById(R.id.header_left_button).setVisibility(-1);
		findViewById(R.id.header_right_button).setVisibility(-1);

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});
		final Context contaxt = this;
		findViewById(R.id.btnHomeFrieldList).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						
						Intent intent = new Intent(contaxt, FriendList.class);
						contaxt.startActivity(intent);
					}
				});

		mSipBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();

				// Registration Event
				if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT
						.equals(action)) {
					NgnRegistrationEventArgs args = intent
							.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
					if (args == null) {
						Log.e(TAG, "Invalid event args");
						return;
					}
					switch (args.getEventType()) {
					case REGISTRATION_NOK:
					case UNREGISTRATION_OK:
					case REGISTRATION_OK:
					case REGISTRATION_INPROGRESS:
					case UNREGISTRATION_INPROGRESS:
					case UNREGISTRATION_NOK:
					default:
						break;
					}
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		registerReceiver(mSipBroadCastRecv, intentFilter);

	}

	@Override
	protected void onDestroy() {
		if (mSipBroadCastRecv != null) {
			unregisterReceiver(mSipBroadCastRecv);
			mSipBroadCastRecv = null;
		}

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
