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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText usernametext;
    private EditText passwordtext;
    private EditText display_nametext;
    private TextView errormsgtext;
    private ProgressBar spinner;
    private Button loginBtn;
    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    private boolean isFullnameValid = false;
    public String jwt_token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernametext = (EditText) findViewById(R.id.username);
        passwordtext = (EditText) findViewById(R.id.password);
        display_nametext = (EditText) findViewById(R.id.display_name);
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
                        "Trying to register with : "+
                                usernametext.getText().toString()+"/"+display_nametext.getText().toString()+"/"+
                                passwordtext.getText().toString());
                try {
                    sendRegisterData(usernametext.getText().toString(),
                                    display_nametext.getText().toString(),
                                    passwordtext.getText().toString());
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
                    usernametext.setError("Veuillez entrer votre adresse email ENSTA B.");
                }
            }
        });

        display_nametext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { enableLoginButton(); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String display_name = display_nametext.getText().toString();
                isFullnameValid = checkValidDisplayName(display_name);
                if (!isFullnameValid) {
                    display_nametext.setError("Veuillez indiquer un vrai nom.\nex: Pierre Dupont");
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
        String EMAIL_PATTERN = "[a-zA-Z0-9.\\-_]*@ensta-bretagne.(fr|org)";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    // validating displayname
    private boolean checkValidDisplayName(String display_name) {
        String EMAIL_PATTERN = "^[a-zàâçéèêëîïôûùüÿñæœA-ZÀÂÇÉÈÊËÎÏÔÛÙÜŸÑÆŒ.-]* [a-zàâçéèêëîïôûùüÿñæœA-ZÀÂÇÉÈÊËÎÏÔÛÙÜŸÑÆŒ. -]*$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(display_name);
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
        if (isPasswordValid && isUsernameValid && isFullnameValid) {
            loginBtn.setEnabled(true);
        } else {
            loginBtn.setEnabled(false);
        }
    }

    private void sendRegisterData(final String username, String fullname, String password) throws UnsupportedEncodingException {
        String enc_username = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
        String enc_fullname = URLEncoder.encode(fullname, StandardCharsets.UTF_8.toString());
        String enc_pass = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
        String query_string = "register-user.php?username="+enc_username+"&password="+enc_pass+"&display_name="+enc_fullname;
        errormsgtext.setText("");

        NetworkManager.getInstance().makeJSONRequest(query_string, new NetworkResponseListener<String>() {
            @Override
            public void getResult(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                if (!result.isEmpty()) {
                    Log.d("LoginDebug", "Response: " + result.toString());
                    spinner.setVisibility(View.GONE);
                    try {
                        if (!response.getString("message").equals("Successful register.")){
                            errormsgtext.setText(response.getString("message"));
                        }
                        else {
                            // Registration ok
                            Toast.makeText(getApplicationContext(), "Inscription réussie ! Veuillez vous connecter.", Toast.LENGTH_LONG).show();
                            finish();
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

