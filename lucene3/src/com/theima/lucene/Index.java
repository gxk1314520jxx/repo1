package com.theima.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class Index {
  

    @Test
    public void testIKAnalyzer() throws IOException {
        //创建IKAnalyzer对象
        IKAnalyzer analyzer = new IKAnalyzer();
        IKAnalyzer analyzer = new IKAnalyzer();


        //基于analyzer创建TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("IKAnalyzer", "靳雪霞荣获河南理工大学奖学金");
        //添加指引,相当于一个指针,获取关键字
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //设置偏移量,记录关键字的起始与结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针重置在列表头部
        tokenStream.reset();
        //遍历tokenStream,通过incrementToken方法判断列表是否结束
        while (tokenStream.incrementToken()) {
			  System.out.println("结束" + "我爱靳雪霞2");
			  System.out.println("结束" + "我爱靳雪霞");
             System.out.println("结束" + "我爱靳雪霞");
			  System.out.println("结束" + "我爱靳雪霞");
			  System.out.println("结束" + "我爱靳雪霞");
        }
    }


}
