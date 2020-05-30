package com.example.test1.item;

public class listViewClassroom_item {
    private int classNum;
    private int studentNum;
    private int arrivedNum;
    public listViewClassroom_item(int classNum, int studentNum, int arrivedNum){
        this.classNum=classNum;
        this.studentNum=studentNum;
        this.arrivedNum=arrivedNum;
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

}
