package io.inertia.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.inertia.Activities.Presenters.MainActivityPresenter;
import io.inertia.Application.ApplicationConstants;
import io.inertia.Authentication.AuthenticationConstants;
import io.inertia.Authentication.AuthenticationHelper;
import io.inertia.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private Account mAccount;
    private Unbinder mUnbinder;
    private MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(getBaseContext());
        Account[] accounts = mAccountManager.getAccountsByType(AuthenticationConstants.AUTH_ACCOUNT_TYPE);

        // If we don't have any accounts, make the user login/sign up and clear the activity stack, otherwise get the account
        if (accounts.length == 0) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.ARG_NO_ACCOUNTS, true);
            startActivity(loginIntent);
            finish();
        } else if (accounts.length == 1) {
            // Access the first account
            mAccount = accounts[0];

            // Setup the auth helper
            AuthenticationHelper.setup(mAccount, mAccountManager, this);

            // Setup the presenter
            mPresenter = new MainActivityPresenter(this);

            // Setup the view
            setupView(this);

            // Begin syncing messages
            mPresenter.sync();
        } else {
            Toast.makeText(getBaseContext(), "More than 1 account found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupView(final Activity activity) {
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        //mMessageRecyclerViewAdapter = new MessageRecyclerViewAdapter(getBaseContext(), mPresenter.get(), true, false);
        //mMessageGridView.setAdapter(mMessageRecyclerViewAdapter);
    }
}
