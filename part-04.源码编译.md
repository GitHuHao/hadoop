为什么需要手动编译源码?
```
1).官网下载的 hadoop-2.7.2.tar.gz 只提供了32位版本,需要使用64位应用,发挥硬件的最佳性能就需要,重新编译.
2).自动下载官方源码编译,可以整合其他许多插件,方便自由扩展.
3).自动编译过程,编译器或根据硬件的参数情况,对程序进行优化.
```

机器环境
```
克隆虚拟机 
    基于 hadoop01 创建 maven 节点

权限
    root 身份登录

主机名
# vim /etc/hostname
----------------------------------------------
maven
----------------------------------------------

host 映射
# vim /etc/hosts
----------------------------------------------
192.168.152.105 maven
----------------------------------------------

网络
# vim /etc/sysconfig/network-scripts/ifcfg-ens33

重启
reboot
 
ssh 免密登录
# ssh-keygen -t rsa
# ssh-copy-id -i root@maven
```

Java 环境
```
java 环境
查看是否安装了原生 java 相关环境，如果安装，则卸载
    $ rpm -qa |grep java
    $ rpm -qa | grep java | xargs sudo rpm -e --nodeps
    
    $ rpm -qa |grep jdk
    $ rpm -qa | grep jdk | xargs sudo rpm -e --nodeps
    
    $ rpm -qa |grep gcj
    $ rpm -qa | grep gcj | xargs sudo rpm -e --nodeps

安装 jdk-8u144-linux-x64.tar.gz
    下载 https://pan.baidu.com/s/1qcxyMwX_2O6uPqPEggnz-Q my2f
    $ sudo tar -zxvf jdk-8u144-linux-x64.tar.gz -C /opt/softwares/

注册到环境
    $ sudo vim /etc/profile
    ----------------------------------------------
    # java
    export JAVA_HOME="/opt/softwares/jdk1.8.0_144"
    export JRE_HOME="$JAVA_HOME/jre"
    export CLASSPATH=".:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib/rt.jar"
    export PATH=$PATH:$JAVA_HOME/bin
    ----------------------------------------------
    $ source /etc/bashrc
    
    对所有 shell 脚本适用
    $ sudo vim /etc/bashrc
    ----------------------------------------------
    # java
    export JAVA_HOME="/opt/softwares/jdk1.8.0_144"
    export JRE_HOME="$JAVA_HOME/jre"
    export CLASSPATH=".:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib/rt.jar"
    export PATH=$PATH:$JAVA_HOME/bin
    ----------------------------------------------
    $ source /etc/bashrc

查看版本
    # java -version
    ----------------------------------------------
    java version "1.8.0_144"
    Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
    Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)
    ----------------------------------------------
```

Maven 环境
```
下载 apache-maven-3.0.5-bin.tar.gz
https://pan.baidu.com/s/1S6tkWqcBY8bRp50Gmo4KQg dv2c

下载 repository.tar.gz
https://pan.baidu.com/s/129kTkD2ZRIzKnMcQK9Fwbw qfp2

解压应用
# tar -zxvf apache-maven-3.0.5-bin.tar.gz -C /opt/softwares/
# cd /opt/softwares/apache-maven-3.0.5
# vim conf/settings.xml
----------------------------------------------
<!-- 仓库目录-->
<localRepository>/opt/softwares/apache-maven-3.0.5/repository</localRepository>

<!-- 加速镜像-->
<mirrors>
    <mirror>
        <id>central</id>
        <name>central maven</name>
        <url>http://central.maven.org/maven2/</url>
        <mirrorOf>central</mirrorOf>
    </mirror>
    <mirror>
         <id>alimaven</id>
         <name>aliyun maven</name>
         <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
         <mirrorOf>central</mirrorOf>
    </mirror>
<mirrors>
----------------------------------------------

解压仓库
tar -zxvf repository.tar.gz -C /opt/softwares/apache-maven-3.0.5

注册环境变量
# vim /etc/profile
----------------------------------------------
# mvn
export MAVEN_HOME="/opt/softwares/apache-maven-3.0.5"
export PATH=$PATH:$MAVEN_HOME/bin
export MAVEN_OPTS="-Xms1024m -Xmx2048m -Xss36m -XX:MaxPermSize=512m"
----------------------------------------------
# source /etc/profile

# vim /etc/bashrc
----------------------------------------------
# mvn
export MAVEN_HOME="/opt/softwares/apache-maven-3.0.5"
export PATH=$PATH:$MAVEN_HOME/bin
export MAVEN_OPTS="-Xms1024m -Xmx2048m -Xss36m -XX:MaxPermSize=512m"
----------------------------------------------
# source /etc/bashrc

mvn -version 
----------------------------------------------
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=512m; support was removed in 8.0
Apache Maven 3.0.5 (r01de14724cdef164cd33c7c8c2fe155faf9602da; 2013-02-19 21:51:28+0800)
Maven home: /opt/softwares/apache-maven-3.0.5
Java version: 1.8.0_144, vendor: Oracle Corporation
Java home: /opt/softwares/jdk1.8.0_144/jre
Default locale: zh_CN, platform encoding: UTF-8
OS name: "linux", version: "3.10.0-514.el7.x86_64", arch: "amd64", family: "unix"
----------------------------------------------
```

Ant 环境
```
下载 apache-ant-1.9.9-bin.tar.gz
https://pan.baidu.com/s/15KpYWHLGOq17IFtRzFU0jw thj5

解压应用
# tar -zxvf apache-ant-1.9.9-bin.tar.gz -C /opt/softwares/

注册环境
# vim /etc/profile
----------------------------------------------
# mvn
export ANT_HOME="/opt/softwares/apache-ant-1.9.9"
export PATH=$PATH:$ANT_HOME/bin
----------------------------------------------
# source /etc/profile

# vim /etc/bashrc
----------------------------------------------
# mvn
export ANT_HOME="/opt/softwares/apache-ant-1.9.9"
export PATH=$PATH:$ANT_HOME/bin
----------------------------------------------
# source /etc/bashrc

查看版本
# ant -version
----------------------------------------------
Apache Ant(TM) version 1.9.9 compiled on February 2 2017
----------------------------------------------
```

Protoc 编译环境
``` 
下载 protobuf-2.5.0.tar.gz
https://pan.baidu.com/s/1DMXptFRObD8axnNNGkNhOg ypbu

解压源码
# tar -zxvf protobuf-2.5.0.tar.gz -C /opt/softwares/

注册环境
# vim /etc/profile
----------------------------------------------
# protobuf
export LD_LIBRARY_PATH="/opt/softwares/protobuf-2.5.0"
export PATH=$PATH:$LD_LIBRARY_PATH
----------------------------------------------
# source /etc/profile

# vim /etc/bashrc
----------------------------------------------
# protobuf
export LD_LIBRARY_PATH="/opt/softwares/protobuf-2.5.0"
export PATH=$PATH:$LD_LIBRARY_PATH
----------------------------------------------
# source /etc/bashrc

安装protobuf 编译依赖
# yum install glibc-headers gcc-c++ make cmake openssl-devel ncurses-devel

# cd /opt/softwares/protobuf-2.5.0

配置环境
# ./configure

编译
# make

编译检查
# make check

安装
# make install

动态链接绑定
# ldconfig

查看版本
# protoc --version
----------------------------------------------
libprotoc 2.5.0
----------------------------------------------
```

Snappy 压缩插件
```
下载 snappy-1.1.3.tar.gz
https://pan.baidu.com/s/1eneXB3eg2X392HXFr0GzSg inq4

解压源码
# tar -zxvf snappy-1.1.3.tar.gz -C /opt/softwares/

snappy编译依赖
# yum install svn autoconf automake libtool cmake ncurses-devel openssl-develgcc*

# cd  /opt/softwares/snappy-1.1.3

# ./configure

# make 

# make install

查看snappy 库文件
# ls -lh /usr/local/lib |grep snappy
----------------------------------------------
-rw-r--r-- 1 root root 511K 9月  21 22:57 libsnappy.a
-rwxr-xr-x 1 root root  955 9月  21 22:57 libsnappy.la
lrwxrwxrwx 1 root root   18 9月  21 22:57 libsnappy.so -> libsnappy.so.1.3.0
lrwxrwxrwx 1 root root   18 9月  21 22:57 libsnappy.so.1 -> libsnappy.so.1.3.0
-rwxr-xr-x 1 root root 253K 9月  21 22:57 libsnappy.so.1.3.0
----------------------------------------------
```

hadoop-2.7.2 源码编译
```
下载 hadoop-2.7.2-src.tar.gz
https://pan.baidu.com/s/18Sp9O2NK-gjROQ6zibhqyg jwwt 

解压源码
# tar -zxvf hadoop-2.7.2-src.tar.gz -C /opt/downloads/

切入源码目录
# cd /opt/downloads/hadoop-2.7.2-src

编译（耗时约 30min）
# mvn clean package -DskipTests -Pdist,native -Dtar -Dsnappy.lib=/usr/local/lib -Dbundle.snappy

查看编译后 tar
# ls -l hadoop-dist/target/
----------------------------------------------
总用量 581040
drwxr-xr-x 2 root root        28 9月  21 23:07 antrun
-rw-r--r-- 1 root root      1871 9月  21 23:07 dist-layout-stitching.sh
-rw-r--r-- 1 root root       644 9月  21 23:07 dist-tar-stitching.sh
drwxr-xr-x 9 root root       149 9月  21 23:07 hadoop-2.7.2
-rw-r--r-- 1 root root 197950701 9月  21 23:07 hadoop-2.7.2.tar.gz
-rw-r--r-- 1 root root      2825 9月  21 23:07 hadoop-dist-2.7.2.jar
-rw-r--r-- 1 root root 397019397 9月  21 23:07 hadoop-dist-2.7.2-javadoc.jar
drwxr-xr-x 2 root root        51 9月  21 23:07 javadoc-bundle-options
drwxr-xr-x 2 root root        28 9月  21 23:07 maven-archiver
drwxr-xr-x 2 root root         6 9月  21 23:07 test-dir
----------------------------------------------

查看库文件
# ll hadoop-dist/target/hadoop-2.7.2/lib/native/
----------------------------------------------
总用量 5556
-rw-r--r-- 1 root root 1329092 9月  21 23:07 libhadoop.a
-rw-r--r-- 1 root root 1607256 9月  21 23:07 libhadooppipes.a
lrwxrwxrwx 1 root root      18 9月  21 23:07 libhadoop.so -> libhadoop.so.1.0.0
-rwxr-xr-x 1 root root  770464 9月  21 23:07 libhadoop.so.1.0.0
-rw-r--r-- 1 root root  475002 9月  21 23:07 libhadooputils.a
-rw-r--r-- 1 root root  433908 9月  21 23:07 libhdfs.a
lrwxrwxrwx 1 root root      16 9月  21 23:07 libhdfs.so -> libhdfs.so.0.0.0
-rwxr-xr-x 1 root root  272288 9月  21 23:07 libhdfs.so.0.0.0
-rw-r--r-- 1 root root  522288 9月  21 23:07 libsnappy.a
-rwxr-xr-x 1 root root     955 9月  21 23:07 libsnappy.la
lrwxrwxrwx 1 root root      18 9月  21 23:07 libsnappy.so -> libsnappy.so.1.3.0
lrwxrwxrwx 1 root root      18 9月  21 23:07 libsnappy.so.1 -> libsnappy.so.1.3.0
-rwxr-xr-x 1 root root  258616 9月  21 23:07 libsnappy.so.1.3.0
----------------------------------------------

备份
# mkdir hadoop-dist/target/hadoop-2.7.2.tar.gz compiled/hadoop-2.7.2_snappy.tar.gz

# cp hadoop-2.7.2_snappy.tar.gz
```

替换集群
```
权限 
admin 身份执行

备份原应用
$ xcall mv /opt/softwares/hadoop-2.7.2 /opt/softwares/hadoop-2.7.2.bk

解压
$ tar -zxvf hadoop-2.7.2_snappy.tar.gz -C /opt/softwares/

$ cd /opt/softwares/hadoop-2.7.2

复用配置
$ rm -rf etc/hadoop/{core-site.xml,hdfs-site.xml,mapred-site.xml,yarn-site.xml,slaves}
$ cp ../hadoop-2.7.2.bk/etc/hadoop/{core-site.xml,hdfs-site.xml,mapred-site.xml,yarn-site.xml,slaves} etc/hadoop

复用启动脚本
$ cp ../hadoop-2.7.2.bk/sbin/{startAll.sh,stopAll.sh} sbin

$ mkdir data/tmp

格式化
$ hdfs namenode -format

启动集群
$ ./sbin/startAll.sh

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
http://hadoop03:8088/
```

压缩测试
```
略 
```

