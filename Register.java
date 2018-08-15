package com.cse.cou.alamgir.mybookshope;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText user_name,email,password;
    Button register;
    String str_name,str_email,str_password;
    ProgressDialog progressDialog;
    ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user_name= (EditText) findViewById(R.id.reg_name);
        email= (EditText) findViewById(R.id.reg_email);
        password= (EditText) findViewById(R.id.reg_pass);
        register= (Button) findViewById(R.id.register_btn);
        progressbar= (ProgressBar) findViewById(R.id.register_progress);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registration();
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    public void registration() {
        str_name = user_name.getText().toString();
        str_email = email.getText().toString();
        str_password = password.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(str_password) && !isPasswordValid(str_password)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(str_email)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(str_email)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {


            progressbar.setVisibility(View.VISIBLE);

            StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.registerUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressbar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        if(!jsonObject.getBoolean("error")){
                            startActivity(new Intent(Register.this,LoginActivity.class));
                        }
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressbar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param=new HashMap<>();
                    param.put("name",str_name);
                    param.put("email",str_email);
                    param.put("password",str_password);
                    return param;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        }
    }
}

