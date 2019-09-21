# 机器环境
```
列表
    192.168.152.102 hadoop01
    192.168.152.103 hadoop02
    192.168.152.104 hadoop03

克隆 
    基于hadoop01 创建 hadoop02,03

修改主机名
    $ vim /etc/hostname

修改网络
    $ vim /etc/sysconfig/network-scripts/ifcfg-ens33

关闭防火墙
    查看状态
    $ sudo systemctl status firewalld.service
    停止服务
    $ sudo systemctl stop firewalld.service
    禁止开机启动
    $ sudo systemctl disable firewalld.service

重启
    $ sudo reboot

ssh 免密互访
    删除旧文件
    $ rm -rf ~/.ssh
    初始化授信
    $ ssh-keygen -t rsa
    分发公钥（包括给自己）
    $ ssh-copy-id -i admin@hadoop01
    $ ssh-copy-id -i admin@hadoop02
    $ ssh-copy-id -i admin@hadoop03

```
# 辅助脚本
```
ssh 远程同步操作
    $ vim /usr/local/bin/xcall
    ----------------------------------------------
    #!/bin/bash
    
    if (($#==0));then
        echo "no args ..."
        exit 0
    fi
    
    USER=`whoami`
    HOST=`hostname`
    DIR=`cd -P $(dirname $1);pwd`
    
    for (( i=1 ; i<=3 ; i++ ))
    do
        #if [ hadoop0$i != $HOST ];then
            echo -e "\n>>> ssh $USER@hadoop0$i \"cd $DIR; $@\""
            ssh $USER@hadoop0$i "cd $DIR; $@"
        #fi
    done
    
    exit 0
    ----------------------------------------------
    
    $ sudo chmod 755 /usr/local/bin/xcall
    
    $ xcall jps
    ----------------------------------------------
    >>> ssh admin@hadoop01 "cd /opt/softwares/hadoop-2.7.2; jps"
    8012 Jps
    
    >>> ssh admin@hadoop02 "cd /opt/softwares/hadoop-2.7.2; jps"
    5651 Jps
    
    >>> ssh admin@hadoop03 "cd /opt/softwares/hadoop-2.7.2; jps"
    5950 Jps
    ----------------------------------------------
    
ssh 远程同步文件(只同步修改)
    ----------------------------------------------
    #!/bin/bash
    
    if (( $# == 0 ));then
        echo "no args"                         
        exit 0
    fi
    
    USER=`whoami`
    DIR=`cd -P $(dirname $1);pwd`
    TARGET=`basename $1`
    MYSELF=`hostname`
    
    for (( i = 1 ; i <= 3 ; i++ ))
        do
         if [ $MYSELF != hadoop0$i ];then
             echo -e "\n>>> rsync -rvl $DIR/$TARGET $USER@hadoop0$i:$DIR"
             rsync -rvl $DIR/$TARGET $USER@hadoop0$i:$DIR
         fi
        done
    
    exit 0
    ----------------------------------------------
    
    $ sudo chmod 755 /usr/local/bin/xsync
    
    $ touch 1.txt
    $ xsync 1.txt （支持相对路径)
    ----------------------------------------------
    >>> rsync -rvl /opt/softwares/hadoop-2.7.2/1.txt admin@hadoop02:/opt/softwares/hadoop-2.7.2
    sending incremental file list
    1.txt
    
    sent 67 bytes  received 31 bytes  196.00 bytes/sec
    total size is 0  speedup is 0.00
    
    >>> rsync -rvl /opt/softwares/hadoop-2.7.2/1.txt admin@hadoop03:/opt/softwares/hadoop-2.7.2
    sending incremental file list
    1.txt
    
    sent 67 bytes  received 31 bytes  196.00 bytes/sec
    total size is 0  speedup is 0.00
    ----------------------------------------------
    
    $ xcall rm 1.txt (支持相对路径)
    ----------------------------------------------
    >>> ssh admin@hadoop01 "cd /opt/softwares/hadoop-2.7.2; rm 1.txt"
    
    >>> ssh admin@hadoop02 "cd /opt/softwares/hadoop-2.7.2; rm 1.txt"
    
    >>> ssh admin@hadoop03 "cd /opt/softwares/hadoop-2.7.2; rm 1.txt"
    ----------------------------------------------
    
```
# 配置
``` 
目录
$ cd /opt/softwares/hadoop-2.7.2

$ vim etc/hadoop/core-site.xml
----------------------------------------------
<!-- 配置NameNode 节点 -->
<property>
	<name>fs.defaultFS</name>
	<value>hdfs://hadoop01:9000</value>
</property>
 
<!-- 配置数据目录 存放 nn dn,sn 数据-->
<property>
	<name>hadoop.tmp.dir</name>
	<value>/opt/softwares/hadoop-2.7.2/data/tmp</value>
</property>
----------------------------------------------

$ vim etc/hadoop/hdfs-site.xml
----------------------------------------------
<!-- 定义hdfs重要性略次于namenode节点的sn节点 -->
<property>
	<name>dfs.namenode.secondary.http-address</name>
	<value>hadoop02:50090</value>
</property>

<!-- 文件系统存储数据块的副本数,从此处体现出为伪布式特点 --> 
<property>
	<name>dfs.replication</name>
	<value>3</value>
</property>
----------------------------------------------

$ vim etc/hadoop/yarn-site.xml
----------------------------------------------
<!-- 数据块汇总洗牌方式 -->
<property>
	<name>yarn.nodemanager.aux-services</name>
	<value>mapreduce_shuffle</value>
</property>

<!-- RM 节点 -->
<property>
	<name>yarn.resourcemanager.hostname</name>
	<value>hadoop03</value>
</property>
----------------------------------------------

$ vim etc/hadoop/mapred-site.xml
----------------------------------------------
<!-- 声明MR 以yarn 方式运行 -->
<property>
	<name>mapreduce.framework.name</name>
	<value>yarn</value>
</property>
----------------------------------------------

$ vim etc/hadoop/slaves
----------------------------------------------
hadoop01
hadoop02
hadoop03
----------------------------------------------
```

启动
```
hadoop01 机器启动 HDFS
$ ./sbin/start-dfs.sh

hadoop03 机器启动 YARN
$ ./sbin/start-yarn.sh

$ xcall jps
----------------------------------------------
>>> ssh admin@hadoop01 "cd /opt/softwares/hadoop-2.7.2; jps"
6834 NodeManager
6435 NameNode
8517 Jps
6571 DataNode

>>> ssh admin@hadoop02 "cd /opt/softwares/hadoop-2.7.2; jps"
6180 Jps
4902 NodeManager
4700 DataNode
4798 SecondaryNameNode

>>> ssh admin@hadoop03 "cd /opt/softwares/hadoop-2.7.2; jps"
4660 NodeManager
4407 DataNode
4551 ResourceManager
6393 Jps
----------------------------------------------
```

grep 测试
```
$ hadoop fs -mkdir -p /apps/mr/grep/in
$ hadoop fs -put ../../apps/mr/grep/in/* /apps/mr/grep/in/
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep /apps/mr/grep/in /apps/mr/grep/out 'www[a-z.]+'
```

wordcount 测试
```
$ hadoop fs -mkdir -p /apps/mr/wc/in
$ hadoop fs -put ../../apps/mr/wc/in/* /apps/mr/wc/in/  
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /apps/mr/wc/in /apps/mr/wc/out
```
 
web 页面
```
hdfs 
http://hadoop01:50070/
    
yarn
http://hadoop03:8088/

```

历史记录日志聚合
```
配置
$ vim etc/hadoop/mapred-site.xml
----------------------------------------------
<!--历史服务-->
<property>
	<name>mapreduce.jobhistory.address</name>
	<value>hadoop02:10020</value>
</property>

<!--历史服务http通信 -->
<property>
	<name>mapreduce.jobhistory.webapp.address</name>
	<value>hadoop02:19888</value>
</property>
----------------------------------------------

vim etc/hadoop/yarn-site.xml
----------------------------------------------
<!-- 开启聚合日志服务,所有 --> 
<property>
	<name>yarn.log-aggregation-enable</name>
	<value>true</value>
</property>

<!-- 日志保留时间(秒)-1 永不保存-->
<property>
	<name>yarn.log-aggregation.retain-seconds</name>
	<value>7200</value>
</property>
----------------------------------------------

启动（在指定机器执行）
hadoop02机器上执行
$ ./sbin/mr-jobhistory-daemon.sh start historyserver

$ jps
----------------------------------------------
4902 NodeManager
6569 JobHistoryServer
6793 Jps
4700 DataNode
4798 SecondaryNameNode
----------------------------------------------

再次运行 grep 测试 YARN 上就可以在 HISTORY 查看日志

```

自定义脚本
``` 
$ vim sbin/startAll.sh
----------------------------------------------
#!/bin/bash

/usr/local/bin/xcall jps

ssh admin@hadoop01 "cd /opt/softwares/hadoop-2.7.2; ./sbin/start-dfs.sh"
ssh admin@hadoop03 "cd /opt/softwares/hadoop-2.7.2; ./sbin/start-yarn.sh"
ssh admin@hadoop02 "cd /opt/softwares/hadoop-2.7.2; ./sbin/mr-jobhistory-daemon.sh start historyserver"

/usr/local/bin/xcall jps
----------------------------------------------

$ vim sbin/stopAll.sh 
----------------------------------------------
#!/bin/bash

/usr/local/bin/xcall jps

ssh admin@hadoop02 "cd /opt/softwares/hadoop-2.7.2; ./sbin/mr-jobhistory-daemon.sh stop historyserver"
ssh admin@hadoop03 "cd /opt/softwares/hadoop-2.7.2; ./sbin/stop-yarn.sh"
ssh admin@hadoop01 "cd /opt/softwares/hadoop-2.7.2; ./sbin/stop-dfs.sh"

/usr/local/bin/xcall jps
----------------------------------------------
```

时间同步服务
```
同步规划
    ----------------------------------------------
    192.168.152.102 hadoop01   server (sync with pool.ntp.org)
    192.168.152.102 hadoop02   client (sync with server)
    192.168.152.102 hadoop03   client (sync with server)
    ----------------------------------------------

权限(切换到 root 运行)
    sudo su - root

集群时区
    查看时区(+0800 东8区)
    # date -R
    >>> ssh admin@hadoop01 date -R
    Sat, 21 Sep 2019 18:20:14 +0800
    
    >>> ssh admin@hadoop02 date -R
    Sat, 21 Sep 2019 18:20:14 +0800
    
    >>> ssh admin@hadoop03 date -R
    Sat, 21 Sep 2019 18:20:14 +0800
    
    删除已经存在时区（按需执行）
    # rm -rf /etc/localtime
    
    创建软链时区
    ln -s /usr/share/zoneinfo/Asia/Shanghai /etc/localtime 

server(hadoop01)
    查看 ntpd 服务状态 (activate / inactivate）
    # systemctl status ntpd
    
    停止时间同步(为演示手动同步，需先停止自动同步)
    # systemctl stop ntpd
    
    手动同比 pool.ntp.org 或 ntp1.aliyun.com
    # ntpdate pool.ntp.org
    
    启动时间同步服务
    # systemctl start ntpd
    
    检测是否配置开机启动 systemctl enable|disable ntpd
    # systemctl list-unit-files | grep enable | grep ntpd
    
    # vim /etc/ntp.conf (参照如下内容修改)
    ----------------------------------------------
    # Hosts on local network are less restricted.
    #   nomodify  禁止客户端通过 ntpc ntpd 修改服务器时间
    #    notrap  禁止 trap 远程登录
    restrict 192.168.1.0 mask 255.255.255.0 nomodify notrap
    
    # Use public servers from the pool.ntp.org project.
    # Please consider joining the pool (http://www.pool.ntp.org/join.html).
    # 封闭集群环境，可以将如下 server 注释，时间同步服务器就无法联网，但客户端能与服务器同步时间
    server 0.centos.pool.ntp.org iburst
    server 1.centos.pool.ntp.org iburst
    server 2.centos.pool.ntp.org iburst
    server 3.centos.pool.ntp.org iburst
    
    #  将本机本地时钟作为时间供给源，这样，即便它失去网络连接，它也可以继续为网络提供服务; 
    server 127.127.1.0 
    fudge 127.127.1.0 stratum 10 
    ----------------------------------------------
    
    重启时间同步服务
    # systemctl restart ntpd
    
    定时任务每 5 分钟将系统时间同步给硬件时间
    # crontab -e
    ----------------------------------------------
    # 15 分钟将网络时间同步给硬件时钟
    */15 * * * * /sbin/hwclock -w
    ----------------------------------------------
    
    检测定时任务状态 systemctl start|stop crontab
    # systemctl status crontab
    
    检测定时任务是否开机自启 systemctl enable|disable crond
    # systemctl list-unit-files | grep enable | grep crond

client(hadoop02 hadoop03)
    关闭时间同步服务
    # systemctl stop ntpd
    
    禁止开机启动时间自动同步服务
    # systemctl disable ntpd
    
    测试手动同步
    # ntpd hadoop01 
    
    基于 crontab 周期性与 server 同步时间
    # crontab -e
    ----------------------------------------------
    # 5 分钟与 hadoop01 同步时间
    */5 * * * * /usr/sbin/ntpdate hadoop01
    
    # 15 分钟将网络时间同步给硬件时钟
    */15 * * * * /sbin/hwclock -w
    ----------------------------------------------
    
    查看定时任务状态 systemctl start|stop crond
    # systemctl status crond 
    
    查看开机启动状态 systemctl enable|disable crond
    # systemctl list-unit-files | grep enable | grep crond

测试同步效果
    hadoop02 机器 （手动将日期拨乱）
    # date -s '2019-09-01 00:00:00'
    
    等待 5 分钟，自动与 server 同步矫正

```



