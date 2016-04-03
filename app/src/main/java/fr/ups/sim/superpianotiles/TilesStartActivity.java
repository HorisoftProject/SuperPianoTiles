package fr.ups.sim.superpianotiles;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import fr.ups.sim.superpianotiles.events.TileAdapter;
import fr.ups.sim.superpianotiles.events.TileCounter;
import fr.ups.sim.superpianotiles.events.TileEvent;

public class TilesStartActivity extends Activity {







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        createMenu();


    }




    public void createMenu() {

        setContentView(R.layout.menu_start);


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TilesStartActivity.this, Mode1Activity.class);
                startActivity(intent);
            }
        });
    }








}
