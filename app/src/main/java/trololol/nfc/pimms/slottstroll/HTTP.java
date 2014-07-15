package trololol.nfc.pimms.slottstroll;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.InputConnection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/* HTTP
 * Static interface for retrieving data via HTTP.
 */
public class HTTP {
	public interface Delegate {
		void httpSuccess(HTTP http);
		void httpFailure(HTTP http, int errorCode);
	}

	public static final String ROOT_URL = "http://10.0.0.26/slottsfjell/";


	/*
	================
	Static Methods
	================
	 */
	public static void getWebsite(String url, Delegate delegate) {
        HTTP http = new HTTP(url, delegate);
        http.startDownload();
	}

	public static void getWebsite(String url, Delegate delegate, Map<String, String> postArgs) {
		HTTP http = new HTTP(url, delegate, postArgs);
		http.startDownload();
	}

	public static boolean canConnectToInternet() {
		Context ctx = ScanApplication.sharedApplicationContext();

		ConnectivityManager conmgr = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conmgr.getActiveNetworkInfo();
		if (info != null && info.isConnectedOrConnecting())
			return true;
		return false;
	}


	/*
	================
	DownloadThread
	================
	 */
	private class DownloadThread extends Thread {
		private HTTP _http;
		private String _url;
		private Map<String, String> _postArgs;

		private DownloadThread(HTTP http, String url) {
			_http = http;
			_url = url;
		}

		private DownloadThread(HTTP http, String url, Map<String, String> postArgs) {
			_http = http;
			_url = url;
			_postArgs = postArgs;
		}


		public void run() {
			HttpURLConnection urlConnection = null;
			InputStream inputStream = null;
			byte[] data = null;

			// Don't bother trying unless internet is available
			if (!HTTP.canConnectToInternet()) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						_delegate.httpFailure(HTTP.this, -1);
					}
				});
				return;
			}

			try {
				URL url = new URL(_url);
				urlConnection = (HttpURLConnection)url.openConnection();

				if (_postArgs != null) {
					urlConnection.setRequestMethod("POST");
					urlConnection.setDoOutput(true);

					String args = "";
					for (Map.Entry<String,String> entry : _postArgs.entrySet()) {
						if (args.length() != 0)
							args += "&";
						args += entry.getKey() + "=" + entry.getValue();
					}

					DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
					wr.writeBytes(args);
					wr.flush();
					wr.close();
				}

				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(30000);

				if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					_http.downloadThreadFailed(
							urlConnection.getResponseCode(),
							urlConnection.getResponseMessage());
					return;
				}

				inputStream = new BufferedInputStream(urlConnection.getInputStream());

				/* Read 1kB at a time into the data[]-array. */
				int bytesRead = -1;
				int totalRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					byte[] concat = new byte[totalRead + bytesRead];

					if (data != null && data.length != 0)
						System.arraycopy(data, 0, concat, 0, totalRead);
					System.arraycopy(buffer, 0, concat, totalRead, bytesRead);
					data = concat;

					totalRead += bytesRead;
				}

				inputStream.close();

				final byte[] finaldata = data;
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						if (finaldata != null) {
							_http.downloadThreadCompleted(finaldata);
						} else {
							_http.downloadThreadFailed(-1, "No data retrieved");
						}
					}
				});
			} catch (final Exception ex) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						_http.downloadThreadFailed(-1, ex.getMessage());
					}
				});
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
		}
	}


	/*
	================
	Instance Methods
	================
	 */
	Delegate _delegate;
	String _url;
	DownloadThread _downloadThread;
	byte[] _data;


	private HTTP(String url, Delegate delegate) {
		_url = url;
		_delegate = delegate;
		_downloadThread = new DownloadThread(this, _url);
	}

	private HTTP(String url, Delegate delegate, Map<String, String> postArgs) {
		_url = url;
		_delegate = delegate;
		_downloadThread = new DownloadThread(this, _url, postArgs);
	}

    private void startDownload() {
        _downloadThread.start();
    }


	private void downloadThreadCompleted(byte[] data) {
		_data = data;
		_delegate.httpSuccess(this);
	}

	private void downloadThreadFailed(int responseCode, String msg) {
		Log.e("HTTP", "Download thread failed with code " + responseCode + "\nMessage: " + msg);
		_delegate.httpFailure(this, responseCode);
	}


	public String getTextContent() {
		return new String(_data);
	}

	public byte[] getBinaryContent() {
		return _data;
	}
}

