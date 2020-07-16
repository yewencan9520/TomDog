package com.abc.tomDog;

import com.abc.tomDog.annotation.WebServlet;
import com.abc.tomDog.exception.URIException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class TomDogWebServletScan {

    /**
     * 加载所有的Servlet，并且保存到了Map中
     */
    public HashMap<String, HttpServlet> servletScan()
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, URIException {

        HashMap<String, HttpServlet> servletHashMap = new HashMap<String, HttpServlet>();

        String basePackage = "com.abc.demo";
        String path = basePackage.replaceAll("\\.", "/");
        URL url = TomDogWebServletScan.class.getResource("/");
        File file = new File(url.getPath()+path);
        File[] files = file.listFiles();

        for (int i = 0,size=files.length; i < size; i++) {
            //文件
            if (files[i].isFile()) {
                //所有的class文件
                String filename = files[i].getName();
                if (filename.endsWith(".class")) {
                    String[] strings = filename.split("\\.");
                    String className = strings[0];
                    String allClassName = basePackage+"."+className;
                    Class<?> aClass = Class.forName(allClassName);
                    WebServlet annotation = aClass.getAnnotation(WebServlet.class);
                    if (annotation != null) {
                        String uri = annotation.value();
                        if (servletHashMap.get(uri) != null) {
                            throw new URIException("出现了两个或以上一样的地址：" + uri);
                        }
                        HttpServlet servlet = (HttpServlet) aClass.newInstance();
                        servletHashMap.put(uri,servlet);
                    }
                }
            } else {
                //文件夹
            }
        }
        return servletHashMap;
    }
}
