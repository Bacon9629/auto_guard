package com.bacon.auto_guard.ui.home;

import java.util.ArrayList;
import java.util.HashMap;

class Home_Data {

    ArrayList<HashMap<String,String>> son_data = new ArrayList<>();

    public Home_Data(){


        setData();

    }

    public void setData() {

        //用getSharePreference來決定要給甚麼資料

        HashMap<String, String> temp_map = new HashMap<>();

        for(int i=0;i<6;i++){

            temp_map.put("name","電燈 "+i);
            temp_map.put("status","ON");
            temp_map.put("parent","房間 1");
            temp_map.put("type","default");

            son_data.add(temp_map);

        }

    }

    private void from_internet_to_here(String parent_name){
        //TODO 從網路抓取資料，非同步

    }

    public ArrayList<HashMap<String,String>> getSon_data(){
        //從getSharePreference來決定要抓取甚麼資料
//        from_internet_to_here(parent_name);
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
