package io.inertia.Services.InertiaApi.Web;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 11/17/16.
 */

public class OAuthTokenApiInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request authorisedRequest = originalRequest.newBuilder()
                .header(InertiaWebApiConstants.AUTH_HEADER, InertiaWebApiConstants.ENCODED_AUTH_HEADER)
                .build();

        return chain.proceed(authorisedRequest);
    }
}
