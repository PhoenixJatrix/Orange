package Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.media.session.MediaButtonReceiver;

import com.nullinnix.orange.ShowNotifications;

public class AudioDeviceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaButtonReceiver.handleIntent(ShowNotifications.mediaSessionCompat, intent);
        context.sendBroadcast(new Intent("AUDIO_KEY_PRESS")
                .putExtra("keyPress", intent.getAction()));
    }
}
