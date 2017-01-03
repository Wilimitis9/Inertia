package io.inertia.Services.InertiaApi.Sync.Components;

import javax.inject.Singleton;

import dagger.Subcomponent;
import io.inertia.Activities.Presenters.MainActivityPresenter;
import io.inertia.Services.InertiaApi.Sync.Modules.InertiaSyncApiModule;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
@Singleton
@Subcomponent(modules = {InertiaSyncApiModule.class})
public interface InertiaSyncApiSubcomponent {
    void inject(MainActivityPresenter mainActivityPresenter);
}
