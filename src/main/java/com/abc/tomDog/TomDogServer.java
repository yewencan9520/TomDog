package com.abc.tomDog;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TomDogServer {

    public static HashMap<String, HttpServlet> servletHashMap;
    public static ServerSocket serverSocket;
    private HttpRequestAndResponse httpRequestAndResponse;
    private ExecutorService service;

    public TomDogServer() {
        try {
            //创建一个ServerSocket对象
            serverSocket = new ServerSocket(8080);
            TomDogWebServletScan tomcatWebServletScan = new TomDogWebServletScan();
            servletHashMap = tomcatWebServletScan.servletScan();

            httpRequestAndResponse=new HttpRequestAndResponse(servletHashMap);
            //创建了一个定长线程池，只有6个线程
            service = Executors.newFixedThreadPool(100);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     *开启线程
     */
    private void start() throws IOException {
        while (true) {
            //每来一个请求都会有一个socket对象被创建
            //当没有请求的时候，会一致阻塞在此处
            final Socket socket = serverSocket.accept();
            //每一个请求都给其分配一个线程进行操作
            //从线程池中获取线程执行请求任务
            service.execute(new RequestRunnable(socket,httpRequestAndResponse));
        }
    }

    public static void main(String[] args) throws IOException {

        TomDogServer tomcatServer = new TomDogServer();
        tomcatServer.start();//开始接收请求
    }
}













