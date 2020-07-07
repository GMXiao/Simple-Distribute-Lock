package com.mingxiao.follower;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class ServerService {
    public static Boolean isLeader = false;
    public static String myIP = "202.120.38.100";
    public static String leaderIP = "119.3.33.187";
    public static String myClientID = myIP;
    public void initServer() {
            //
            final Scanner scanner = new Scanner(System.in);
            String mode = scanner.nextLine();
            if(!mode.equals("test"))System.out.println("Please enter command: test to start a test!");
            if (mode.equals("test")) {
                //首先开线程连接leader
                try {
                    String threadname = "leader";
                    Socket client = new Socket(leaderIP,9090);//绑定服务器的ip和端口号
                    // 获取客户端的输入输出流
                    final InputStream ins = client.getInputStream();
                    final OutputStream ous = client.getOutputStream();
                    //与leader进行第轮的信息交互
                    String msg =utils.readMsg(ins);
                    System.out.println(msg);
                    utils.sendMsg(ous,myClientID);
                    //开启两个线程来处理发送和接受信息
                    //发送消息线程
                    new Thread() {
                        public void run() {
                            try {
                                while (true) {
                                    // 从控制台得到指令
                                    String order = scanner.next();//三种指令：getlock, freelock, checklock
                                    String para = scanner.nextLine();//锁名称： lock name
                                    switch (order) {
                                        case "getlock":
                                            if(utils.lockmap.containsKey(para)){
                                                System.out.println("error:This lock is occupied!");
                                            } else{
                                                utils.sendMsg(ous,order+para);
                                            }
                                            break;
                                        case "freelock":
                                            if(!utils.lockmap.containsKey(para)){
                                                System.out.println("error:This lock doesn't exist!");
                                            }else if (!utils.lockmap.get(para).equals(myClientID)){
                                                System.out.println("error:This lock doesn't belong to you");
                                            }else{
                                                utils.sendMsg(ous,order+para);
                                            }
                                            break;
                                        case "checklock":
                                            utils.checkLock(para);
                                            break;
                                        case "help":
                                            utils.getHelpInfo();
                                            break;
                                        default:
                                            System.out.println("error:Your command isn't right!");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                    //读取消息线程
                    new Thread() {
                        public void run() {
                            try {
                                while (true) {
                                    String message = utils.readMsg(ins);
                                    System.out.println(message);
                                    utils.handle(message);//更改本地map
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
}
