package io.inertia.Authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Luke Wilimitis on 10/16/16.
 */
public class InertiaAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator accountAuthenticator = new AccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }
}
