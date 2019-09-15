# 环境介绍
```
MAC OS + VMvareFusion
CentOS Linux release 7.3.1611 (Core)
openjdk version "1.8.0_222"
hadoop-3.1.2
```

# MAC 允许外部安装源
```
sudo spctl --master-disable
```

# 安装 VMvareFusion
```
下载http://xclient.info/?_=a819a092e8c1b32605effffb98afdf6a
```

# 安装虚拟机
```
下载
    CentOS-7-x86_64-DVD-1611.ios
    https://pan.baidu.com/s/1h9gBytEF-iD8NyvrjCMLAQ 提取码: 5ew3

镜像安装
    vmware > 新建 > 自定义虚拟机 > 加载 CD > 带 GUI 安装 > 创建 root 用户 > 创建普通用户，并添加到管理员组

设置运行级别
    $ cat vim /etc/inittab 查看需要系统启动设置
    ----------------------------------------------
    # multi-user.target: analogous to runlevel 3  多用户运行级别
    # graphical.target: analogous to runlevel 5   图形化运行级别
    ----------------------------------------------

    设置为多用户运行级别(命令行启动)
    $ systemctl set-default multi-user.target

    查看是否设置成功
    $ systemctl get-default

sudo免密操作设置
    $ sudo vim /etc/sudoers
    # %wheel        ALL=(ALL)       NOPASSWD: ALL
    admin   ALL=(ALL)       NOPASSWD: ALL

主机名
    $ sudo vim /etc/hostname
    hadoop.01

    $ hostname hadoop.01

网络
    $ sudo vim /etc/sysconfig/network-scripts/ifcfg-ens33
    ----------------------------------------------
    TYPE="Ethernet"
    BOOTPROTO="static"
    DEFROUTE="yes"
    PEERDNS="yes"
    PEERROUTES="yes"
    IPV4_FAILURE_FATAL="no"
    IPV6INIT="no"
    IPV6_AUTOCONF="no"
    IPV6_DEFROUTE="no"
    IPV6_PEERDNS="no"
    IPV6_PEERROUTES="no"
    IPV6_FAILURE_FATAL="no"
    IPV6_ADDR_GEN_MODE="stable-privacy"
    NAME="ens33"
    UUID="e1a3cf58-e44d-4605-af69-a00709de5908"
    DEVICE="ens33"
    ONBOOT="yes"
    IPADDR="192.168.152.102"
    NETMASK="255.255.255.0"
    GATEWAY="192.168.152.2"
    DNS1="8.8.8.8"
    DNS2="114.114.114.114"
    ----------------------------------------------

    sudo systemctl restart network

    ping -c 3 www.baidu.com

    ping 192.168.1.7 与宿主机互访测试

hosts主机名 IP 映射
    $ sudo vim /etc/hosts
    ----------------------------------------------
    192.168.152.102 hadoop.01
    ----------------------------------------------

ssh免密登录
    虚拟机生成 ssh key
    $ sudo ssh-keygen -t rsa

    运行 192.168.1.7 以 admin 身份免密登录
    $ ssh-copy-id -i admin@192.168.1.7

    宿主机免密登录测试
    $ ssh admin@hadoop.01

使用 163 yum 源，替换原生安装源
    $ wget http://mirrors.163.com/.help/CentOS7-Base-163.repo
    $ sudo mv /etc/yum.repos.d/CentOS-Base.repo  /etc/yum.repos.d/CentOS-Base.repo.bk
    $ sudo cp CentOS7-Base-163.repo /etc/yum.repos.d/CentOS-Base.repo
    $ sudo yum clean all
    $ sudo yum makecache

安装mlocate
    $ sudo yum install mlocate
    $ updatedb


```

# java 环境
```
    查看是否安装了原生 java 相关环境，如果安装，则卸载
    $ rpm -qa |grep java
    $ rpm -qa | grep java | xargs sudo rpm -e --nodeps

    $ rpm -qa |grep jdk
    $ rpm -qa | grep jdk | xargs sudo rpm -e --nodeps

    $ rpm -qa |grep gcj
    $ rpm -qa | grep gcj | xargs sudo rpm -e --nodeps

    安装 openjdk
    $ sudo yum install java-1.8.0-openjdk* -y

    查看 java版本
    $ java -version
    openjdk version "1.8.0_222"

    $ locate rt.jar
    /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-0.el7_6.x86_64/jre/lib/rt.jar

    对所有用户适用
    $ sudo vim /etc/profile
    ----------------------------------------------
    # java
    export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-0.el7_6.x86_64/jre"
    export CLASSPATH=.:$JAVA_HOME/lib/rt.jar
    export PATH=$PATH:$JAVA_HOME/bin
    ----------------------------------------------
    $ source /etc/bashrc

    对所有 shell 脚本适用
    $ sudo vim /etc/bashrc
    ----------------------------------------------
    # java
    export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-0.el7_6.x86_64/jre"
    export CLASSPATH=.:$JAVA_HOME/lib/rt.jar
    export PATH=$PATH:$JAVA_HOME/bin
    ----------------------------------------------
    $ source /etc/bashrc

```

# hadoop(单节点)
```
$ sudo mkdir /opt/softwares /opt/downloads
$ sudo chown -R admin:admin /opt/softwares /opt/downloads
$ cd /opt/downloads/

二进制
wget -o hadoop-3.1.2.tar.gz https://www-us.apache.org/dist/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz

源码
wget -o hadoop-3.1.2-src.tar.gz https://www-us.apache.org/dist/hadoop/common/hadoop-3.1.2/hadoop-3.1.2-src.tar.gz

tar -zxvf hadoop-3.1.2.tar.gz

mv hadoop-3.1.2 /opt/softwares

$ sudo vim /etc/profile
----------------------------------------------
# mr
export HADOOP_HOME="/opt/softwares/hadoop-3.1.2"
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
----------------------------------------------
source /etc/profile

$ sudo vim /etc/bashrc
----------------------------------------------
# mr
export HADOOP_HOME="/opt/softwares/hadoop-3.1.2"
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
----------------------------------------------
source /etc/profile

目录结构
$ ll /opt/softwares/hadoop-3.1.2/
    bin 二进制命令
    etc 配置文件
    include 库函数
    lib 对系统的依赖
    libexec 配置脚本
    sbin    集群控制脚本
    share   文档、客户端与测试用例
    LICENSE.txt
    NOTICE.txt
    README.txt

```

grep 测试
```
$ sudo mkdir -p  /opt/apps/mr/grep/in

$ sudo chown -R admin:admin /opt/apps

$ sudo updatedb

$ locate core-site.xml
$ cp /opt/softwares/hadoop-3.1.2/etc/hadoop/core-site.xml /opt/apps/mr/grep/in

正则搜索 /opt/apps/mr/grep/in/*文件内容，找出包含 www 的行数
$ cd /opt/softwares/hadoop-3.1.2
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.2.jar grep /opt/apps/mr/grep/in/* /opt/apps/mr/grep/out 'www[a-z.]+'


$ ll /opt/apps/mr/grep/out/
----------------------------------------------
-rw-r--r-- 1 admin admin 17 9月  15 23:00 part-r-00000
-rw-r--r-- 1 admin admin  0 9月  15 23:00 _SUCCESS  空白文件，标记 MR 执行成功
----------------------------------------------

$ cat /opt/apps/mr/grep/out/part-r-00000
----------------------------------------------
1	www.apache.org
----------------------------------------------
```

wordcount 测试
```
$ mkdir -p  /opt/apps/mr/wc/in/

$ vim /opt/apps/mr/wc/in/words.txt
----------------------------------------------
aa bb cc
aa a bb
s dd cc
----------------------------------------------

$ cd /opt/softwares/hadoop-3.1.2

# 统计单词出现次数
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.2.jar wordcount /opt/apps/mr/wc/in/* /opt/apps/mr/wc/out

$ ll /opt/apps/mr/wc/out/
----------------------------------------------
-rw-r--r-- 1 admin admin 28 9月  15 23:10 part-r-00000
-rw-r--r-- 1 admin admin  0 9月  15 23:10 _SUCCESS   空白文件，标记 MR 执行成功
----------------------------------------------

$ cat /opt/apps/mr/wc/out/part-r-00000
----------------------------------------------
a	1
aa	2
bb	2
cc	2
dd	1
s	1
----------------------------------------------

```



