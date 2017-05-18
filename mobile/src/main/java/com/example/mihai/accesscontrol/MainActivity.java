package com.example.mihai.accesscontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class MainActivity extends AppCompatActivity {
    LoggedUser loggedUser;

    @BindView(R.id.login_button)
    Button loginButton;

    @BindView(R.id.usernameEditText)
    EditText usernameEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.loggedUser = new LoggedUser(this.getApplicationContext());
        try {
            if (this.loggedUser.getLoggedUser() != null) {
                Intent myIntent = new Intent(getApplicationContext(), LoggedActivity.class);
                startActivity(myIntent);

            }
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.login_button)
    public void login_button(View view) {
        try{
        loggedUser.setLoggedUser(new User(usernameEditText.getText().toString(),passwordEditText.getText().toString()));
        Intent myIntent = new Intent(getApplicationContext(), LoggedActivity.class);
        startActivity(myIntent);}
        catch (Exception e){
            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG);
        }
    }
}
