package com.androidlokomedia.crudvolley;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Data> dataList = new ArrayList<Data>();
    Adapter adapter;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    EditText textId;
    EditText textEmail;
    EditText textPassword;
    ListView listView;
    String id, email, password;

    private static final String URL = "http://192.168.56.1/Tutorial_Crud/";

    private static String url_select = URL + "select.php";
    private static String url_insert = URL + "Insert.php";
    private static String url_edit = URL + "Edit.php";
    private static String url_update = URL + "Update.php";
    private static String url_delete = URL + "Delete.php";

    public static final String TAG_ID = "id";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        username = (EditText)findViewById(R.id.textUsername);
//        password = (EditText)findViewById(R.id.textPassword);
//        login = (Button)findViewById(R.id.signin);
//
//        requestQueue = Volley.newRequestQueue(this);
//
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if (jsonObject.names().get(0).equals("success"));{
//                                Toast.makeText(getApplicationContext(), "Success "+jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("VolleyError", "onErrorResponse: "+error);
//                    }
//                }) {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError{
//                        HashMap<String, String> hashMap = new HashMap<String, String>();
//                        hashMap.put("email", username.getText().toString());
//                        hashMap.put("password", password.getText().toString());
//
//                        return hashMap;
//                    }
//                };
//
//                requestQueue.add(stringRequest);
//            }
//        });

        requestQueue = Volley.newRequestQueue(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView)findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.view);

        adapter = new Adapter(MainActivity.this, dataList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                panggilVolley();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String idx = Integer.toString(dataList.get(position).getId());

                final CharSequence[] dialogItem = {"Edit", "Delete"};
                dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                edit(idx);
                                break;
                            case 1:
                                delete(idx);
                                break;
                        }
                    }
                }).show();

                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForm("","","","SIMPAN");
            }
        });
    }

    public void kosong(){
        textId.setText(null);
        textEmail.setText(null);
        textPassword.setText(null);
    }

    public void dialogForm(String idx, final String emailx, final String passwordx, String button){
        dialog = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.form_login, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Login");

        textId = (EditText)dialogView.findViewById(R.id.txt_id);
        textEmail  = (EditText)dialogView.findViewById(R.id.txt_email);
        textPassword = (EditText)dialogView.findViewById(R.id.txt_password);

        if (!idx.isEmpty()){
            textId.setText(idx);
            textEmail.setText(emailx);
            textPassword.setText(passwordx);
        } else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id = textId.getText().toString();
                email = textEmail.getText().toString();
                password = textPassword.getText().toString();

                simpanOrUpdate();
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                kosong();
            }
        }).show();
    }

    public void simpanOrUpdate(){
        String url;

        if (id.isEmpty()){
            url = url_insert;
        } else {
            url = url_update;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Responsee", "onResponse: "+response.toString());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("Sukses", "onResponse: "+jsonObject.getString("success"));
                        panggilVolley();
                        kosong();
                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "onErrorResponse: "+error.toString());
//                JSONObject jsonObject = new JSONObject()
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                if (id.isEmpty()){
                    params.put("email", email);
                    params.put("password", password);
                } else {
                    params.put("id", id);
                    params.put("email", email);
                    params.put("password", password);
                }
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void panggilVolley(){
                dataList.clear();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(true);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url_select, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                                [
//                                {
//                                    "id": "30",
//                                        "email": "esss3addss2sti@gmasil.com",
//                                        "password": "12345688s"
//                                }
//                                ]
//                                Log.d("jsonArray", "onResponse: "+response.toString());

                        for (int i = 0; i < response.length(); i++){
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);
                                Data data = new Data();
                                data.setId(jsonObject.getInt("id"));
                                data.setEmail(jsonObject.getString("email"));
                                data.setPassword(jsonObject.getString("password"));

                                dataList.add(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", "onErrorResponse: "+error.toString());
                    }
                });

//                        Controller.getController().addToRequestQueue(jsonArrayRequest);
                requestQueue.add(jsonArrayRequest);

    }

    public void edit(final String idx){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("ResponseEdit", "onResponse: "+response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    final int idxedit = jsonObject.getInt("id");
                    String email = jsonObject.getString("email");
                    String password = jsonObject.getString("password");

                    dialogForm(Integer.toString(idxedit), email, password, "Update");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void delete(final String id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("DeleteList", "onResponse: "+response.toString());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    panggilVolley();
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
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh(){
        dataList.clear();
        adapter.notifyDataSetChanged();
        panggilVolley();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
