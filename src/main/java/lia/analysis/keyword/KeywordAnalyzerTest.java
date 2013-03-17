package lia.analysis.keyword;

import junit.framework.TestCase;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;
import lia.analysis.keyword.KeywordAnalyzer;

public class KeywordAnalyzerTest extends TestCase {
  RAMDirectory directory;
  private IndexSearcher searcher;

  public void setUp() throws Exception {
    directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory,
                                         new SimpleAnalyzer(),
                                         true);

    Document doc = new Document();
    doc.add(Field.Keyword("partnum", "Q36"));
    doc.add(Field.Text("description", "Illidium Space Modulator"));
    writer.addDocument(doc);

    writer.close();

    searcher = new IndexSearcher(directory);
  }

  public void testTermQuery() throws Exception {
    Query query = new TermQuery(new Term("partnum", "Q36"));
    Hits hits = searcher.search(query);
    assertEquals(1, hits.length());
  }

  public void testBasicQueryParser() throws Exception {
    Query query = QueryParser.parse("partnum:Q36 AND SPACE",
                                    "description",
                                    new SimpleAnalyzer());

    Hits hits = searcher.search(query);
    assertEquals("note Q36 -> q",
               "+partnum:q +space", query.toString("description"));
    assertEquals("doc not found :(", 0, hits.length());
  }

  public void testPerFieldAnalyzer() throws Exception {
    PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
                                              new SimpleAnalyzer());
    analyzer.addAnalyzer("partnum", new KeywordAnalyzer());

    Query query = QueryParser.parse("partnum:Q36 AND SPACE",
                                    "description",
                                    analyzer);

    Hits hits = searcher.search(query);
    assertEquals("Q36 kept as-is",
              "+partnum:Q36 +space", query.toString("description"));
    assertEquals("doc found!", 1, hits.length());

  }
}
