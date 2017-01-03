package io.inertia.Services.InertiaApi.Sync.Definitions;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.inertia.Application.ApplicationConstants;
import io.inertia.Models.Message;
import io.inertia.Services.InertiaApi.Web.Definitions.MessageWebApi;
import io.realm.Realm;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
public class MessageSyncApi {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();

    private MessageWebApi mMessageWebApi;

    @Inject
    public MessageSyncApi(MessageWebApi messageWebApi) {
        mMessageWebApi = messageWebApi;
    }

    public void download() {

        // Download and update messages from the api
        mMessageWebApi.getMessagesObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<List<Message>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "download completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "download failed: " + e.getMessage());
                    }

                    @Override
                    public void onNext(final List<Message> message) {
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Realm.getDefaultInstance().insertOrUpdate(message);
                            }
                        });
                    }
                });
    }
}
