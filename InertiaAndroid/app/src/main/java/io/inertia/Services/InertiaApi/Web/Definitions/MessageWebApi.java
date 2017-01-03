package io.inertia.Services.InertiaApi.Web.Definitions;

import java.util.List;

import io.inertia.Models.Message;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */

public interface MessageWebApi {
    @GET
    Observable<List<Message>> getMessagesObservable();
}
