package com.stien.festivaljakt.slottsfjell;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends BaseActivity {
	private TextView _scoreText;
	private TextView _promptText;
	private TextView _helpText;
	private ListView _listView;
	private LinearLayout _listLayout;


	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_main);

		_scoreText = (TextView)findViewById(R.id.main_label_score);
		_promptText = (TextView)findViewById(R.id.main_label_list_prompt);
		_helpText = (TextView)findViewById(R.id.main_label_help);
		_listView = (ListView)findViewById(R.id.main_listview);
		_listLayout = (LinearLayout)findViewById(R.id.main_listview_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings_info) {
			Intent intent = new Intent(this, InfoActivity.class);
			startActivity(intent);
		}

		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		_scoreText.setText("" + UserPreferences.getUserScore());

		if (!ScanApplication.RELEASE_READY) {
			new AlertDialog.Builder(this)
					.setTitle("Kommer snart!")
					.setMessage("Alt er ikke helt på plass enda, men det er på vei. Ting forventes å være klappende klart i løpet av onsdag formiddag. De siste endringene kommer i en oppdatering.")
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					})
					.setPositiveButton("Play Store...", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
							startActivity(intent);
							finish();
						}
					})
					.show();
			return;
		}

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			new AlertDialog.Builder(this)
					.setTitle("Beklager!")
					.setMessage("Telefonen din støtter ikke NFC, som applikasjonen behøver for å fungere.")
					.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					})
					.show();
		} else if (!nfcAdapter.isEnabled()) {
			new AlertDialog.Builder(this)
					.setTitle("NFC er av")
					.setMessage("NFC er skrudd av, og er nødvendig for at applikasjonen skal fungere. Skru på nå?")
					.setNeutralButton("Ikke nå", null)
					.setPositiveButton("Innstillinger", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						}
					})
					.show();
		} else if (UserPreferences.getUserName() == null || UserPreferences.getUserTag() == null) {
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			TagDatabase db = new TagDatabase(ScanApplication.sharedApplicationContext());
			List<String> names = db.getNames();
			_listView.setAdapter(new TagAdapter(db.getNames()));

			if (names.size() == 0) {
				_listLayout.setVisibility(View.INVISIBLE);
				_helpText.setVisibility(View.VISIBLE);
			} else {
				_listLayout.setVisibility(View.VISIBLE);
				_helpText.setVisibility(View.INVISIBLE);
			}
		}
	}
}

