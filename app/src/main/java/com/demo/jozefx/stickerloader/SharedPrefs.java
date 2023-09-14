package com.demo.jozefx.stickerloader;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.demo.jozefx.StickerPack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public  class SharedPrefs {

    private static String prefName="StickerJsonData";
    private static String jsonArrayName="JsonData";

    public  static void deletePack(Context c,String identifier){
        SharedPreferences sharedpreferences=c.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String existingPack= getData(c);
        List<StickerPack> existingPacks=new ArrayList<>();
        int i=0;

        try {
            JSONArray jAray=new JSONArray(existingPack);
            for (;i<jAray.length();i++ ){
                JSONObject currentOject=jAray.getJSONObject(i);
                Gson gson = new Gson();
                String currentStrObject=currentOject.toString();
                StickerPack s=gson.fromJson(currentStrObject,StickerPack.class);
                if( !s.identifier.equals(identifier) ){
                    existingPacks.add(s);
                }
            }
            Gson gson = new Gson();
            String jsonPack = gson.toJson(existingPacks);
            editor.putString(jsonArrayName,  jsonPack);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void putData(StickerPack pack, Context c){
        SharedPreferences sharedpreferences=c.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String existingPack= getData(c);
        List<StickerPack> existingPacks=new ArrayList<>();
        int i=0;
        boolean alredyAdded=false;
        try {
            JSONArray jAray=new JSONArray(existingPack);
            for (;i<jAray.length();i++ ){
                JSONObject currentOject=jAray.getJSONObject(i);
                Gson gson = new Gson();
                String currentStrObject=currentOject.toString();
                StickerPack s=gson.fromJson(currentStrObject,StickerPack.class);
                if( s.identifier.equals(pack.identifier) )
                    alredyAdded=true;

                existingPacks.add(s);
            }
            if(!alredyAdded)
            {
                existingPacks.add(pack);
            }
            Gson gson = new Gson();
            String jsonPack = gson.toJson(existingPacks);
            editor.putString(jsonArrayName,  jsonPack);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static String getData(Context c){
        SharedPreferences sp = c.getSharedPreferences(prefName , Context.MODE_PRIVATE);
        return (sp.getString(jsonArrayName,"[]"));
    }

}
