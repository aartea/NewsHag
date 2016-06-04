package badassapps.aaron.newshag;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by austin on 6/3/16.
 */


public class MySyncServicesMovies extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapterMovies sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new SyncAdapterMovies(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
