package com.bigdata.hadoop.split.sequence;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;


import java.io.IOException;
import java.io.InputStream;

public class WholeRecordReader extends RecordReader<NullWritable, BytesWritable> {

    private FileSplit split;// 切片
    private Configuration conf; // 配置
    private BytesWritable value = new BytesWritable(); // 往外写数据的缓冲对象
    private boolean processed = false; // 标记是否处理完毕了（初始化，未被处理）

    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        // 初始化切片，配置
        this.split = (FileSplit) inputSplit;
        this.conf = taskAttemptContext.getConfiguration();
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if(!processed){
            // 未被处理
            // 创建字节数组作为缓冲区（缓冲长度，为切片全部长度）
            byte[] contents = new byte[(int) split.getLength()];
            // 获取切片路径，文件系统
            Path path = split.getPath();
            FileSystem fs = path.getFileSystem(conf);
            FSDataInputStream fdis = null;

            try {
                // 读取文件
                fdis = fs.open(path);
                // 读取整个切片
                IOUtils.readFully(fdis,contents,0,contents.length);
                // 序列化
                value.set(contents,0,contents.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 关闭流
                IOUtils.closeStream(fdis);
            }
            // 标记处理完毕了
            this.processed = true;
            return true;
        }
        return false;
    }

    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        // 一次性读取整个分片，没有 key
        return NullWritable.get();
    }

    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        // 输出分片内容
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        // 是否读取完毕
        return this.processed?1:0;
    }

    @Override
    public void close() throws IOException {
    }
}
