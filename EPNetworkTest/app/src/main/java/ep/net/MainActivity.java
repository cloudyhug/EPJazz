package ep.net;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView filetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filetext = findViewById(R.id.filetext);

        Intent intent = new Intent(this, WiFiDirectActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                long t = data.getLongExtra("time", -1);
                long st = data.getLongExtra("startingTime", -1);
                filetext.setText("" + t + " & " + st);
            }
        }
    }

}
