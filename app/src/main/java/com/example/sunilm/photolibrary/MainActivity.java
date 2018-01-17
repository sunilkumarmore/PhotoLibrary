package com.example.sunilm.photolibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static com.example.sunilm.photolibrary.DoWork.STATUS_Progress;
import static com.example.sunilm.photolibrary.DoWork.STATUS_START;
import static com.example.sunilm.photolibrary.MainActivity.DoAPiCall.GET_Images;
import static com.example.sunilm.photolibrary.MainActivity.DoAPiCall.GET_Images_progress;

public class MainActivity extends Activity {
    TextView textView;
    AlertDialog alret;
    ImageView iv;
    ProgressDialog pD;
    int currentPic = 0;
 public   static Handler handler = null;
   // ImageView iv;
    //CharSequence cs[];
    String  cs[] = {"uncc", "winter","aurora","wonders"};
    String bs[] ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView3);
        Button goButton = (Button) findViewById(R.id.Go);
         pD = new ProgressDialog(MainActivity.this);
        pD.setTitle("Loading Photo");
        pD.setMax(100);
        pD.setCancelable(false);


        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if(msg.getData()==null)
                {
                    pD.dismiss();
                }
                else {
                    Log.d("demo", "entered main handler");
                    if (msg.getData().containsKey("image")) {
                        Log.d("demo", "entered main handler if loop ");
                        byte[] data = msg.getData().getByteArray("image");
                        Log.d("demo", "retrieved back");
                        if (data != null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            iv = (ImageView) findViewById(R.id.imageView);
                            iv.setImageBitmap(bmp);
                        }
                        pD.dismiss();

                    }

                    if (msg.what == STATUS_Progress || msg.what == GET_Images_progress) {
                        if (msg.getData().containsKey("progress")) {
                            pD.setProgress(msg.getData().getInt("progress"));

                        }
//progress
                    }
                    //GET_Images_progress
                    if (msg.what == GET_Images) {
                        if (msg.getData().containsKey("allimages")) {
                            Log.d("demo", "entered getting images");
                            String jj = msg.getData().getString("allimages");
                            bs = jj.split("\n");
                            new DoWork(handler).execute(bs[currentPic]);
                        }
                    }

                    pD.dismiss();
            }

                return false;
            }
        });


        ImageButton nxtPage =(ImageButton) findViewById(R.id.imageButton9);
        ImageButton  previousPage =(ImageButton) findViewById(R.id.imageButton10);
        nxtPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPic = currentPic+1;
                if(currentPic < bs.length) {

                }
                else
                {
                    currentPic = 0;
                }
                pD.show();
                new DoWork(handler).execute(bs[currentPic]);
            }
        });


        previousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPic = currentPic-1;
                if(currentPic<0) {
                    currentPic = bs.length - 1;
                }
                pD.show();
                    new DoWork(handler).execute(bs[currentPic]);


            }
        });


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose a keyword").
                        setItems(cs, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Log.d("demo", "item clicked" + cs[item]);
                                textView.setText(cs[item]);
                                pD.show();
                                new DoAPiCall(handler).execute(cs[item]);

                            }
                        });
                final AlertDialog singleItemAlert = builder.create();
                singleItemAlert.show();
            }
        });
    }

    public class DoAPiCall extends AsyncTask<String, Integer, String> {

        public  Handler handler;
        public  DoAPiCall(Handler handler) {
            this.handler = handler;
        }
        static final int GET_Images = 0x00;
        static final int GET_Images_progress = 0x00;
      // static final int STATUS_Progress = 0x01;

        @Override
        protected String doInBackground(String... params) {
            String y = params[0];
            Log.d("demo", y);
            StringBuilder sb = new StringBuilder();

            try {
        /*        URL url = new URL(y);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                 image = BitmapFactory.decodeStream(url.openStream());*/


                URL url = new URL("http://dev.theappsdr.com/apis/photos/index.php?keyword="+y);
                URLConnection con = url.openConnection();
                //image = BitmapFactory.decodeStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line="";
                while((line = reader.readLine()) != null){
                    sb.append(line + "\n");
                }
                reader.close();
                return sb.toString();

            } catch (Exception e) {
                //sendMsg("Failed Downloading");
                Log.d("demo", "File Failed Downloading");
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            Message mg = new Message();
            mg.what = GET_Images_progress;
            Bundle bund = new Bundle();
            bund.putInt("progress",values[0]);
            mg.setData(bund);
            //handler = new Handler();
            handler.sendMessage(mg);
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String images) {
            Message mg = new Message();
            mg.what = GET_Images;
            Bundle bund = new Bundle();
            if(images!=null) {
                //imageView.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();
                //Log.d("demo", "completed post exeute" + byteArray);
                bund.putString("allimages", images);
                //bund.putInt("next one",);
                mg.setData(bund);
                //handler = new Handler();
                handler.sendMessage(mg);

                Log.d("demo", "completed post exeute");

            }

            super.onPostExecute(images);
        }

    }
}



