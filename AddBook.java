package com.cse.cou.alamgir.mybookshope;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    EditText book_name,writer,edition,department,phone_number,price;
    ImageView imageView;
    Button add_book_btn;
    Bitmap bitmap;

    SharedPreferences sharedPreferences;

    int user_id,department_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        sharedPreferences=getSharedPreferences(Constant.sharedPref_name,MODE_PRIVATE);
        department_id=sharedPreferences.getInt("dept_id",-1);
        user_id=sharedPreferences.getInt("user_id",-1);

        book_name= (EditText) findViewById(R.id.add_bookname);
        writer= (EditText) findViewById(R.id.add_writer);
        edition= (EditText) findViewById(R.id.add_book_edition);
        department= (EditText) findViewById(R.id.add_dept);
        department.setText(sharedPreferences.getString("dept_name",null));

        phone_number= (EditText) findViewById(R.id.add_phone_number);
        price= (EditText) findViewById(R.id.add_book_price);
        imageView= (ImageView) findViewById(R.id.add_book_img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });
        add_book_btn= (Button) findViewById(R.id.add_book_btn);
        add_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });
    }
    private void addBook(){


        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constant.addBook, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("AddBook",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(!jsonObject.getBoolean("error")){

                        Toast.makeText(AddBook.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                        startActivity(new Intent(AddBook.this,Profile.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> param=new HashMap<>();
                param.put("book_name",book_name.getText().toString());
                param.put("writer",writer.getText().toString());
                param.put("edition",edition.getText().toString());
                param.put("department_id",department_id+"");
                param.put("user_id",user_id+"");
                param.put("price",price.getText().toString());
                param.put("phone_number",phone_number.getText().toString());
                param.put("image",imageToString(bitmap));
                return param;
            }
        };
        RequestHandler.getInstance(AddBook.this).addToRequestQueue(stringRequest);
        
    }



    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imgbyte=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgbyte,Base64.DEFAULT);
    }
    private void selectImage(){

     Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK&& data!=null){
            Uri path=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),path);

                Bitmap bitmapImage = BitmapFactory.decodeFile(path.toString());
                int nh = (int) ( bitmapImage.getHeight() * (360.0 / bitmapImage.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 360, nh, true);


                imageView.setImageBitmap(scaled);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
