# 基础安装

```
目录
$ cd /opt/softwares/hadoop-2.7.2

配置 （ vim 带格式粘贴 :set paste）
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
<!-- 文件系统存储数据块的副本数,从此处体现出为伪布式特点 --> 
<property>
    <name>dfs.replication</name>
    <value>1</value>
</property>
----------------------------------------------

初始化
$ bin/hdfs namenode -format
.... 结尾处出现下面内容，表面格式化成功
Storage directory /opt/softwares/hadoop-2.7.2/data/tmp/dfs/name has been successfully formatted.
....

$ ll data/tmp/dfs/name/current/
----------------------------------------------
fsimage_0000000000000000000        初始镜像文件
fsimage_0000000000000000000.md5    镜像文件 md5校验文件
seen_txid                          操作 id
VERSION                            版本号，默认 0 升级新特性修改
----------------------------------------------

启动 NN
$ sbin/hadoop-daemon.sh start namenode

$ jps
----------------------------------------------
3001 NameNode
3068 Jps
----------------------------------------------

$ tailf log logs/hadoop-admin-namenode-hadoop01.out

启动 DN
$ sbin/hadoop-daemon.sh start datanode

$ jps
----------------------------------------------
3111 DataNode
3001 NameNode
3182 Jps
----------------------------------------------

tailf logs/hadoop-admin-datanode-hadoop01.out

常用命令(参照 shell)
hadoop fs -ls 查看
hadoop fs -ls -R 递归查看目录
hadoop fs -mv /a/1.txt /a/2.txt /a/aa  将前面所有移动到最后一个（移动或重命名）
hadoop fs -cp /a/1.txt /a/1.txt.bk 备份
hadoop fs -scp  集群拷贝
hadoop fs -cat xx.txt 查看文本
hadoop fs -mkdir -p /a/b/c 创建多重目录 与 fs.defaultFS 为根
hadoop fs -rm xx.txt 删除 (开启回收站情况，回移动到回收站)
hadoop fs -rm -r xx.txt 递归删除
hadoop fs -rm -r --skipTrash /a 递归删除（直接删除跳过回收站）
hadoop fs -du -h /apps 查看制定目录所占空间(默认字节为单位，例： 844  /apps/mr)
hadoop fs -df -h / 查看系统可用空间
----------------------------------------------
Filesystem              Size  Used  Available  Use%
hdfs://hadoop01:9000  17.0 G  44 K     10.0 G    0%
----------------------------------------------
```

# HDFS 上运行 MR
```
目录
$ cd /opt/softwares/hadoop-2.7.2

$ jps
----------------------------------------------
3111 DataNode
3001 NameNode
3182 Jps
----------------------------------------------
GREP 测试 
创建测试目录
$ hadoop fs -mkdir -p /apps/mr/grep/in

本地测试数据上传 hdfs
$ hadoop fs -put ../../apps/mr/grep/in/* /apps/mr/grep/in

运行测试样例（扫描指定目录下文件，正则查找包含 www 的行数）
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep /apps/mr/grep/in /apps/mr/grep/out 'www[a-z.]+'
# 忽略 EBADF: Bad file descriptor 报警

$ hadoop fs -ls /apps/mr/grep/out
----------------------------------------------
Found 2 items
-rw-r--r--   1 admin supergroup          0 2019-09-20 22:56 /apps/mr/grep/out/_SUCCESS  MR 执行成功标记
-rw-r--r--   1 admin supergroup         17 2019-09-20 22:56 /apps/mr/grep/out/part-r-00000 结果
----------------------------------------------

$ hadoop fs -cat /apps/mr/grep/out/part-r-00000
----------------------------------------------
1	www.apache.org
----------------------------------------------

WORDCOUNT 测试
    
创建测试目录
$ hadoop fs -mkdir -p /apps/mr/wc/in

本地测试数据上传 hdfs
$ hadoop fs -put ../../apps/mr/wc/in/* /apps/mr/wc/in

运行测试样例(wordcount)
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /apps/mr/wc/in /apps/mr/wc/out

$ hadoop fs -ls /apps/mr/wc/out
----------------------------------------------
Found 2 items
-rw-r--r--   1 admin supergroup          0 2019-09-20 22:59 /apps/mr/wc/out/_SUCCESS
-rw-r--r--   1 admin supergroup         28 2019-09-20 22:59 /apps/mr/wc/out/part-r-00000
----------------------------------------------

$ hadoop fs -cat /apps/mr/wc/out/part-r-00000
----------------------------------------------
a	1
aa	2
bb	2
cc	2
dd	1
s	1
----------------------------------------------

停止集群
$ ./sbin/hadoop-daemon.sh stop datanode
$ ./sbin/hadoop-daemon.sh stop namenode

$ jps

检测 NN 目录
$ ll data/tmp/dfs/name/current/
----------------------------------------------
总用量 2072
-rw-rw-r-- 1 admin admin 1048576 9月  20 22:59 edits_0000000000000000001-0000000000000000067  早期编辑日志
-rw-rw-r-- 1 admin admin 1048576 9月  20 23:34 edits_inprogress_0000000000000000068  当前编辑日志
-rw-rw-r-- 1 admin admin     352 9月  20 22:31 fsimage_0000000000000000000         早期镜像文件,停机后内容为空，开机后先滚动合并，然后加载数据
-rw-rw-r-- 1 admin admin      62 9月  20 22:31 fsimage_0000000000000000000.md5
-rw-rw-r-- 1 admin admin    1378 9月  20 23:34 fsimage_0000000000000000067         当前镜像文件 保存的是偏移量 >67 之后操作的镜像文件
-rw-rw-r-- 1 admin admin      62 9月  20 23:34 fsimage_0000000000000000067.md5
-rw-rw-r-- 1 admin admin       3 9月  20 23:34 seen_txid     偏移量记录(之后操作从 68 开始标记)
-rw-rw-r-- 1 admin admin     207 9月  20 23:34 VERSION       记录 namenode id, 集群 id,集群创建时间，块池 id 等信息
----------------------------------------------

$ cat data/tmp/dfs/name/current/seen_txid 
----------------------------------------------
68
----------------------------------------------
 
$ cat VERSION 
----------------------------------------------
#Fri Sep 20 23:34:14 CST 2019
namespaceID=2138521285
clusterID=CID-ee13d398-0f2a-45dc-a8f4-28964347db33
cTime=0
storageType=NAME_NODE
blockpoolID=BP-246176607-192.168.152.102-1568989917089
layoutVersion=-63
----------------------------------------------

重启机器（发现表编辑日志滚动，镜像文件重新加载了）
./sbin/hadoop-daemon.sh start namenode
./sbin/hadoop-daemon.sh start datanode

namenode 目录
$ tree data/tmp/dfs/name/
data/tmp/dfs/name/
├── current
│   ├── edits_0000000000000000001-0000000000000000067
│   ├── edits_0000000000000000068-0000000000000000068
│   ├── edits_inprogress_0000000000000000069
│   ├── fsimage_0000000000000000067
│   ├── fsimage_0000000000000000067.md5
│   ├── fsimage_0000000000000000068
│   ├── fsimage_0000000000000000068.md5
│   ├── seen_txid
│   └── VERSION
└── in_use.lock

datanode 目录
$ tree data/tmp/dfs/
data/tmp/dfs/
├── data
│   ├── current
│   │   ├── BP-246176607-192.168.152.102-1568989917089
│   │   │   ├── current
│   │   │   │   ├── dfsUsed
│   │   │   │   ├── finalized
│   │   │   │   │   └── subdir0
│   │   │   │   │       └── subdir0
│   │   │   │   │           ├── blk_1073741825
│   │   │   │   │           ├── blk_1073741825_1001.meta
│   │   │   │   │           ├── blk_1073741827
│   │   │   │   │           ├── blk_1073741827_1003.meta
│   │   │   │   │           ├── blk_1073741828
│   │   │   │   │           ├── blk_1073741828_1004.meta
│   │   │   │   │           ├── blk_1073741829
│   │   │   │   │           └── blk_1073741829_1005.meta
│   │   │   │   ├── rbw
│   │   │   │   └── VERSION
│   │   │   ├── scanner.cursor
│   │   │   └── tmp
│   │   └── VERSION
│   └── in_use.lock
└── name
    ├── current
    │   ├── edits_0000000000000000001-0000000000000000067
    │   ├── edits_0000000000000000068-0000000000000000068
    │   ├── edits_inprogress_0000000000000000069
    │   ├── fsimage_0000000000000000067
    │   ├── fsimage_0000000000000000067.md5
    │   ├── fsimage_0000000000000000068
    │   ├── fsimage_0000000000000000068.md5
    │   ├── seen_txid
    │   └── VERSION
    └── in_use.lock

mr 工作目录
$ tree data/tmp/mapred/
data/tmp/mapred/
├── local
│   └── localRunner
│       └── admin
│           ├── jobcache
│           │   ├── job_local2023556536_0001
│           │   │   ├── attempt_local2023556536_0001_m_000000_0
│           │   │   └── attempt_local2023556536_0001_r_000000_0
│           │   │       └── output
│           │   ├── job_local495893052_0001
│           │   │   ├── attempt_local495893052_0001_m_000000_0
│           │   │   └── attempt_local495893052_0001_r_000000_0
│           │   │       └── output
│           │   └── job_local754723910_0002
│           │       ├── attempt_local754723910_0002_m_000000_0
│           │       └── attempt_local754723910_0002_r_000000_0
│           │           └── output
│           ├── job_local2023556536_0001
│           ├── job_local495893052_0001
│           └── job_local754723910_0002
└── staging
    ├── admin1858753103
    ├── admin2023556536
    ├── admin495893052
    └── admin754723910

namenode datanode 工作日志，审计日志
$ ll logs/
----------------------------------------------
总用量 244
-rw-rw-r-- 1 admin admin  76456 9月  21 09:38 hadoop-admin-datanode-hadoop01.log
-rw-rw-r-- 1 admin admin    715 9月  21 09:20 hadoop-admin-datanode-hadoop01.out
-rw-rw-r-- 1 admin admin    715 9月  20 23:34 hadoop-admin-datanode-hadoop01.out.1
-rw-rw-r-- 1 admin admin    715 9月  20 22:49 hadoop-admin-datanode-hadoop01.out.2
-rw-rw-r-- 1 admin admin  32740 9月  20 22:34 hadoop-admin-namenode-hadoop.01.log
-rw-rw-r-- 1 admin admin 100570 9月  21 09:38 hadoop-admin-namenode-hadoop01.log
-rw-rw-r-- 1 admin admin    715 9月  20 22:34 hadoop-admin-namenode-hadoop.01.out
-rw-rw-r-- 1 admin admin    715 9月  21 09:20 hadoop-admin-namenode-hadoop01.out
-rw-rw-r-- 1 admin admin    715 9月  20 22:32 hadoop-admin-namenode-hadoop.01.out.1
-rw-rw-r-- 1 admin admin    715 9月  20 23:34 hadoop-admin-namenode-hadoop01.out.1
-rw-rw-r-- 1 admin admin   5007 9月  20 23:04 hadoop-admin-namenode-hadoop01.out.2
-rw-rw-r-- 1 admin admin      0 9月  20 22:32 SecurityAuth-admin.audit
----------------------------------------------

```

# YARN上运行 MR
```
目录
$ cd /opt/softwares/hadoop-2.7.2

hdfs停机
$ ./sbin/hadoop-daemon.sh stop datanode
$ ./sbin/hadoop-daemon.sh stop namenode

配置(在已经配好的core-site.xml hdfs-site.xml 基础上)
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
         <value>hadoop01</value>
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

删除 namenode datanode mapred 工作目录，日志(审计文件) 
$ rm -rf ../data/tmp/* logs/*

初始化
    hdfs namenode -format

启动机器
逐个启动
$ ./sbin/hadoop-daemon.sh start namenode
$ ./sbin/hadoop-daemon.sh start datanode

$ ./sbin/yarn-daemon.sh start resourcemanager
$ ./sbin/yarn-daemon.sh start nodemanager

$ jps
----------------------------------------------
7378 NameNode
9542 Jps
7468 DataNode
7567 ResourceManager
7823 NodeManager
----------------------------------------------

逐层启动
hdfs
$ ./sbin/start-dfs.sh

yarn
$ ./sbin/start-yarn.sh

一次性启动
$ ./sbin/start-all.sh
$ ./sbin/stop-all.sh

grep 测试
$ hadoop fs -mkdir -p /apps/mr/grep/in
$ hadoop fs -put ../../apps/mr/grep/in/* /apps/mr/grep/in/
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep /apps/mr/grep/in /apps/mr/grep/out 'www[a-z.]+'

wordcount 测试
$ hadoop fs -mkdir -p /apps/mr/wc/in
$ hadoop fs -put ../../apps/mr/wc/in/* /apps/mr/wc/in/  
$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar wordcount /apps/mr/wc/in /apps/mr/wc/out
    
web 页面
hdfs 
http://hadoop01:50070/
    
yarn
http://hadoop01:8088/
```



