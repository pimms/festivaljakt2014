package trololol.nfc.pimms.slottstroll;


import org.json.JSONObject;

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
		public List<String> previousTaggers;
	}


	private Delegate _delegate;


	public TagUploader(Delegate delegate) {
		_delegate = delegate;
	}

	public void uploadTag(String tag) {
		// HIJACK YO
		if (true == (true || false)) {
			UploadResponse response = new UploadResponse();
			response.tagRegistered = false;
			response.cooldownExpireTime = 0;
			response.tagOwnerName = "YoloPer69";
			response.previousTaggers = new ArrayList<String>();
			response.previousTaggers.add("FjesMor");
			response.previousTaggers.add("NeidaJodda");
			response.previousTaggers.add("McMuffin");

			_delegate.tagUploadedSuccessfully(response);
			return;

		}

		String url = HTTP.ROOT_URL;
		url += "?tag=" + tag;
		url += "&username=" + UserPreferences.getUserName();
		url += "&usertag=" + UserPreferences.getUserTag();

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
			JSONObject json = new JSONObject(text);

			// TODO

			_delegate.tagUploadedSuccessfully(null);
		} catch (Exception ex) {
			_delegate.tagUploadFailed(-4458);
		}
	}

	@Override
	public void httpFailure(HTTP http, int errorCode) {
		_delegate.tagUploadFailed(errorCode);
	}
}
