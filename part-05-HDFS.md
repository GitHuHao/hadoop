是什么？
```
 1）HDFS 是一个文件系统,用于存储文件,通过层级目录树定位文件;其次,是分布式存储的,由多台服务器联合使用,各自承担不同角色,完成系统集群功能.
 2）HDFS的设计适合一次写入,多次读出常见,有编辑文件的操作,但一般不推荐使用,因为hdfs是被定位为数据仓库,而非数据加工工厂.因此HDFS适合数据分析,不适合作网盘使用;
```

有什么？
```
Namenode: 
 1).负责管理整个文件系统的元数据,以及每一个路径(文件)对应的块信息;
 2).在hadoop-2.7.2/etc/hadoop/core-site.xml 中定义文件系统根 "fs.defaultFS"，存储目录"hadoop.tmp.dir"
 3).市面流行的服务器内存大小为128G,datanode存储的数据块block,在namenode上的注册元文件大小为150Byte左右,
 小文件推荐使用 hadoop har -HarName xzz.har -p /user/in /user/out 进行压缩,可以节省Nn空间;
 
Datenode
1).datanode负责存储数据块,每个数据块都可以在多个Dn节点上存储,保证整个文件系统有多个此数据块的副本;
2).集群初始启动,会进入安全模式safemode,大约持续30s时间,此过程Nn会加载fsimage 文件系统镜像,edit编辑日志到内存构建完整的文件系统存储信息,
以及上次停机时保存的Dn节点块的元数据信息(块长度,校验和,时间戳),随后启动的Dn节点会自动往Nn册实时块元数据信息,通过Nn比对,决定Dn节点哪些块完好,哪些块损坏不能继续使用;
3).Nn节点定期检测Dn节点心跳(默认30s),发现某个Dn节点失联时,不会立刻判断该节点失效,而是重连两次(时间间隔5min),因此判断Dn节点失联的时间间隔为2x5min + 30 s 10min30s ;
4).Dn 节点周期性对本节点的块进行校验checksum,发现某块的checksum发生更改,即判断此块失效,客户端发生编辑请求时,会被该Dn节点同步到其他可以的节点.
5).默认块副本数为 3，通过在hdfs-site.xml修改"dfs.replication"，指定副本数。
块默认长度 126

Block 块
1).上传任意大小的文件到 hdfs ,都会被拆解成为 若干数据块blocks,每个block的上限对于单节点本地文件系统为64M,分布式文件系统为128M,每个block块,在Nn上注册的元数据大小为150Byte;
2).Dn空间足够时,上传文件会在指定的Dn节点上全量备份,Dn空间不足够时,则会分拆到不同节点备份;
3).传输的每一个块,只有当内存次磁盘寻址时间占比为1%时,传输效率最高,市面默认服务器传输带宽为100M/s,磁盘寻址平均时间为10ms,
传输每个块需要的时间为10ms÷1%=1000ms=1s,此时间内传输数据量,就是块的大小 1s×100M/s =100M ,因此块大小在大于100Mb,且为2的幂次结果时,最佳选项就是128M.

SecondaryNamenode
1) SecondaryNamenode 用来监控HDFS状态的辅助后台程序,定期执行checkpoint帮助Namenode完成文件系统镜像备份fsimage;
2) 此外SecondaryNamenode 备份了与Namenode 相同的块元数据信息,当Nn意外宕机,可以通过拷贝Sn的data/tmp/dfs/secondaryname 恢复Nn的数据
3) 在hdfs-site.xml中指定"dfs.namenode.secondary.http-address"分配Sn节点，集群在未明确指明Sn节点时,默认会在Nn节点启动Sn
```

常用命令
``` 
查看帮助 hdfs dfs -help CMD 
$ hdfs dfs -help ls 
    -d 只查看目录
    -h 字节可读性
    -R 递归扫描

创建目录
$ hdfs dfs -mkdir
    -p 多层创建

本地剪切到hdfs(等效于 put)
$ hdfs dfs -moveFromLocal source ... to

hdfs剪切到本地（等效于 get）
$ hdfs dfs -moveToLocal source ... to

本地拷贝到hdfs(等效于 put)
$ hdfs dfs -copyFromLocal source ... to

hdfs拷贝到本地（等效于 get）
$ hdfs dfs -copyToLocal source ... to

本地文件追加到 hdfs 文件(文件不存在回创建)
$ hdfs dfs -appendToFile source ... to

查看文件（可同时查看多个）
$ hdfs dfs -cat file ...

查看 hdfs 文件( The allowed formats are zip and TextRecordInputStream and Avro.)
$ hdfs dfs -text file

查看最后 1 kb 内容(不支持通配)
$ hdfs dfs -tail file

修改属组
$ hdfs dfs -chgrp [-R] file

修改属主
$ hdfs dfs -chown [-R] xxx file

修改权限
$ hdfs dfs -chmod [-R] 755 file

hdfs拷贝到hdfs
$ hdfs dfs -cp source ... to

hdfs移动到hdfs
$ hdfs dfs -mv source ... to

文件系统可用空间
$ hdfs dfs -df

查看指定目录空间大小
$ hdfs dfs -du /dir 
    -s 统计汇总
    -h 字节格式化
    
统计文件个数
$ hdfs dfs -count /dir

设置文件副本数（节点退役时，如果副本数少于约定最小副本数，无法退役，需要调整副本数设置，才能完成退役）
$ hdfs dfs -setrep num file
```