package pntanasis.android.metronome;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by maxime on 18/12/2017.
 */

public class LectureActivity extends Activity {

    private Button boutonPlus;
    private Button boutonMoins;
    private TextView currentBeat;
    public int bpm;
    private short noteValue = 1;
    private short beats = 1;
    private double beatSound = 2440;
    private double sound = 6440;
    private final int minBpm = 40;
    private final int maxBpm = 208;
    private AudioManager audio;
    private LectureActivity.MetronomeAsyncTask metroTask;
    private Handler mHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture);

        boutonPlus = (Button) findViewById(R.id.boutonPlus_lecture);
        boutonMoins = (Button) findViewById(R.id.boutonMoins_lecture);

        currentBeat = (TextView) findViewById(R.id.currentBeat_lecture);
        currentBeat.setTextColor(Color.GREEN);

        Intent intent =getIntent();
        bpm=intent.getIntExtra("bpm",0);

        TextView bpmText = (TextView) findViewById(R.id.BPM_lecture);
        bpmText.setText(""+bpm);

        metroTask = new LectureActivity.MetronomeAsyncTask();


    }

    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String message = (String)msg.obj;
                if(message.equals("1"))
                    currentBeat.setTextColor(Color.GREEN);
                else
                    currentBeat.setTextColor(getResources().getColor(R.color.yellow));
                //currentBeat.setText(message);
            }
        };
    }

    private void maxBpmGuard() {
        if(bpm >= maxBpm ) {
            boutonPlus.setEnabled(false);
            boutonPlus.setPressed(false);
        } else if(!boutonMoins.isEnabled() && bpm>minBpm) {
            boutonMoins.setEnabled(true);
        }
    }

    public void onPlusClick(View view) {
        TextView bpmText1 = findViewById(R.id.BPM_lecture);
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
        TextView bpmText1 = findViewById(R.id.BPM_lecture);
        bpm=Integer.valueOf(bpmText1.getText().toString());
        bpm--;
        bpmText1.setText(""+bpm);
        minBpmGuard();
    }

    public synchronized void onStartStopClick_lecture(View view) {
        TextView bpmText1 = findViewById(R.id.BPM_lecture);
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        if(buttonText.equalsIgnoreCase("start")) {
            button.setText(R.string.stop);
            boutonMoins.setEnabled(false);
            boutonMoins.setPressed(false);
            boutonPlus.setEnabled(false);
            boutonPlus.setPressed(false);
            bpmText1.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            else
                metroTask.execute();



        } else {
            button.setText(R.string.start);
            metroTask.stop();
            boutonMoins.setEnabled(true);
            boutonPlus.setEnabled(true);
            bpmText1.setEnabled(true);
            metroTask = new MetronomeAsyncTask();
            Runtime.getRuntime().gc();
        }
    }



    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String> {
        Metronome metronome;


        MetronomeAsyncTask() {
            mHandler = getHandler();
            metronome = new Metronome(mHandler);
        }

        protected String doInBackground(Void... params) {
            metronome.setBeat(beats);
            metronome.setNoteValue(noteValue);
            metronome.setBpm(bpm);
            metronome.setBeatSound(beatSound);
            metronome.setSound(sound);

            metronome.play();

            return null;
        }

        public void stop() {
            metronome.stop();
            metronome = null;
        }

        public void setBpm(short bpm) {
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(short beat) {
            if(metronome != null)
                metronome.setBeat(beat);
        }

    }
}
