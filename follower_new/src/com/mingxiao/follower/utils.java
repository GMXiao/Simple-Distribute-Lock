package com.mingxiao.follower;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class utils {
    //map来代表分布式锁
    public static Map<String, String> lockmap = new HashMap<String, String>();

    public static void checkLock(String lockname){
        System.out.println("check lock ······");
        if(!lockmap.containsKey(lockname)){
            System.out.println("error:doesn't exist lock:"+lockname);
        }else{
            String lockowner = lockmap.get(lockname);
            System.out.println("Lock"+lockname+"'s owner is"+lockowner);
        }

    }

    // 发送消息的函数
    public static void sendMsg(OutputStream os, String s) throws IOException {
        // 向客户端输出信息
        byte[] bytes = s.getBytes();
        os.write(bytes);
        os.write(13);
        os.write(10);
        os.flush();

    }

    // 读取客户端输入数据的函数
    public static String readMsg(InputStream ins) throws Exception {
        // 读取客户端的信息
        int value = ins.read();
        // 读取整行 读取到回车（13）换行（10）时停止读
        String str = "";
        while (value != 10) {
            // 点击关闭客户端时会返回-1值
            if (value == -1) {
                throw new Exception();
            }
            str = str + ((char) value);
            value = ins.read();
        }
        str = str.trim();
        return str;
    }

    //得到传输的信息中的命令
    public static String getOrder(String message){
        int loc = 0;
        for (int i=0;i<message.length();i++){
            if(message.charAt(i)==' '){
                loc = i;
                break;
            }
        }
        String order = message.substring(0,loc);
        return order;
    }

    //得到传输的信息中的参数
    public static String getPara(String message){
        int loc = 0;
        for (int i=0;i<message.length();i++){
            if(message.charAt(i)==' '){
                loc = i;
                break;
            }
        }
        String para = message.substring(loc);
        return para;
    }

    public static void handle(String message){
        String info = message.substring(0,5);
        if(!info.equals("error")){
            int[] loc = new int[4];
            int mid = 0;
            for (int i=0;i<message.length();i++){
                if(message.charAt(i)==' '){
                    loc[mid] = i;
                    mid++;
                }
            }
            if(message.substring(loc[0]+1,loc[1]).equals("get")){
                lockmap.put(message.substring(loc[3]),message.substring(0,loc[0]));
            }else{
                lockmap.remove(message.substring(loc[3]));
            }

        }
    }
    //帮助信息
    public static void getHelpInfo(){
        System.out.println("This tool is used for Big data project2: distribute lock test.");
        System.out.println("Usage: <command> [parameter],  Where parameter is name of a lock");
        System.out.println("Commands are:");
        System.out.println("                getlock      get a lock from the distribute sysetem");
        System.out.println("                freelock     release the lock you own");
        System.out.println("                checklock    check a lock belongs to whom");
        System.out.println("                help         get help for how to use this system");
    }
}
