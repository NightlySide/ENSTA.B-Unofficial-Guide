package io.github.nightlyside.enstaunofficialguide.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.nightlyside.enstaunofficialguide.R;
import io.github.nightlyside.enstaunofficialguide.data_structure.User;
import io.github.nightlyside.enstaunofficialguide.network.NetworkManager;
import io.github.nightlyside.enstaunofficialguide.network.NetworkResponseListener;

public class LoginActivity extends AppCompatActivity {

    private EditText usernametext;
    private EditText passwordtext;
    private TextView errormsgtext;
    private ProgressBar spinner;
    private Button loginBtn;
    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    public String jwt_token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernametext = (EditText) findViewById(R.id.username);
        passwordtext = (EditText) findViewById(R.id.password);
        errormsgtext = (TextView) findViewById(R.id.error_msg);
        loginBtn = (Button) findViewById(R.id.login);
        spinner = (ProgressBar) findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.GONE);

        // On récupère la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LoginDebug",
                        "Trying to login with : "+
                                usernametext.getText().toString()+"/"+
                                passwordtext.getText().toString());
                try {
                    sendLoginData(usernametext.getText().toString(), passwordtext.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        usernametext.setDefaultFocusHighlightEnabled(true);
        usernametext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { enableLoginButton(); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String username = usernametext.getText().toString();
                isUsernameValid = checkValidUsername(username);
                if (!isUsernameValid) {
                    usernametext.setError("Username invalide.");
                }
            }
        });

        passwordtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { enableLoginButton(); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String pass = passwordtext.getText().toString();
                isPasswordValid = checkValidPassword(pass);
                if (!isPasswordValid) {
                    passwordtext.setError("Mot de passe trop court.");
                }
            }
        });
    }

    // validating username
    private boolean checkValidUsername(String username) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean checkValidPassword(String pass) {
        if (pass != null && pass.length() > 5) {
            return true;
        }
        return false;
    }

    private void enableLoginButton() {
        if (isPasswordValid && isUsernameValid) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }

    private void sendLoginData(final String username, String password) throws UnsupportedEncodingException {
        String enc_username = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
        String enc_pass = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
        String query_string = "login.php?username="+enc_username+"&password="+enc_pass;
        errormsgtext.setText("");

        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                if (!result.isEmpty()) {
                    Log.d("LoginDebug", "Response: " + result.toString());
                    spinner.setVisibility(View.GONE);
                    try {
                        if (!response.getString("message").equals("Successful login.")){
                            errormsgtext.setText(response.getString("message"));
                        }
                        else {
                            // We have the correct credentials
                            jwt_token = response.getString("jwt");
                            spinner.setVisibility(View.VISIBLE);

                            User.saveUserFromJWTToken(getApplicationContext(), jwt_token, new NetworkResponseListener<Boolean>() {
                                @Override
                                public void getResult(Boolean object) {
                                    MainActivity.loggedUser = User.getUserFromSharedPreferences(getApplicationContext());
                                    spinner.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Heureux de te revoir " + MainActivity.loggedUser.display_name+ " !", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    spinner.setVisibility(View.GONE);
                    errormsgtext.setText("Une erreur est survenue, veuillez réessayer.");
                }
            }
        });
        spinner.setVisibility(View.VISIBLE);

    }
}

