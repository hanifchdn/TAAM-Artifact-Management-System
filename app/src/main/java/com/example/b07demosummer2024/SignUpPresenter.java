package com.example.b07demosummer2024;

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View view;
    public SignUpPresenter(SignUpContract.View view){

        this.view = view;
    }
    @Override
    public void signUp(String email, String password, String confirmPassword){

    }
    @Override
    public void onDestroy(){
        view = null;
    }
}