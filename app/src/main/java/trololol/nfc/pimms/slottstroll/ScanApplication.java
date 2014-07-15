package trololol.nfc.pimms.slottstroll;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import java.security.MessageDigest;

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


	public static String md5(String input) {
		try {
			char[] ch = input.toCharArray();
			byte[] msg = new byte[ch.length];
			for (int i=0; i<ch.length; i++)
				msg[i] = (byte)ch[i];

			StringBuffer hexString = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(msg);

			for (int i = 0; i < hash.length; i++) {
				if ((0xff & hash[i]) < 0x10) {
					hexString.append("0"
							+ Integer.toHexString((0xFF & hash[i])));
				} else {
					hexString.append(Integer.toHexString(0xFF & hash[i]));
				}
			}

			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	public ScanApplication() {
		_sharedInstance = this;
	}
}
