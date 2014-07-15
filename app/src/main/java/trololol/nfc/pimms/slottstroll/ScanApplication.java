package trololol.nfc.pimms.slottstroll;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by pimms on 7/15/14.
 */
public class ScanApplication extends Application {
	private static ScanApplication _sharedInstance;

	public static ScanApplication sharedApplication() {
		return _sharedInstance;
	}

	public static Context sharedApplicationContext() {
		return _sharedInstance.getApplicationContext();
	}

	public static String uniqueAndroidID() {
		return Settings.Secure.getString(sharedApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
	}



	public ScanApplication() {
		_sharedInstance = this;
	}
}
