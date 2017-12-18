package pntanasis.android.metronome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by maxime on 23/11/2017.
 */

public class MainActivity extends Activity {
    public int bpm = 100;
    private final int minBpm = 40;
    private final int maxBpm = 208;
    private Button boutonPlus;
    private Button boutonMoins;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametrage);

        boutonPlus = (Button) findViewById(R.id.boutonPlus);

        boutonMoins = (Button) findViewById(R.id.boutonMoins);


    }

    private void maxBpmGuard() {
        if(bpm >= maxBpm) {
            boutonPlus.setEnabled(false);
            boutonPlus.setPressed(false);
        } else if(!boutonMoins.isEnabled() && bpm>minBpm) {
            boutonMoins.setEnabled(true);
        }
    }

    public void onPlusClick(View view) {
        TextView bpmText1 = findViewById(R.id.BPM);
        bpm=Integer.valueOf(bpmText1.getText().toString());
        bpm++;
        bpmText1.setText(""+bpm);
        maxBpmGuard();
    }

    private void minBpmGuard() {
        if(bpm <= minBpm) {
            boutonMoins.setEnabled(false);
            boutonMoins.setPressed(false);
        } else if(!boutonPlus.isEnabled() && bpm<maxBpm) {
            boutonPlus.setEnabled(true);
        }
    }

    public void onMinusClick(View view) {
        TextView bpmText1 = findViewById(R.id.BPM);
        bpm=Integer.valueOf(bpmText1.getText().toString());
        bpm--;
        bpmText1.setText(""+bpm);
        minBpmGuard();
    }








    public void page_beatkeaper(View view){
        Intent intent = new Intent(this,MetronomeActivity.class);
        intent.putExtra("bpm",bpm);
        startActivity(intent);
    }

    public void page_lecture(View view){
        Intent intent = new Intent(this,LectureActivity.class);
        intent.putExtra("bpm",bpm);
        startActivity(intent);
    }
}
