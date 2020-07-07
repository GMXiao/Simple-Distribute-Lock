package com.mingxiao.leader;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public  class FollowerThread implements Runnable {
    public Socket socket;
    private Thread t;
    private String threadName;
    private String ID;
    FollowerThread(Socket socket,String threadname) {
        this.socket = socket;
        threadName = threadname;
        System.out.println("Creating " +  threadName );
    }

    public void run() {
        //System.out.println("Running " +  threadName );
        // 创建服务器ServerSocket对象，对象中传递系统要指定的端口号。
        try {
            // 使用Socket对象中的方法getInputStream()获取网络字节输入流InputStream对象。
            InputStream inputStream = socket.getInputStream();
            // 使用Socket对象中的方法getOutputStream()获取网络字节输出流OutputStream对象。
            OutputStream outputStream = socket.getOutputStream();

            String msg = "You connect Leader success! ";
            utils.sendMsg(outputStream, msg);

            //收取follower的名字
            ID=utils.readMsg(inputStream);
            System.out.println("followerID: "+ID);

            //读取Follower端发来的消息进行处理
            msg=utils.readMsg(inputStream);
            while(!msg.equals("disconnect")){
                String order = utils.getOrder(msg);
                String para = utils.getPara(msg);
                System.out.println("1"+order+"1 1"+para+"1");
                switch (order) {
                    case "getlock":
                        //这里虽然是远端follower发来的请求，但是leader帮他请求锁，所以isLeader是true
                        String info = utils.getLock(para,ID,true);
                        utils.sendMsg(outputStream,info);
                        break;
                    case "freelock":
                        utils.freeLock(para,ID,true);
                        break;
                    case "checklock":
                        utils.checkLock(para);
                        break;
                    default:
                        System.out.println("指令不正确");
                }
                //等待读取Follower端发来的下一条消息
                msg=utils.readMsg(inputStream);
            }
            // 使用网络字节输出流OutputStream对象中的方法write，给客户端回写数据。
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