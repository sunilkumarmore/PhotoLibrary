package com.example.sunilm.photolibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sunilm on 1/16/2018.
 */

    public class DoWork extends AsyncTask<CharSequence, Integer, Bitmap> {

    public  Handler handler;
    public  DoWork(Handler handler) {
        this.handler = handler;
    }
        static final int STATUS_START = 0x00;
    static final int STATUS_Progress = 0x01;

        @Override
        protected Bitmap doInBackground(CharSequence... params) {
            String y = params[0].toString();
            Log.d("demo", y);
            Bitmap image = null;

            try {
        /*        URL url = new URL(y);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                 image = BitmapFactory.decodeStream(url.openStream());*/


                URL url = new URL(y);
                URLConnection con = url.openConnection();
                if(con.getInputStream()!=null) {

                    image = BitmapFactory.decodeStream(con.getInputStream());

                    if (image != null) {
                        // sendMsg("File Retrieved");
                        Log.d("demo", "File Retrieved");
                    } else {
                        // sendMsg("File Error");
                        Log.d("demo", "File error");
                    }
                }


            } catch (Exception e) {
                //sendMsg("Failed Downloading");
                Log.d("demo", "File Failed Downloading");
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            Message mg = new Message();
            mg.what = STATUS_Progress;
            Bundle bund = new Bundle();
            bund.putInt("progress",values[0]);
            mg.setData(bund);
            //handler = new Handler();
            handler.sendMessage(mg);
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(Bitmap imageView) {
            Message mg = new Message();
            mg.what = STATUS_START;
            Bundle bund = new Bundle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(imageView!=null) {
                imageView.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.d("demo", "completed post exeute" + byteArray);
                bund.putByteArray("image", byteArray);
                // bund.putInt("next one",);
                mg.setData(bund);
                //handler = new Handler();
                handler.sendMessage(mg);

                Log.d("demo", "completed post exeute");

            }
            else
            {
                handler.sendMessage(mg);

            }

            super.onPostExecute(imageView);
        }

    }


