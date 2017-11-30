package pntanasis.android.metronome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by maxime on 23/11/2017.
 */

public class MainActivity extends Activity {
    TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametrage);




    }




    public void page1(View view){
        startActivity(new Intent(this,MetronomeActivity.class));
    }
}
