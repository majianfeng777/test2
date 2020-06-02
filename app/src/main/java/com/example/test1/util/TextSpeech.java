package com.example.test1.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;


import java.util.Locale;

public class TextSpeech {
    public TextToSpeech textToSpeech;
    public TextSpeech(Context context, final String text){
        textToSpeech=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS){
                    // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setPitch(1.0f);
                    // 设置语速
                    textToSpeech.setSpeechRate(0.8f);
                    textToSpeech.setLanguage(Locale.CHINESE);
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
    public void stop(){
        if (textToSpeech!=null){
            textToSpeech.stop();
        }
    }
    public void release(){
        if (textToSpeech!=null){
            textToSpeech.shutdown();
        }
    }

}
