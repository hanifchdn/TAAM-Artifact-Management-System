package com.example.b07demosummer2024;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    public LoginPresenter(LoginContract.View view){
        this.view = view;
    }
    @Override
    public void login(String email, String password){

    }
    @Override
    public void onDestroy(){
        view = null;
    }
}