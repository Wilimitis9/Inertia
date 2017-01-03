package io.inertia.Application;

import android.app.Application;
import android.content.Context;

import io.inertia.Services.InertiaApi.Sync.Components.InertiaSyncApiSubcomponent;
import io.inertia.Services.InertiaApi.Sync.Modules.InertiaSyncApiModule;
import io.inertia.Services.InertiaApi.Web.Components.DaggerInertiaWebApiComponent;
import io.inertia.Services.InertiaApi.Web.Components.InertiaWebApiComponent;
import io.inertia.Services.InertiaApi.Web.Modules.InertiaWebApiModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
public class InertiaApplication extends Application {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();
    
    private static InertiaWebApiComponent mInertiaWebApiComponent;
    private static InertiaSyncApiSubcomponent mInertiaSyncApiSubcomponent;
    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDependencyGraph();
        setupRealm();
    }

    private void setupDependencyGraph() {

        // Setup the app context
        mApplicationContext = getApplicationContext();

        // Build dep graph
        mInertiaWebApiComponent = DaggerInertiaWebApiComponent.builder().inertiaWebApiModule(new InertiaWebApiModule(this)).build();
        mInertiaSyncApiSubcomponent = mInertiaWebApiComponent.newInertiaSyncApiSubcomponent(new InertiaSyncApiModule());

        // Let's get weird here and inject so we can use our dependencies when
        // building out some global instances for our dependencies
        mInertiaWebApiComponent.inject(this);
    }

    private void setupRealm() {
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static Context getContext() {
        return mApplicationContext;
    }

    public static InertiaWebApiComponent getInertiaWebApiComponent() {
        return mInertiaWebApiComponent;
    }

    public static InertiaSyncApiSubcomponent getInertiaSyncApiSubcomponent() {
        return mInertiaSyncApiSubcomponent;
    }
}
