package co.go.simpleplayer.service;

/**
 * Created by roma on 22.09.2014.
 */
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.go.simpleplayer.PlayActivity;
import co.go.simpleplayer.R;
import co.go.simpleplayer.StationListActivity;
import co.go.simpleplayer.app.AppController;

public class MediaPlayerService extends Service {

    private static final String TAG = MediaPlayerService.class.getSimpleName();

    private MediaPlayer mediaPlayer = null;
    private int stationId = 0;

    private static int classID = 354; // just a number
    private static final String url = "http://101.ru/api/getstationstream.php?station_id=%d&quality=1&type=2";

    public static String START_PLAY_STATION_ID = "START_PLAY_STATION_ID";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int sid = intent.getIntExtra(START_PLAY_STATION_ID, 0);
        if (sid > 0) {
            if (sid != stationId) {
                stop();
            }
            stationId = sid;
            play();
        }
        return Service.START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void play() {
        Log.d(TAG, url);

        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(PlayActivity.STATION_ID, stationId);
        //TODO:investifate flags and activity stack
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("My Music Player")
                .setContentText("Now Playing: \"Rain\"")
                .setSmallIcon(R.drawable.ic_action_play)
                .setContentIntent(pi)
                .build();


        // Creating volley request obj
        JsonObjectRequest stationReq = new JsonObjectRequest(String.format(url, stationId), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //hidePDialog();

                        // Parsing json
                        JSONObject jsonStationResult = null;
                        JSONArray jsonStationPlaylist = null;
                        try {
                            jsonStationResult = response.getJSONObject("result");
                            jsonStationPlaylist = jsonStationResult.getJSONArray("playlist");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < jsonStationPlaylist.length(); i++) {
                            try {
                                JSONObject obj = jsonStationPlaylist.getJSONObject(i);
                                final String playUrl = obj.getString("url");

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                try {
                                    mediaPlayer.setDataSource(playUrl);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        Intent playIntent = new Intent(PlayActivity.RECEIVED_AUDIO_STREAM);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(playIntent);
                                        mediaPlayer.start();
                                    }
                                });
                                mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)

                               // hidePDialog();
                                break;//use first link
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
               // hidePDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stationReq);

        startForeground(classID, notification);
    }

    @Override
    public void onDestroy() {
        stop();
    }

    private void stop() {
        if (stationId > 0) {
            stationId = 0;
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            stopForeground(true);
        }
    }

}

