package trololol.nfc.pimms.slottstroll;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

	public static SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ScanApplication.sharedApplicationContext());
	}


	public static int getUserScore() {
		return getDefaultSharedPreferences().getInt("score", 0);
	}

	public static void setUserScore(int score) {
		SharedPreferences.Editor edit = getDefaultSharedPreferences().edit();
		edit.putInt("score", score);
		edit.commit();
	}
}
