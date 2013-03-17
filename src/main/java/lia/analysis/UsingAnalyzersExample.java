package lia.analysis;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;

public class UsingAnalyzersExample {
    /**
     * This method doesn't do anything, except compile correctly.
     * This is used to show snippets of how Analyzers are used.
     */
    public void someMethod() throws IOException, ParseException {
        RAMDirectory directory = new RAMDirectory();

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriter writer = new IndexWriter(directory, analyzer, true);

        Document doc = new Document();
        doc.add(Field.Text("title", "This is the title"));
        doc.add(Field.UnStored("contents", "...document contents..."));
        writer.addDocument(doc);

        writer.addDocument(doc, analyzer);

        String expression = "some query";

        Query query = QueryParser.parse(expression, "contents", analyzer);

        QueryParser parser = new QueryParser("contents", analyzer);
        query = parser.parse(expression);
    }
}
