package trololol.nfc.pimms.slottstroll;

import android.app.Application;
import android.content.Context;

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



	public ScanApplication() {
		_sharedInstance = this;
	}
}
