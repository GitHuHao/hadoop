package com.bigdata.hadoop.filter.logclean2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestSplit {

    public static void main(String[] args) {
        String line = "true113.90.83.205-18/Sep/2013:14:01:39/series/?cf_action=sync_comments&post_id=24420048\"http://blog.fens.me/series/\"\"Mozilla/5.0 (Windows";
        String[] data = line.split("\001");
        for(String word:data){
            System.out.println(word);
        }
    }


}
