package trololol.nfc.pimms.slottstroll;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView _scoreText;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		_scoreText = (TextView)findViewById(R.id.main_label_score);
	}

	@Override
	public void onResume() {
		super.onResume();

		_scoreText.setText("" + UserPreferences.getUserScore());
	}
}

