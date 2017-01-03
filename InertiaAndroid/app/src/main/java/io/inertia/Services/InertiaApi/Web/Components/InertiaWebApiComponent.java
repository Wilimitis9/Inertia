package io.inertia.Services.InertiaApi.Web.Components;

import javax.inject.Singleton;

import dagger.Component;
import io.inertia.Activities.LoginActivity;
import io.inertia.Activities.MainActivity;
import io.inertia.Application.ApplicationConstants;
import io.inertia.Application.InertiaApplication;
import io.inertia.Authentication.AccountAuthenticator;
import io.inertia.Services.InertiaApi.Sync.Components.InertiaSyncApiSubcomponent;
import io.inertia.Services.InertiaApi.Sync.Modules.InertiaSyncApiModule;
import io.inertia.Services.InertiaApi.Web.Modules.InertiaWebApiModule;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
@Singleton
@Component(modules = InertiaWebApiModule.class)
public interface InertiaWebApiComponent {
    void inject(InertiaApplication inertiaApplication);
    void inject(MainActivity mainActivity);
    void inject(LoginActivity loginActivity);
    void inject(AccountAuthenticator accountAuthenticator);

    InertiaSyncApiSubcomponent newInertiaSyncApiSubcomponent(InertiaSyncApiModule inertiaSyncApiModule);
}
