package io.inertia.Activities.Presenters;

import javax.inject.Inject;

import io.inertia.Activities.MainActivity;
import io.inertia.Application.ApplicationConstants;
import io.inertia.Application.InertiaApplication;
import io.inertia.Services.InertiaApi.Sync.Definitions.MessageSyncApi;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
public class MainActivityPresenter {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();

    @Inject
    MessageSyncApi mMessageSyncApi;

    private MainActivity mMainActivity;

    public MainActivityPresenter(MainActivity mainActivity) {
        InertiaApplication.getInertiaSyncApiSubcomponent().inject(this);
        this.mMainActivity = mainActivity;
    }

    public void sync() {
        mMessageSyncApi.download();
    }
}
