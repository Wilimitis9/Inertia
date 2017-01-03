package io.inertia.Services.InertiaApi.Sync.Modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.inertia.Services.InertiaApi.Sync.Definitions.MessageSyncApi;
import io.inertia.Services.InertiaApi.Web.Definitions.MessageWebApi;

/**
 * Created by Luke Wilimitis on 11/27/16.
 */

@Module
public class InertiaSyncApiModule {

    @Provides
    @Singleton
    MessageSyncApi provideUserSyncApi(MessageWebApi messageWebApi) {
        return new MessageSyncApi(messageWebApi);
    }
}
