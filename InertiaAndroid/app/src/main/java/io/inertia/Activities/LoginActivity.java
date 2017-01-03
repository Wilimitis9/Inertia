package io.inertia.Activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import io.inertia.Application.ApplicationConstants;
import io.inertia.Application.InertiaApplication;
import io.inertia.Authentication.AccessToken;
import io.inertia.Authentication.AuthenticationConstants;
import io.inertia.Authentication.AuthenticationHelper;
import io.inertia.Models.OAuthToken;
import io.inertia.R;
import io.inertia.Services.InertiaApi.Web.Definitions.OAuthTokenWebApi;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiConstants;

public class LoginActivity extends AccountAuthenticatorActivity {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();
    public final static String ARG_NO_ACCOUNTS = "NO_ACCOUNTS";
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TOKEN_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_REFRESH_TOKEN = "REFRESH_TOKEN";
    public final static String PARAM_ACCESS_TOKEN_EXPIRATION_DATE = "EXPIRATION_DATE";

    private boolean mFromHomeActivityNoAccounts = false;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;

    @Inject
    OAuthTokenWebApi mOAuthTokenWebApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inject the dependencies
        InertiaApplication.getInertiaWebApiComponent().inject(this);

        // Setup the activity references and events
        setupView();

        // Get the auth service
        //mTokenAuthenticationService = OAuthTokenService.getInstance();

        // Get the account manager
        mAccountManager = AccountManager.get(getBaseContext());

        // Get the account name and auth token type from the intent or the view
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        if (accountName != null) {
            mUsernameField.setText(accountName);
        }

        // Get the auth token type from the intent or the view
        mAuthTokenType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS;
        }

        // Determine if we came from the home activity with no accounts yet, default to false
        mFromHomeActivityNoAccounts = getIntent().getBooleanExtra(ARG_NO_ACCOUNTS, false);
    }

    // Setup the activity references and events
    private void setupView() {
        mUsernameField = ((EditText)findViewById(R.id.login_activity_username_field));
        mPasswordField = ((EditText)findViewById(R.id.login_activity_password_field));
        mLoginButton = ((Button)findViewById(R.id.login_activity_button));

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    // Attempt to login the user based on the username and password field
    private void login() {
        final String username = mUsernameField.getText().toString();
        final String password = mPasswordField.getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE) != null ?
                getIntent().getStringExtra(ARG_ACCOUNT_TYPE) : AuthenticationConstants.AUTH_ACCOUNT_TYPE;

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                Log.d(TAG, "login started");

                Bundle bundle = new Bundle();
                try {
                    OAuthToken oAuthToken = mOAuthTokenWebApi.getOAuthToken(username, password, InertiaWebApiConstants.GRANT_TYPE_PASSWORD).execute().body();

                    if (oAuthToken != null && oAuthToken.getAccessToken() != null) {
                        AccessToken accessToken = AuthenticationHelper.convertToken(oAuthToken);
                        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        bundle.putString(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                        bundle.putString(PARAM_REFRESH_TOKEN, accessToken.getRefreshToken());
                        bundle.putString(PARAM_ACCESS_TOKEN_EXPIRATION_DATE, String.valueOf(accessToken.getExpirationDate().getTime()));
                    } else {
                        bundle.putString(KEY_ERROR_MESSAGE, "login failed");
                        Log.d(TAG, "Error retrieving auth token, null token returned");
                    }
                }
                catch (Exception e) {
                    // TODO don't show exception details in prod
                    bundle.putString(KEY_ERROR_MESSAGE, e.getMessage());

                    Log.d(TAG, "Error retrieving auth token: " + e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(bundle);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Add the account to the device and set the auth token
    private void finishLogin(Intent intent) {
        Log.d(TAG, "finishLogin started");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String refreshToken = intent.getStringExtra(PARAM_REFRESH_TOKEN);

        final Account[] accounts = mAccountManager.getAccountsByType(intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        final Account account = accounts.length != 0 ? accounts[0] : new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        // If we don't have any accounts yet, let's add a new one
        if (accounts.length == 0) {
            mAccountManager.addAccountExplicitly(account, refreshToken, null);
        } else {
            mAccountManager.setPassword(accounts[0], refreshToken);
        }

        // Now let's set the access token for the account
        mAccountManager.setAuthToken(account, mAuthTokenType, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
        mAccountManager.setUserData(account, AuthenticationConstants.AUTH_TOKEN_EXPIRATION_DATE, intent.getStringExtra(PARAM_ACCESS_TOKEN_EXPIRATION_DATE));

        // Return to the caller or restart the home activity
        if (mFromHomeActivityNoAccounts) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        } else {
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
        }

        // Remove this activity from the stack
        finish();
    }
}
