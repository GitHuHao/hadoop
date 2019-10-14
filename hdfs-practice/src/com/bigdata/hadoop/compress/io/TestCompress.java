package com.bigdata.hadoop.compress.io;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;


import java.io.*;

public class TestCompress {

    public static void main(String[] args) {
        compress("hdfs-practice/data/compress/in/data.txt","org.apache.hadoop.io.compress.BZip2Codec");
        decompress("hdfs-practice/data/compress/in/data.txt.bz2");
    }

    private static void decompress(String filename){
        /**
         * 解压缩
         * 压缩文件 -> 标准输出
         */
        // 创建配置
        Configuration conf = new Configuration();
        // 创建编解码器
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        CompressionCodec codec = factory.getCodec(new Path(filename));
        if(null==codec){
            System.out.println(String.format("Can not find codec for file %s",filename));
            return;
        }

        // 压缩输入流
        InputStream cin = null;
        // 标准输出流
        FileOutputStream out = null;

        try {
            // 基于编解码器创建压缩输入流
            cin = codec.createInputStream(new FileInputStream(filename));
            File fout = new File(String.format("%s.decodec", filename));
            // 标准输出流
            out = new FileOutputStream(fout);
            // 流对拷
            IOUtils.copyBytes(cin,out,5*1024*1024,false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(cin,out);
        }
    }

    private static void compress(String filename,String className){
        File fileIn = new File(filename);
        InputStream in = null;
        CompressionOutputStream cout = null;

        try {
            // 标准输入流
            in = new FileInputStream((fileIn));
            // 基于全类名创建解码器类
            Class<?> codecClass = Class.forName(className);
            // 创建配置
            Configuration conf = new Configuration();
            // 反射创建编解码器
            CompressionCodec codec = (CompressionCodec)ReflectionUtils.newInstance(codecClass, conf);

            // 准备压缩文件
            File fileOut = new File(String.format("%s%s",filename,codec.getDefaultExtension()));
            fileOut.delete();

            // 创建压缩文件输出流
            OutputStream out = new FileOutputStream(fileOut);
            cout = codec.createOutputStream(out);
            // 流对拷
            IOUtils.copyBytes(in,cout,5*1024*1024,false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(in,cout);
        }
    }

    private static void close(InputStream in ,OutputStream out) {
        try {
            if(in!=null){
                in.close();
            }
            if(out!=null){
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
