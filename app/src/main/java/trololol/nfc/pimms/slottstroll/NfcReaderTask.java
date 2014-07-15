package trololol.nfc.pimms.slottstroll;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class NfcReaderTask extends AsyncTask<Tag, Void, Void> {
	public interface Delegate {
		public void nfcIdSuccess(String nfcId);
		public void nfcIdFailed();
	}


	private String _readId;
	private Delegate _delegate;


	public NfcReaderTask(Delegate delegate) {
		_delegate = delegate;
	}


	@Override
	protected Void doInBackground(Tag... tags) {
		NfcV nfcv = NfcV.get(tags[0]);
		if (nfcv == null) {
			Log.e("RFID", "wtf man, null etc");
			return null;
		}

		try {
			nfcv.connect();
			if (!nfcv.isConnected()) {
				Log.e("RFID", "Not connected");
			} else {
				Log.d("RFID", "Connected to tag");

				// Read the ID of the tag
				byte[] id = tags[0].getId();
				if (id != null ) {
					_readId = ScannerActivity.bytesToHex(id);
					Log.d("RFID", "Read ID = " + _readId);
				}

				// Close the tag
				nfcv.close();
			}
		} catch (Exception ex) {
			Log.e("RFID", "wtf ex: " + ex.getMessage());
			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void v) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				if (_readId == null)
					_delegate.nfcIdFailed();
				else
					_delegate.nfcIdSuccess(_readId);
			}
		});
	}
}

