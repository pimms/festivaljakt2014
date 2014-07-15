package trololol.nfc.pimms.slottstroll;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TagUploader implements HTTP.Delegate {

	public interface Delegate {
		// That the scan was uploaded does not mean that the tag was registered successfully.
		// Check the response object carefully!
		void tagUploadedSuccessfully(UploadResponse response);

		// Something went wrong in uploading the tag
		void tagUploadFailed(int errorCode);
	}

	public static class UploadResponse {
		public boolean tagRegistered;
		public long cooldownExpireTime;
		public String tagOwnerName;
	}


	private Delegate _delegate;


	public TagUploader(Delegate delegate) {
		_delegate = delegate;
	}

	public void uploadTag(String tag) {
		String url = HTTP.ROOT_URL;
		url += "tag.php";
		url += "?tag=" + tag.trim();
		url +="&user=" + UserPreferences.getUserTag();

		HTTP.getWebsite(url, this);
	}


	/*
	================
	HTTP Delegate
	================
	*/
	@Override
	public void httpSuccess(HTTP http) {
		String text = http.getTextContent();

		try {
			UploadResponse response = new UploadResponse();
			JSONObject json = new JSONObject(text);
			if (json.getBoolean("status") == false) {
				_delegate.tagUploadFailed(-349349);
				Log.e("TagUploader", json.getString("message"));
				return;
			}

			int score = json.getInt("score");
			UserPreferences.setUserScore(score);

			response.tagOwnerName = json.getString("owner_name");

			boolean registered = json.getBoolean("tag_registered");
			response.tagRegistered = registered;
			if (!registered) {
				response.cooldownExpireTime = json.getInt("cooldown");
			} else {
				TagDatabase db = new TagDatabase(ScanApplication.sharedApplicationContext());
				db.insertName(response);
			}

			_delegate.tagUploadedSuccessfully(response);
		} catch (Exception ex) {
			_delegate.tagUploadFailed(-4458);
		}
	}

	@Override
	public void httpFailure(HTTP http, int errorCode) {
		_delegate.tagUploadFailed(errorCode);
	}
}
