package lia.analysis.codec;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;
import java.io.IOException;

public class MetaphoneAnalyzerTest extends TestCase {
  public void testFilter() throws Exception {
    Token[] tokens =
      AnalyzerUtils.tokensFromAnalysis(
                          new MetaphoneInjectionAnalyzer(), "cool cat");

    AnalyzerUtils.assertTokensEqual(tokens,
                      new String[]{ "cool", "KL", "cat", "KT" });

    assertEquals(1, tokens[0].getPositionIncrement());
    assertEquals(0, tokens[1].getPositionIncrement());
  }

  public void testKoolKat() throws Exception {
    RAMDirectory directory = new RAMDirectory();
    Analyzer analyzer = new MetaphoneReplacementAnalyzer();

    IndexWriter writer = new IndexWriter(directory, analyzer, true);

    Document doc = new Document();
    doc.add(Field.Text("contents", "cool cat"));
    writer.addDocument(doc);
    writer.close();

    IndexSearcher searcher = new IndexSearcher(directory);
    Query query = QueryParser.parse("kool kat",
                                    "contents",
                                    analyzer);

    Hits hits = searcher.search(query);

    assertEquals(1, hits.length());
    assertEquals("cool cat", hits.doc(0).get("contents"));

    searcher.close();
  }

  public static void main(String[] args) throws IOException {
    MetaphoneReplacementAnalyzer analyzer =
                                 new MetaphoneReplacementAnalyzer();
    AnalyzerUtils.displayTokens(analyzer,
                   "The quick brown fox jumped over the lazy dogs");

    System.out.println("");
    AnalyzerUtils.displayTokens(analyzer,
                   "Tha quik brown phox jumpd ovvar tha lazi dogz");
  }
}
