package com.abc.tomDog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class HttpRequestAndResponse {

     private  HashMap<String, HttpServlet> servletHashMap;

    public HttpRequestAndResponse(HashMap<String, HttpServlet> servletHashMap) {
        this.servletHashMap = servletHashMap;
    }


    /**
     * 响应202状态码
     */
    public void httpAccepted(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        //服务器收到浏览器的请求之后，需要返回202，之后才能读取数据
        String begin = "HTTP/1.1 202 Accepted\n" +
                // "Location: http://www.baidu.com\n"+
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                "Server: Apache\n";
        outputStream.write(begin.getBytes());
        outputStream.flush();
    }


    /**
     * 请求
     */
    public void request(Socket socket) throws IOException {
        //读取浏览器请求的数据
        InputStream inputStream = socket.getInputStream();
        //用来存放input读取到的数据
        byte[] buffer = new byte[1024];
        //用来记录读取了多少个字节
        int len = 0;
        StringBuilder stringBuilder = new StringBuilder();
        //如果没有数据了，input会返回一个-1
        while (true) {
            len = inputStream.read(buffer);
//                if (len == -1) {
//                    break;
//                }
            //浏览器如果不关闭，此处的流就会一直等待
            stringBuilder.append(new String(buffer,0,len));
            if (len < 1024) {
                break;
            }
        }
        HttpBean httpBean = parseHttpStr(stringBuilder.toString());
        // 想访问的servlet
        String servletUri = httpBean.getServletUri();

        HttpServlet httpServlet = servletHashMap.get(servletUri);
        //进行Get和Post请求分发
        if ("Get".toUpperCase().equals(httpBean.getRequestMethod())) {
            //Get请求
            if ( httpServlet != null) {
                httpServlet.doGet(); //接口回调
            }
        } else if ("Post".toUpperCase().equals(httpBean.getRequestMethod())){
            //Post请求
            if ( httpServlet != null) {
                httpServlet.doPost(); //接口回调
            }
        }

//        inputStream.close();
    }


    /**
     * 响应
     */
    public void response(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String body = "<html><head></head><body><h1>咿呀咿呀喂</h1></body></html>";

        String str = "HTTP/1.1 200 OK\n" +
                // "Location: http://www.baidu.com\n"+
                "Date: Mon, 27 Jul 2009 12:28:53 GMT\n" +
                "Server: Apache\n" +
                "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\n" +
                "ETag: \"34aa387-d-1568eb00\"\n" +
                "Accept-Ranges: bytes\n" +
                "Content-Length: "+body.length()+"\n" +
                "Vary: Accept-Encoding\n" +
                "Content-Type: text/html; charset=utf-8\n"+
                "\n"+body;
        outputStream.write(str.getBytes());
        outputStream.flush();
//        outputStream.close();
    }


    /**
     * 解析Http协议，并封装到HTTPBean类中
     */
    private HttpBean parseHttpStr(String http){
        HttpBean httpBean = new HttpBean();

        String[] strings = http.split("\n");
        for (int i = 0,len=strings.length; i < len; i++) {
            if (i==0) {
                String[] stateLinesParam = strings[0].split(" ");
                httpBean.setRequestMethod(stateLinesParam[0]);
                httpBean.setUrl(stateLinesParam[1]);
                String[] split = stateLinesParam[1].split("\\?");
                httpBean.setServletUri(split[0]);
            }
        }
        return httpBean;
    }
}
