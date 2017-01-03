package io.inertia.Services.InertiaApi.Web;

import android.util.Log;

import java.io.IOException;

import io.inertia.Application.ApplicationConstants;
import io.inertia.Authentication.AuthenticationHelper;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Luke Wilimitis on 1/2/17.
 */
public class InertiaWebApiAuthenticator implements Authenticator {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();

    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        // First, let's invalidate the token
        try {
            AuthenticationHelper.invalidateCurrentToken();
        } catch (Exception e) {
            Log.d(TAG, "failed to invalidate old auth token: " + e.getMessage());
        }

        if (responseCount(response) >= InertiaWebApiConstants.INERTIA_API_DEFAULT_ATTEMPTS) {
            return null;
        }

        // Get a new auth token
        String newAccessToken = AuthenticationHelper.getAccessToken();

        // Add new header to rejected request and retry it
        return response.request().newBuilder()
                .addHeader(InertiaWebApiConstants.AUTH_HEADER, InertiaWebApiConstants.AUTH_HEADER_BEARER + " " + newAccessToken)
                .build();
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
