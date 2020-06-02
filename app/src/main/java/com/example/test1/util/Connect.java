package com.example.test1.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Connect {
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public static String ip="192.168.43.73";
    public static int port=9988;
    public Connect() throws IOException {
        while (socket==null){
            socket=new Socket(ip,port);
            socket.setSoTimeout(50000);
        }
    }
    public void post(String message) throws IOException {
        outputStream = socket.getOutputStream();
        socket.getOutputStream().write(message.getBytes("UTF-8"));
        //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
        outputStream.flush();
    }
    public String get() throws IOException {
        inputStream = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String reply = null;
        StringBuilder sb = new StringBuilder();
        reply=br.readLine();
        sb.append(reply);
        return sb.toString();
    }
    public void closeInput() throws IOException {
        if (inputStream!=null){
            inputStream.close();
        }
    }
    public void closeOutput() throws IOException {
        if (outputStream!=null){
            outputStream.close();
        }
    }
    public void closeSocket() throws IOException {
        if (socket!=null){
            socket.close();
        }
    }

}
