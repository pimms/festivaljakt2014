package trololol.nfc.pimms.slottstroll;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
	private TextView _scoreText;
	private TextView _promptText;
	private ListView _listView;


	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		_scoreText = (TextView)findViewById(R.id.main_label_score);
		_promptText = (TextView)findViewById(R.id.main_label_list_prompt);
		_listView = (ListView)findViewById(R.id.main_listview);
	}

	@Override
	public void onResume() {
		super.onResume();

		_scoreText.setText("" + UserPreferences.getUserScore());

		if (UserPreferences.getUserName() == null || UserPreferences.getUserTag() == null) {
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			TagDatabase db = new TagDatabase(ScanApplication.sharedApplicationContext());
			List<String> names = db.getNames();
			_listView.setAdapter(new TagAdapter(db.getNames()));

			if (names.size() == 0) {
				_promptText.setVisibility(View.INVISIBLE);
			} else {
				_promptText.setVisibility(View.VISIBLE);
			}
		}
	}
}

