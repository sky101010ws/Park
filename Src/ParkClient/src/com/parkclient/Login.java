package com.parkclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class Login extends Activity {

	Toast tst;
    private String TAG = this.getClass().getSimpleName();
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private ArrayList<NewsModel> arrNews ;
    
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg){
           super.handleMessage(msg);
           pd.dismiss();
        }
     };
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

    @Override
    protected void onStart()
    {
    	startRequest();
		super.onStart();  
    }
    
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId()) 
		{
			case R.id.register:
				Intent in = new Intent();
				in.setClass(this, PersonMgr.class);
				startActivity(in);
//				tst = Toast.makeText(Login.this, "��Ҫע��", Toast.LENGTH_SHORT);
//				tst.show(); 
				break;
			default: 
				break; 
		}  
	}


	private void startRequest()
	{
       pd= ProgressDialog.show(Login.this, "��½", "objRequest post ��¼�У����Ժ�...");
       new Thread()
       {
          public void run()
          {
	          String url = "http://192.168.1.104/APP/parkpark/login.php?";
	          StringRequest sr = createPostStringRequest(url);
//	          JsonObjectRequest jr = createPostObjRequest(url);
//            String url = "http://192.168.1.104/APP/parkpark/login.php?uname=admin&upassword=admin";
//          	JsonObjectRequest jr = createGetModeRequest(Request.Method.GET,url);
	          addToRequestQueue(sr);
          }
       }.start();
	}
	
	public RequestQueue getRequestQueue() 
	{
		if (mRequestQueue == null)
		{
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}        
		return mRequestQueue;    
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) 
	{
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
	
	public <T> void addToRequestQueue(Request<T> req) 
	{
		req.setTag(TAG);
        getRequestQueue().add(req);
    }

	public JsonObjectRequest createGetModeRequest(int method, String url)
	{
		JsonObjectRequest jr = new JsonObjectRequest(
			url, null,
			new Response.Listener<JSONObject>()
			{
	            @Override
	            public void onResponse(JSONObject response) 
	            {
	                Log.i(TAG,response.toString());
	                parseJSON(response);
	                ResponseEnd("get: return success.");	             }
			},
			new Response.ErrorListener()
			{
	            @Override
	            public void onErrorResponse(VolleyError error)
	            {
	                Log.i(TAG,error.getMessage());
					ResponseEnd("get: return error.");
	            }
			});
		return jr;
	}
	
	
	public JsonObjectRequest createPostObjRequest(String url)
	{
		JSONObject params=new JSONObject(getRealParams());
		
		JsonObjectRequest jr = new JsonObjectRequest(
			Request.Method.POST, url, params,
			new Response.Listener<JSONObject>()
			{
	            @Override
	            public void onResponse(JSONObject response)
	            {
	                Log.i(TAG,response.toString());
	                parseJSON(response);
					ResponseEnd("post :return success.");
	                ;            
	             }
			},
			new Response.ErrorListener()
			{
	            @Override
	            public void onErrorResponse(VolleyError error)
	            {
	                Log.i(TAG,error.getMessage());
	                ResponseEnd("post: return error.");
	            }
			})
		{
			@Override
			public Map<String, String> getHeaders() 
			{       
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json"); 
				headers.put("Content-Type", "application/json; charset=UTF-8"); 
				return headers;  
			}
		};
		return jr;
	}
	
	public StringRequest createPostStringRequest(String url)
	{
		StringRequest SR = new StringRequest(
			Request.Method.POST, url,
			new Response.Listener<String>() 
			{			
				@Override
				public void onResponse(String response)
				{ 
					try {
						JSONObject jObj = new JSONObject(response);
						parseJSON(jObj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 
			new Response.ErrorListener() 
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{
					Log.e(TAG, error.getMessage(), error);
					ResponseEnd("post: return error.");
				}
			})
			{
				@Override
				protected Map<String, String> getParams() 
				{
					return getRealParams();
				}
			}; 
		return SR;
	}
	
	protected Map<String, String> getRealParams()
	{
		//������������Ҫpost�Ĳ���            
		Map<String, String> map = new HashMap<String, String>();
		map.put("uname", "admin");
		map.put("upassword", "admin"); 
		return map;    
	}

    private void parseJSON(JSONObject json)
    {
        try{
            String msg = json.getString("msg");
            String flag = json.getString("flag");
            String sex = json.getString("usex");
            String addr = json.getString("uaddr");
            String realName = json.getString("urealname");
            ResponseEnd("msg:"+msg+" flag:"+flag+" sex:"+sex+" addr:"+addr+" realName:"+realName);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void ResponseEnd(String showInfo) 
    {
        handler.sendEmptyMessage(0);
		tst = Toast.makeText(Login.this, showInfo, Toast.LENGTH_LONG);
		tst.show(); 
    }
    
    class NewsModel{
        private String title;
        private String link;
        private String description;
        private String pubDate;

        void setTitle(String title) {
            this.title = title;
        }

        void setLink(String link) {
            this.link = link;
        }

        void setDescription(String description) {
            this.description = description;
        }

        void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }

        String getLink() {
            return link;
        }

        String getDescription() {
            return description;
        }

        String getPubDate() {
            return pubDate;
        }

        String getTitle() {

            return title;
        }
    }

    
}
