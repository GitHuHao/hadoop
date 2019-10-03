package com.bigdata.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestHDFS {
    @Test
    public void getFS(){
        /**
         * 获取文件系统
         *         标记HDFS用户名
         *         方案 1：VM 运行参数  -DHADOOP_USER_NAME=admin
         *         方案 2：FileSystem.get 最后一个参数直接带上"admin"
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            // VM -DHADOOP_USER_NAME=admin
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            System.out.println(fs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fs!=null){
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void uploadFile(){
        /**
         * 上传文件
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            fs.copyFromLocalFile(new Path("data/1.txt"),new Path("/apps/test"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void downloadFile(){
        /**
         * 下载文件
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            fs.copyToLocalFile(new Path("/apps/test/1.txt"),new Path("data/1_bk.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void createDir(){
        /**
         * 创建目录，设置 user group other 权限
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            // FsPermission u g o
            fs.mkdirs(new Path("/apps/test/idea"),new FsPermission(FsAction.READ_WRITE ,FsAction.READ ,FsAction.NONE ));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void listDir(){
        /**
         * fs.listStatus 查看全部文件和目录
         * fs.listFiles 只查看文件
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            FileStatus[] fileStatuses = fs.listStatus(new Path("/apps/test"));
            for (FileStatus fileStatus:fileStatuses) {
                System.out.println(fileStatus);
            }
//            RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("/apps/test"), true);
//            while(iterator.hasNext()){
//                System.out.println(iterator.next());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void delDir(){
        /**
         * 删除目录
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            String path = "/apps/test/idea";
            boolean delete = fs.delete(new Path(path), true);
            System.out.println(String.format("delete %s %s",path,delete));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void showDir(){
        /**
         * 获取扫描路径下全部信息
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(new Path("/apps/test"), true);

            while(iterator.hasNext()){
                LocatedFileStatus fileStatus = iterator.next();
                boolean isDir = fileStatus.isDirectory();
                boolean isFile = fileStatus.isFile();
                boolean isSymlink = fileStatus.isSymlink();
                boolean isEncrypted = fileStatus.isEncrypted();

                FsPermission permission = fileStatus.getPermission();
                String owner = fileStatus.getOwner();
                String group = fileStatus.getGroup();
                long accessTime = fileStatus.getAccessTime();
                long modificationTime = fileStatus.getModificationTime();
                short replication = fileStatus.getReplication();
                BlockLocation[] blockLocations = fileStatus.getBlockLocations();

                List<String> blockInfoList = new ArrayList<>();

                for(BlockLocation blockLoc:blockLocations){
                    String[] names = blockLoc.getNames();
                    long length = blockLoc.getLength();
                    long offset = blockLoc.getOffset();
                    String[] topologyPaths = blockLoc.getTopologyPaths();
                    String info = String.format("%s\toffset[%s->%s]%s",Arrays.asList(names).toString(),offset,offset+length,Arrays.asList(topologyPaths).toString());
                    blockInfoList.add(info);
                }

                long blockSize = fileStatus.getBlockSize();
                long len = fileStatus.getLen();
                Path path = fileStatus.getPath();

                System.out.println(String.format("isDir:%s,isFile:%s,isSymlink:%s,isEncrypted:%s\t %s\t%s\t%s\t%s\t%s\t%s",isDir,isFile,isSymlink,isEncrypted,permission,owner,group,accessTime,modificationTime,replication));
                System.out.println(String.format("blocks:%s\nsize:%s\nlen:%s\npath:%s",blockInfoList,blockSize,len,path));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void uploadByStream(){
        /**
         * 基于 IO 流上传
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        FSDataOutputStream fdos = null;
        FileInputStream fis = null;

        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            fdos = fs.create(new Path("/apps/test/1.txt"));
            fis = new FileInputStream(new File("data/1.txt"));
            IOUtils.copyBytes(fis,fdos,8096,false);// 不管 close与否，都需要在 finally 中执行关闭
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fis);
            IOUtils.closeStream(fdos);
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void downloadByStream(){
        /**
         * 基于 IO 流下载
         */
        Configuration conf = new Configuration();
        FSDataInputStream fdis = null;
        FileOutputStream fos = null;
        FileSystem fs = null;

        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,":admin");
            fdis = fs.open(new Path("/apps/test/1.txt"));
            fos  = new FileOutputStream(new File("data/1_bk.txt"));
            IOUtils.copyBytes(fdis,fos,8096,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fdis);
            IOUtils.closeStream(fos);
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void seekRead(){
        /**
         * 定位读取
         * 然后使用  cat hadoop-2.7.2.tar.gz.part1 hadoop-2.7.2.tar.gz.part2 >> hadoop-2.7.2.tar.gz 合并
         */
        Configuration conf = new Configuration();
        FileSystem fs = null;
        FSDataInputStream fdis = null;
        FileOutputStream fos1 = null;
        FileOutputStream fos2 = null;

        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            fdis = fs.open(new Path("/apps/test/hadoop-2.7.2.tar.gz"));
            fos1  = new FileOutputStream(new File("data/hadoop-2.7.2.tar.gz.part1"));
            fos2  = new FileOutputStream(new File("data/hadoop-2.7.2.tar.gz.part2"));

            byte[] buf = new byte[1024];

            for(int i=0;i<128*1024;i++){
                fdis.read(buf);
                fos1.write(buf);
            }
            fdis.seek(128*1024*1024);
            IOUtils.copyBytes(fdis,fos2,8096,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fdis);
            IOUtils.closeStream(fos1);
            IOUtils.closeStream(fos2);
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Test
    public void checkConsistency(){
        Configuration conf = new Configuration();
        FileSystem fs = null;
        FSDataOutputStream fdos = null;

        try {
            fs = FileSystem.get(new URI("hdfs://hadoop01:9000"),conf,"admin");
            fdos = fs.create(new Path("/apps/test/3.txt")); // 创建了空文件
            fdos.write("from java client\n".getBytes()); // 写出了数据，但尚未完成所有块咋 dn列表的同步，文件仍未空
            fdos.hflush(); // 刷写完毕，块文件在所有 dn 节点完成同步，此时数据是完整可见的，但 web 页面 block-info 不可见
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fdos); // 关闭输出流，nn 节点元数据维护完毕，此时 web 页面 block-info 可见
            if(fs!=null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }





}
