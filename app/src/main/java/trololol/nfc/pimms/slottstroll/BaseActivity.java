package trololol.nfc.pimms.slottstroll;

import android.app.Activity;
import android.app.ProgressDialog;


public class BaseActivity extends Activity {
	private ProgressDialog _progressDialog;


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
