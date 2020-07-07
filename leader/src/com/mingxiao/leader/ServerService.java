package com.mingxiao.leader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class ServerService {
    public static Boolean isLeader = true;
    public static String myIP = "119.3.33.187";
    public static String[] followerIP = {"202.120.38.131","202.120.38.100"};
    public static String myClientID = myIP;
    public static int followerserver = 2;
    public static ArrayList<LeaderThread> list =new ArrayList<LeaderThread>();
    public void initServer() throws IOException {
            final Scanner scanner = new Scanner(System.in);
            String mode = scanner.nextLine();
            if(!mode.equals("test"))System.out.println("Please enter command: test to start a test!");
            if (mode.equals("test")) {
                //开启另外线程监听其他服务器
                try {
                    //创建服务器端对象,并指定端口号
                    ServerSocket server = new ServerSocket(9090);
                    System.out.println("Leader has built......");
                    //不断获取客户端的连接
                    for (int i=0;i<followerserver;i++){
                        Socket socket =server.accept();
                        //System.out.println("Follower连接进来了......");
                        //当有客户端连接进来以后，开启一个线程，用来处理该客户端的逻辑,
                        String threadname = "leader"+followerserver;
                        LeaderThread st = new LeaderThread(socket,threadname);
                        st.start();
                        //添加该客户端到容器中
                        list.add(st);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                System.out.println("Start the distribute lock test······");
                while (true) {//主线程，处理本地的请求
                    System.out.println("Please enter the command······");
                    String order = scanner.next();//getlock, freelock, checklock
                    String para = scanner.nextLine();// lock name
                    if(order.equals("exit"))break;//退出测试
                    String info ="";
                    switch (order) {
                        case "help":
                            info = "This is a help info";
                            utils.getHelpInfo();
                            break;
                        case "getlock":
                            info = utils.getLock(para,myClientID,isLeader);
                            System.out.println(info);
                            break;
                        case "freelock":
                            info = utils.freeLock(para,myClientID,isLeader);
                            System.out.println(info);
                            break;
                        case "checklock":
                            info = "This is a check";
                            utils.checkLock(para);
                            break;
                        default:
                            info = "error:Your command isn't right!";
                            System.out.println(info);
                    }
                    if (!(info.equals("error:This lock is occupied!")||
                            info.equals("This is a help info")||
                            info.equals("")||
                            info.equals("This is a check")||
                            info.equals("error:Your command isn't right!")||
                            info.equals("error:This lock doesn't exist!")||
                            info.equals("error:This lock doesn't belong to your"))) {
                        //给容器中的每个对象转发消息
                        System.out.println("Send request to follower,follower number is "+ServerService.list.size());
                        for (int i = 0; i <ServerService.list.size(); i++) {
                            LeaderThread st =ServerService.list.get(i);
                            //不给自己转发消息
                            System.out.println("Send request......");
                            utils.sendMsg(st.outputStream,info);
                            System.out.println("Send request success......");
                        }
                    }
                    }
                System.out.println("End the distribute lock test······");
            }
    }
}
