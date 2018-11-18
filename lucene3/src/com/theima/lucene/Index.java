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
    //1.创建一个Director对象,指定索引库保存的位置
    //2.基于Directory对象 分词器 创建一个IndexWriter对象
    //3.读取磁盘上的文件,每一个文件创建一个文档对象
    //4.向文档对象添加域
    //5.把文档对象写入索引库
    //5.关闭indexwriter对象
    @Test
    public void createIndex() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        //2.读取磁盘上的文件,每个文件创建一个文档对象
        File dir = new File("D:\\008 A0.lucene2018\\02.参考资料\\searchsource");
        for (File f : dir.listFiles()) {
            //2.1获取每一个文件的文件名,路径,内容,大小
            String filePath = f.getPath();
            String fileName = f.getName();
            long fileSize = FileUtils.sizeOf(f);
            String fileContent = FileUtils.readFileToString(f, "utf-8");
            //2.2创建域
            TextField fileNameField = new TextField("fileNameField", fileName, Field.Store.YES);
            TextField filePathField = new TextField("filePathField", filePath, Field.Store.YES);
            TextField fileSizeField = new TextField("fileSizeField", fileSize + "", Field.Store.YES);
            TextField fileContentField = new TextField("fileContentField", fileContent, Field.Store.YES);
            //2.3创建文档对象,把每一个文件对应的域加入文档对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(filePathField);
            document.add(fileSizeField);
            document.add(fileContentField);
            //3.将文档对象加入索引库
            indexWriter.addDocument(document);
        }
        //4.关闭索引库
        indexWriter.close();
    }

    @Test
    public void createSearch3() throws IOException {
        //创建indexSearcher对象
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建termQuery对象
        TermQuery query = new TermQuery(new Term("fileContentField", "apache"));
        TopDocs topDocs = indexSearcher.search(query, 5);
        System.out.println("查询出的条数:" + topDocs.totalHits);
        //获取docId,根据docId查询出document对象
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            //输出打印document对象名字域内的内容
            System.out.println(document.getField("fileContentField"));
			 
        }
    }
    @Test
    public void testTokenStream() throws IOException {
        //创建一个标准分析器对象
        StandardAnalyzer analyzer = new StandardAnalyzer();
        //获取tokenStream对象(里边包含了分词信息)
        TokenStream tokenStream = analyzer.tokenStream("test", "The spring Framework provides a comprehensive programming and configuration model");
        //添加一个引用,可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //添加一个偏移量的引用,记录了关键词的开始位置以及结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表,通过increamentToken方法判断列表是否结束
        while (tokenStream.incrementToken()) {
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //取关键词
            System.out.println(charTermAttribute);
            //结束位置
            System.out.println("end->" + offsetAttribute.endOffset());
            System.out.println("------------------------");
        }
        //关闭tokenStream对象
        tokenStream.close();

    }

    @Test
    public void testTokenStream2() throws IOException {
        //创建一个标准分析器
        StandardAnalyzer standarAnalyzer = new StandardAnalyzer();
        //获取TokenStream对象
        TokenStream tokenStream = standarAnalyzer.tokenStream("myTest", "靳雪霞是一个好同学");
        //添加一个引用,获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //添加一个偏移量的引用,记录关键词的开始位置,结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表头部
        tokenStream.reset();
        //遍历关键词列表,通过incrementToken方法判断方法是否结束
        while (tokenStream.incrementToken()) {
            System.out.println(offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println(offsetAttribute.endOffset());
            System.out.println("-------------------------------");
        }
    }

    /**
     * 标准分析器的分词效果
     *
     * @throws IOException
     */

    @Test
    public void TestTokenStream() throws IOException {
        //创建标准分析器对象
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        //基于分析器对象创建TokenStream
        TokenStream tokenStream = standardAnalyzer.tokenStream("myTest", "靳雪霞荣获河南理工大奖学金");
        //设置引用,获取关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //设置偏移量,记录关键词的起始与结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表头部
        tokenStream.reset();
        //遍历tokenStream,incrementToken方法判断列表是否结束
        while (tokenStream.incrementToken()) {
            System.out.println(offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println(offsetAttribute.endOffset());

        }
    }

    @Test
    public void testIKAnalyzer() throws IOException {
        //创建IKAnalyzer对象
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
            System.out.println(offsetAttribute.startOffset());
            //控制台打印出关键词
            System.out.println(charTermAttribute);
            System.out.println(offsetAttribute.endOffset());
            System.out.println("结束" + "-------------------------------");
        }
    }

    //索引库的维护
    @Test
    public void createIndex4() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        //2.添加文档对象
        Document document = new Document();
        //3.创建域
        StringField fileNameField = new StringField("fileNameField", "靳雪霞是个好孩子", Field.Store.YES);
        LongPoint fileSizeField = new LongPoint("fileSizeField", 1000L);
        StoredField fileSizeField2 = new StoredField("fileSizeField", 1000L);
        document.add(fileNameField);
        document.add(fileSizeField);
        document.add(fileSizeField2);
        indexWriter.addDocument(document);
        //关闭indexWriter
        indexWriter.close();
    }

    @Test
    public void createSearch4() throws IOException {
        //创建indexSearcher对象
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建termQuery对象
        TermQuery query = new TermQuery(new Term("fileSizeField", "1000L"));
        TopDocs topDocs = indexSearcher.search(query, 5);
        System.out.println("查询出的条数:" + topDocs.totalHits);
        //获取docId,根据docId查询出document对象
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            //输出打印document对象名字域内的内容
            System.out.println(document.getField("fileContentField"));
        }
    }

    //索引库删除
    @Test
    public void deleteAllIndex() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        indexWriter.deleteAll();
        indexWriter.close();
    }

    //根据查询条件删除
    public void deleteIndexByQuery() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        //2.根据查询条件删除
        Query query = new TermQuery(new Term("fileNameField", "apache"));
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    //索引库的修改原理是先删除,后修改
    @Test
    public void updateIndex() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        Document document = new Document();
        document.add(new TextField("updateField", "更新文档1", Field.Store.YES));
//        indexWriter.addDocument(document);
        indexWriter.updateDocument(new Term("fileNameField"), document);
        indexWriter.close();
    }

    //索引库查询
    //TermQuery .通过项查询,TermQuery不使用分析器 所以建议匹配不分词的Field域查询(比如:StringField,比如订单号,分类id号等)
    public void testTermQuery() throws IOException {
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询对象
        TermQuery query = new TermQuery(new Term("order", "0001"));
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
        }
    }

    //创建索引
    @Test
    public void createIndex5() throws Exception {
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig()
        );
        Document document = new Document();
        StringField pathField = new StringField("path", "D:\\temp\\index", Field.Store.YES);
        document.add(pathField);
        indexWriter.addDocument(document);
        indexWriter.close();
    }

    //索引库查询
    //TermQuery .通过项查询,TermQuery不使用分析器 所以建议匹配不分词的Field域查询(比如:StringField,比如订单号,分类id号等)
    @Test
    public void testTermQuery1() throws IOException {
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建查询对象
        TermQuery query = new TermQuery(new Term("path", "D:\\temp\\index"));
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println( document.getField("path"));
        }
    }
    @Test
    public void createIndex6() throws IOException {
        //1.创建indexWriter对象
        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(new File("D:\\temp\\index").toPath()),
                new IndexWriterConfig(new IKAnalyzer())
        );
        //2.创建document对象
        Document document = new Document();
        //3.创建域
        LongPoint longpoint = new LongPoint("longPoint", 10000L);
        StoredField storedField= new StoredField("longPoint", 10000L);
        document.add(storedField);
        document.add(longpoint);
        indexWriter.addDocument(document);
        indexWriter.close();
    }
    @Test
    public void testRangeQuery() throws IOException {
        Directory directory = FSDirectory.open(new File("D:\\temp\\index").toPath());
        IndexReader indexReader= DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = LongPoint.newRangeQuery("longPoint", 0L, 10000L);
        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 15);
        System.out.println(topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document docment = indexSearcher.doc(scoreDoc.doc);
            System.out.println(docment.getField("longPoint"));
        }
        indexReader.close();
    }






    @Test
    public void testQueryParser() throws IOException, ParseException {
        //1.创建indexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("D:\\temp\\index").toPath())));
        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
        Query query = queryParser.parse("靳雪霞是一个好同学");
        //3.执行查询
        TopDocs topDocs = indexSearcher.search(query, 15);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.getField("content"));
        }
        indexSearcher.getIndexReader().close();
    }

    @Test
    public void createIndex7() {
        //1.创建indexWriter对象
//        IndexWriter indexWriter = new IndexWriter(
//
//        );
    }
}
