package com.example.guessthecelebrity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class MainActivity extends AppCompatActivity{
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    public void celebChosen(View view){
       if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswers))){
           Toast.makeText(this, "CORRECT!!", Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(this, "WRONG!! It was  "+celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
       }
       newQuestion();
    }
    int chosenCeleb=0;
    String[] answers=new String[4];
    int locationOfCorrectAnswers=0;
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return  myBitmap;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
    public  class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result= new StringBuilder();
            URL url;
            HttpURLConnection urlConnection=null;
            try{
             url=new URL(urls[0]);
             urlConnection=(HttpURLConnection)url.openConnection();
             InputStream in=urlConnection.getInputStream();
             InputStreamReader reader=new InputStreamReader(in);
             int data=reader.read();
             while (data!=-1){
                 char current=(char) data;
                 result.append(current);
                 data=reader.read();
             }
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
    public void newQuestion(){
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celebURLs.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswers = rand.nextInt(4);
            int incorrectAnswersLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswers) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorrectAnswersLocation = rand.nextInt(celebURLs.size());
                    while (incorrectAnswersLocation == chosenCeleb) {
                        incorrectAnswersLocation = rand.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswersLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        button0=findViewById(R.id.button0);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);

        DownloadTask task=new DownloadTask();
        String result=null;
        try{
          result= task.execute("https://www.imdb.com/list/ls050322961/").get();
          String[] splitResult=result.split("<a href=\"/name/nm0000138/?ref_=nmls_pst\"");
            Pattern P=Pattern.compile("height=\"209\"\nsrc=\"(.*?)\"");
            Matcher m=P.matcher(splitResult[0]);
            while(m.find()){
                celebURLs.add(m.group(1));
                //System.out.println(m.group(1));
            }
            P=Pattern.compile("<img alt=\"(.*?)\"");
            m=P.matcher(splitResult[0]);
            while(m.find()){
                celebNames.add(m.group(1));
                //System.out.println(m.group(1));
            }
            newQuestion();

        }catch(Exception e){
          e.printStackTrace();
        }
    }
}