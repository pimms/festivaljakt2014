package com.stien.festivaljakt.slottsfjell;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URLEncoder;


public class RegisterActivity extends BaseActivity implements HTTP.Delegate, View.OnClickListener {
	private String _tagID;
	private TextView _idText;
	private EditText _nameText;
	private Button _regButton;



	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_register);

		_idText = (TextView)findViewById(R.id.register_text_id);
		_nameText = (EditText)findViewById(R.id.register_edit_name);
		_regButton = (Button)findViewById(R.id.register_button_register);

		_regButton.setOnClickListener(this);

		ScannerActivity.registerActivity = this;
	}

	@Override
	public void onResume() {
		super.onResume();
		ScannerActivity.registerActivity = this;
	}

	@Override
	public void onStop() {
		super.onStop();
		ScannerActivity.registerActivity = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ScannerActivity.registerActivity = null;
	}


	public void scannerActivityScannedTag(String tag) {
		_tagID = tag;
		_idText.setText(_tagID);
	}


	private void displayAlert(String message) {
		new AlertDialog.Builder(this)
				.setTitle("Heisann, du")
				.setMessage(message)
				.setNeutralButton("Ok", null)
				.show();
	}

	private String getEnteredName() {
		String name = _nameText.getText().toString();
		name = name.trim();
		return name;
	}

	private void registerUser() {
		try {
			String url = HTTP.ROOT_URL;
			url += "register.php";
			url += "?username=" + URLEncoder.encode(getEnteredName(), "UTF-8");
			url += "&usertag=" + ScanApplication.md5(_tagID);
			url += "&userid=" + ScanApplication.uniqueAndroidID();

			HTTP.getWebsite(url, this);
			showProgressHUD();
		} catch (Exception ex) {
			displayAlert("SORRY! Noe gikk forferdelig galt. Prøv igjen en annen dag.");
		}
	}


	/*
	================
	HTTP Delegate
	================
	*/
	@Override
	public void httpSuccess(HTTP http) {
		hideProgressHUD();

		try {
			JSONObject json = new JSONObject(http.getTextContent());
			boolean status = json.getBoolean("status");

			if (status == false) {
				displayAlert(json.getString("message"));
			} else {
				String name = json.getString("username");
				String tag = json.getString("usertag");
				UserPreferences.setUserName(name);
				UserPreferences.setUserTag(tag);

				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		} catch (Exception ex) {
			displayAlert("Noe gikk galt. Prøv igjen senere.");
		}
	}

	@Override
	public void httpFailure(HTTP http, int errorCode) {
		hideProgressHUD();
		displayAlert("Kunne ikke koble til internett. Sjekk innstillingene dine og prøv igjen.");
	}

	/*
	================
	OnClickListener
	================
	*/
	@Override
	public void onClick(View view) {
		if (view == _regButton) {
			if (_tagID == null) {
				displayAlert("Skann chipen på kortet ditt for å fortsette");
			} else if (getEnteredName().length() < 2) {
				displayAlert("Skriv inn et brukernavn for å fortsette");
			} else {
				registerUser();
			}
		}
	}
}
