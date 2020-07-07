package com.mingxiao.leader;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class utils {
    //map来代表分布式锁
    public static Map<String, String> lockmap = new HashMap<String, String>();

    public static String getLock(String lockname,String requester,Boolean isLeader){
        String info="";
        //如果是leader的话，只用查看本地的Map即可
        if(isLeader){
            if(lockmap.containsKey(lockname)){
                String lockowner = lockmap.get(lockname);
                info="error:This lock is occupied!";
            }else{
                lockmap.put(lockname,requester);
                info=requester+" get lock success:"+lockname;
            }
        }else{//如果是follower，需要和远端leader通信以获取锁信息
            System.out.println("get lock from remote lead server······");
        }
        return info;
    }

    public static String freeLock(String lockname, String requester, Boolean isLeader){
        String info="";
        if(isLeader){
            System.out.println("free lock ······");
            if(!lockmap.containsKey(lockname)){
                info = "error:This lock doesn't exist!";
                //System.out.println("error:This lock doesn't：exist!");
            }else{
                if(lockmap.get(lockname).equals(requester)){//我拥有该锁
                    lockmap.remove(lockname);
                    info = requester+" free lock success:"+lockname;
                    //System.out.println("成功释放锁："+lockname);
                }else{
                    info = "error:This lock doesn't belong to your";
                   // System.out.println("error:This lock doesn't belong to your");
                }
            }
        }else{
            System.out.println("free lock from remote lead server······");
            //需和远端通信
        }
        return info;
    }
    public static void checkLock(String lockname){
        System.out.println("check lock ······");
        if(!lockmap.containsKey(lockname)){
            System.out.println("error:doesn't exist lock:"+lockname);
        }else{
            String lockowner = lockmap.get(lockname);
            System.out.println("Lock: "+lockname+"'s owner is"+lockowner);
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
        String para = message.substring(loc++);
        return para;
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
