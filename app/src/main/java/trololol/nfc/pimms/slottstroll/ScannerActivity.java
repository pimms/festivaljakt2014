package trololol.nfc.pimms.slottstroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScannerActivity extends Activity implements NfcReaderTask.Delegate, TagUploader.Delegate {
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 *
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


	public static RegisterActivity registerActivity;

	private TextView _statusText;
	private NfcAdapter _nfcAdapter;
	private boolean _hasLaunchedMain;

	private static String MIME_TEXT_PLAIN = "text/plain";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan);

		_statusText = (TextView)findViewById(R.id.scan_label_status);
		_nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (!_nfcAdapter.isEnabled()) {
			new AlertDialog.Builder(this)
					.setTitle("Error")
					.setMessage("NFC is disabled. Enable NFC in the settings menu and try again.")
					.setNeutralButton("Ok", null)
					.show();
		}

		handleIntent(getIntent());
    }

	@Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, _nfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, _nfcAdapter);
		super.onPause();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NfcReaderTask(this).execute(tag);
			} else {
				Log.d("MainActivity", "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = NfcV.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NfcReaderTask(this).execute(tag);
					break;
				}
			}
	    }
	}



    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}



	private void launchMainActivity(final int msDelay) {
		if (_hasLaunchedMain)
			return;

		_hasLaunchedMain = true;

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		}, msDelay);
	}


	/*
	================
	NfcReaderTask Delegate
	================
	*/
	@Override
	public void nfcIdSuccess(String nfcId) {
		_statusText.setText("Scan successful");

		if (registerActivity != null) {
			registerActivity.scannerActivityScannedTag(nfcId);
			finish();
		} else {
			TagUploader uploader = new TagUploader(this);
			uploader.uploadTag(nfcId);
		}
	}

	@Override
	public void nfcIdFailed() {
		_statusText.setText("Failed to scan");
	}


	/*
	================
	TagUploader Delegate
	================
	*/
	@Override
	public void tagUploadedSuccessfully(TagUploader.UploadResponse response) {
		_statusText.setText("Success! Great success!");

		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.view_scan_report, null);
		TextView statusText = (TextView)view.findViewById(R.id.scan_report_status);
		TextView remaining = (TextView)view.findViewById(R.id.scan_report_remaining);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT
		);
		addContentView(view, params);

		if (response.tagRegistered == true) {
			statusText.setText("JUHU");
			remaining.setText("");
		} else {
			statusText.setText("æsjda");
			remaining.setText("TT:MM gjenstår");
		}
	}

	@Override
	public void tagUploadFailed(int errorCode) {
		_statusText.setText("Failed to upload :(");
		launchMainActivity(1000);
	}
}
