package co.go.simpleplayer.service;

/**
 * Created by roma on 22.09.2014.
 */

import android.content.Context;
import android.content.Intent;


public class IntentReceiver extends android.content.BroadcastReceiver {

    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Intent myIntent = new Intent(ctx, MediaPlayerService.class);
            ctx.stopService(myIntent);
        }
    }
}
