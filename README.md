# Simple-Distribute-Lock
## 本实验要求设计一个简单的一致性系统，并满足以下条件:
1.	一个 leader server 和多个 follower servers;
2.	每个 follower server 都有一个复制的映射，该映射由 leader server 处理；
3.	映射的 key 是分布式锁的名称，映射的值是分布式锁的 Client ID;
4.	支持多个 clients 抢占/释放分布式锁，并检查分布式锁的所有者：
  a)	抢占分布式锁—如果锁不存在，则抢占成功，否则失败；
  b)	释放分布式锁—如果 client 拥有该锁，则释放成功，否则失败；
  c)	检查分布式锁—任何 client 都可以检查分布式锁的所有者。
5.	为了确保系统的数据一致性，follower servers 将所有抢占/释放请求发送到 leader server。
6.	为了检查分布式锁的所有者，follower servers 直接访问其本地映射，并将结果直接返回到 clients；
7.	当 leader server 需要处理抢占/释放请求时：
  a)	如果需要，则修改其映射并向所有 follower servers 发送一个request propose；
  b) 当 follower server 收到一个 request propose 时，修改其本地映射，检查请求是否等待；如果请求未处理，向 client 发送反馈。
8.	在这个系统中，所有 clients 提供抢占/释放/检查分布式锁的接口；
9.	定义目标服务器的 IP 地址，基于用户信息生成 Client ID 信息。
## 架构设计
一台Leader服务器，多台Follower服务器，Leader和每台Follower之间进行连接，当有锁抢占、释放等操作时会通知每个Follower，Leader拥有主要的一个Lock Map用来管理系统中的锁。Follower可以向Leader请求抢占锁、释放锁，同时自己也有一份Leader端Lock Map的副本。

![分布式锁架构](https://github.com/GMXiao/Simple-Distribute-Lock/blob/master/pic/jiagou.png "分布式锁架构")

