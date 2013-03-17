package lia.analysis.positional;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class PositionalPorterStopAnalyzerTest extends TestCase {
  private static PositionalPorterStopAnalyzer porterAnalyzer =
      new PositionalPorterStopAnalyzer();

  private RAMDirectory directory;

  public void setUp() throws Exception {
    directory = new RAMDirectory();
    IndexWriter writer =
        new IndexWriter(directory, porterAnalyzer, true);

    Document doc = new Document();
    doc.add(Field.Text("contents",
        "The quick brown fox jumps over the lazy dogs"));
    writer.addDocument(doc);
    writer.close();
  }

  public void testStems() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);
    Query query = QueryParser.parse("laziness",
        "contents",
        porterAnalyzer);
    Hits hits = searcher.search(query);
    assertEquals("lazi", 1, hits.length());


    query = QueryParser.parse("\"fox jumped\"",
        "contents",
        porterAnalyzer);

    hits = searcher.search(query);
    assertEquals("jump jumps jumped jumping", 1, hits.length());
  }

  public void testExactPhrase() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);
    Query query = QueryParser.parse("\"over the lazy\"",
        "contents",
        porterAnalyzer);

    Hits hits = searcher.search(query);
    assertEquals("exact match not found!", 0, hits.length());
  }

  public void testWithSlop() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);

    QueryParser parser = new QueryParser("contents",
        porterAnalyzer);
    parser.setPhraseSlop(1);

    Query query = parser.parse("\"over the lazy\"");

    Hits hits = searcher.search(query);
    assertEquals("hole accounted for", 1, hits.length());
  }

  public static void main(String[] args) throws IOException {
    AnalyzerUtils.displayTokensWithPositions(porterAnalyzer,
        "The quick brown fox jumps over the lazy dogs");
  }
}
