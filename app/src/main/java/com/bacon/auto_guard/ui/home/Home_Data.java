package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

class Home_Data {

    ArrayList<Son_Data_format> son_data = new ArrayList<>();

    public Home_Data(){

        setData();
        sort();

    }

    private void sort() {



    }

    public void setData() {

        //用getSharePreference來決定要給甚麼資料

        Son_Data_format temp1;
        Son_Data_format temp2;

        for(int i=0;i<4;i++){



            temp1 = new Son_Data_format("default", "房間 1", "電燈"+i, "ON");
//            temp2 = new Son_Data_format("default", "房間 2", "插座"+i, "ON");

            son_data.add(temp1);
//            son_data.add(temp2);

        }
//        Log.d(TAG, Objects.requireNonNull(son_data.get(0).get("name")));

    }

    private void from_internet_to_here(String parent_name){
        //TODO 從網路抓取資料，非同步

    }

    public ArrayList<Son_Data_format> getSon_data(){
        //從getSharePreference來決定要抓取甚麼資料
//
//        ArrayList<Son_Data_format> temp = (ArrayList<Son_Data_format>) son_data.clone();
////        Log.d(TAG,son_data.size()+" = first");
//        Son_Data_format temp_data;
//        for(int i=0;i<temp.size();i++){
//            temp_data = son_data.get(i);
//            if (!temp_data.getParent().equals(parent)){
//                temp.remove(temp_data);
//            }
//        }
////        Log.d(TAG,son_data.size()+" = second");
//

        return son_data;
    }

    public ArrayList<HashMap<String,String>> getDefault_data(){
        HashMap<String, String> temp_map = new HashMap<>();
        ArrayList<HashMap<String, String>> temp_array = new ArrayList<>();

        temp_map.put("name","電燈 1");
        temp_map.put("status","ON");
        temp_map.put("parent","房間 1");
        temp_map.put("type","default");
        temp_array.add(temp_map);

        return temp_array;
    }


}
