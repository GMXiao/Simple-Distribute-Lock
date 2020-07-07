package com.mingxiao.leader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public  class LeaderThread implements Runnable {
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    private Thread t;
    private String threadName;
    private String ID;
    LeaderThread(Socket socket,String threadname) {
        this.socket = socket;
        threadName = threadname;
        System.out.println("Creating " +  threadName );
    }

    public void run() {
        // 创建服务器ServerSocket对象，对象中传递系统要指定的端口号。
        try {
            // 使用Socket对象中的方法getInputStream()获取网络字节输入流InputStream对象。
            inputStream = socket.getInputStream();
            // 使用Socket对象中的方法getOutputStream()获取网络字节输出流OutputStream对象。
            outputStream = socket.getOutputStream();

            String msg = "You connect Leader success! ";
            utils.sendMsg(outputStream, msg);

            //收取follower的名字
            ID=utils.readMsg(inputStream);
            System.out.println("follower: "+ID+" connect success!");

            //读取Follower端发来的消息进行处理
            msg=utils.readMsg(inputStream);
            System.out.println(msg);
            while(!"disconnect".equals(msg)){
                String order = utils.getOrder(msg);
                String para = utils.getPara(msg);
                String info = "";
                switch (order) {
                    case "getlock":
                        System.out.println(ID+" want to get lock:"+para);
                        //这里虽然是远端follower发来的请求，但是leader帮他请求锁，所以isLeader是true
                        info = utils.getLock(para,ID,true);
                        System.out.println(info);
                        break;
                    case "freelock":
                        System.out.println(ID+" want to free lock:"+para);
                        info = utils.freeLock(para,ID,true);
                        System.out.println(info);
                        break;
                    default:
                        info = "error:Your command isn't right!";
                        System.out.println(info);
                }

                if (!(info.equals("error:This lock is occupied!")||
                        info.equals("error:Your command isn't right!")||
                        info.equals("error:This lock doesn't exist!")||
                        info.equals("")||
                        info.equals("error:This lock doesn't belong to your"))){
                    //给容器中的每个对象转发消息
                    System.out.println("Send request to follower,follower number is "+ServerService.list.size());
                    for (int i = 0; i <ServerService.list.size(); i++) {
                        LeaderThread st =ServerService.list.get(i);
                        //不给自己转发消息
                        System.out.println("Send request ......");
                        utils.sendMsg(st.outputStream,info);
                        System.out.println("Send request success......");
                    }
                }
                //等待读取下一次的消息
                msg=utils.readMsg(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start () {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

}
