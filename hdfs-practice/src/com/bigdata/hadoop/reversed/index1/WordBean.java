package com.bigdata.hadoop.reversed.index1;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WordBean implements WritableComparable<WordBean> {

    private String path;
    private String word;
    private int count;

    public WordBean() {
    }

    public WordBean(String word, int count, String path) {
        this.word = word;
        this.count = count;
        this.path = path;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s",word,path,count);
    }

    public static WordBean parse(String line){
        String[] data = line.split("\t");
        String word = data[0];
        String path = data[1];
        int count = Integer.parseInt(data[2]);
        return new WordBean(word,count,path);
    }

    @Override
    public int compareTo(WordBean o) {
        int result = o.word.compareTo(word);
        return result;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(path);
        out.writeUTF(word);
        out.writeInt(count);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        path = in.readUTF();
        word = in.readUTF();
        count = in.readInt();
    }
}
