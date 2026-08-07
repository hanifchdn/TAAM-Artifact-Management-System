package com.example.b07demosummer2024;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SessionModel sessionModel = new SessionModel();
            String uid = sessionModel.currentUserUid();
            if (uid == null) {
                loadFragment(new LoginFragment());
            } else {
                sessionModel.fetchUserProfile(uid, new SessionContract.Model.ProfileCallback() {
                    public void onProfileLoaded(User user) {
                        SessionManager.getInstance().setCurrentUser(user);
                        loadFragment(new HomeFragment());
                    }
                    public void onProfileError(String message) {
                        sessionModel.logOut();
                        loadFragment(new LoginFragment());
                    }
                });
            }
        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}