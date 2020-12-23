package com.bacon.auto_guard.ui.home;

class Son_Data_format {

    private String name, status, parent, type;

    public Son_Data_format(){
        this("", "", "", "");
    }
    public Son_Data_format(String type, String parent, String name, String status){
        this.name = name;
        this.parent = parent;
        this.status = status;
        this.type = type;
    }

    public void setData(String type, String parent, String name, String status){
        this.name = name;
        this.parent = parent;
        this.status = status;
        this.type = type;
    }


    public String getName() {
        return name;
    }
    public String getParent() {
        return parent;
    }
    public String getStatus() {
        return status;
    }
    public String getType() {
        return type;
    }
}
