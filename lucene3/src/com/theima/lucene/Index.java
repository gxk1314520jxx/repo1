package com.theima.lucene;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

import java.io.IOException;

public class Index {
  

    @Test
    public void testIKAnalyzer() throws IOException {
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //设置偏移量,记录关键字的起始与结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针重置在列表头部
        tokenStream.reset();
        //遍历tokenStream,通过incrementToken方法判断列表是否结束
        while (tokenStream.incrementToken()) {
            System.out.println("测试了");
        }
    }


}
