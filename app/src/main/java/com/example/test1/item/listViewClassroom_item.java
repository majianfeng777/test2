package com.example.test1.item;

public class listViewClassroom_item {
    private int classNum;
    private int studentNum;
    private int arrivedNum;
    private String ipCamera;
    public listViewClassroom_item(int classNum, int studentNum, int arrivedNum,String ipCamera){
        this.classNum=classNum;
        this.studentNum=studentNum;
        this.arrivedNum=arrivedNum;
        this.ipCamera=ipCamera;
    }
    public int getClassNum() {
        return classNum;
    }

    public int getArrivedNum() {
        return arrivedNum;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public String getIpCamera() {
        return ipCamera;
    }
}
