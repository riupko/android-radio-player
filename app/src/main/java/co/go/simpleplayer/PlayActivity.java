package co.go.simpleplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.go.simpleplayer.app.AppController;
import co.go.simpleplayer.service.MediaPlayerService;


public class PlayActivity extends Activity {

    private static final String TAG = PlayActivity.class.getSimpleName();

    //StationId
    public final static String STATION_ID = "co.go.simpleplayer.STATION_ID";

    private ProgressDialog pDialog;

    private ImageButton stopButton = null;
    private ImageButton playButton = null;

    //Your activity will respond to this action String
    public static final String RECEIVED_AUDIO_STREAM = "co.go.simpleplayer.RECEIVE_AUDIO_STREAM";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RECEIVED_AUDIO_STREAM)) {
                hidePDialog();
                Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), StationListActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVED_AUDIO_STREAM);
        bManager.registerReceiver(bReceiver, intentFilter);

        TextView tv = (TextView) findViewById(R.id.textView);
        final int id = getIntent().getIntExtra(STATION_ID, 0);
        Log.d("StationID", id + "");
        tv.setText(id + "");

        // changing action bar color
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));

        playButton = (ImageButton) findViewById(R.id.btnPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Play", "Play");
                Intent intent = new Intent(getApplicationContext(),
                        MediaPlayerService.class);
                intent.putExtra(MediaPlayerService.START_PLAY_STATION_ID, id);
                startService(intent);
                pDialog = new ProgressDialog(PlayActivity.this);

                // Showing progress dialog before making http request
                pDialog.setMessage("Loading...");
                pDialog.show();
            }
        });

        stopButton = (ImageButton) findViewById(R.id.btnStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        MediaPlayerService.class);
                stopService(intent);
            }
        });
    }

    private void hidePDialog() {
        Log.d(TAG, "hideDialog");
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
