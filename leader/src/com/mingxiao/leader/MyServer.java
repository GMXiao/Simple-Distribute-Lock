package com.mingxiao.leader;

import java.io.IOException;

public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerService ss = new ServerService();
        ss.initServer();
    }
}
