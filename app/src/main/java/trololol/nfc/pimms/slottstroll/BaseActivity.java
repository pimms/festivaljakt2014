package trololol.nfc.pimms.slottstroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;


public class BaseActivity extends Activity {
	private ProgressDialog _progressDialog;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}


	protected void showProgressHUD() {
		hideProgressHUD();

		// TODO
		// Localize string
		_progressDialog = new ProgressDialog(this);
		_progressDialog.setMessage("Laster...");
		_progressDialog.setCancelable(false);
		_progressDialog.setIndeterminate(true);
		_progressDialog.show();
	}

	protected void hideProgressHUD() {
		if (_progressDialog != null) {
			_progressDialog.dismiss();
			_progressDialog = null;
		}
	}

}
