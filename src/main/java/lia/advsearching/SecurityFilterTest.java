package lia.advsearching;

import junit.framework.TestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;

public class SecurityFilterTest extends TestCase {
  private RAMDirectory directory;

  protected void setUp() throws Exception {
    directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory,
        new WhitespaceAnalyzer(), true);

    // Elwood
    Document document = new Document();
    document.add(Field.Keyword("owner", "elwood"));
    document.add(Field.Text("keywords", "elwoods sensitive info"));
    writer.addDocument(document);

    // Jake
    document = new Document();
    document.add(Field.Keyword("owner", "jake"));
    document.add(Field.Text("keywords", "jakes sensitive info"));
    writer.addDocument(document);

    writer.close();
  }

  public void testSecurityFilter() throws Exception {
    TermQuery query = new TermQuery(new Term("keywords", "info"));

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(query);
    assertEquals("Both documents match", 2, hits.length());

    QueryFilter jakeFilter = new QueryFilter(
        new TermQuery(new Term("owner", "jake")));

    hits = searcher.search(query, jakeFilter);
    assertEquals(1, hits.length());
    assertEquals("elwood is safe",
        "jakes sensitive info", hits.doc(0).get("keywords"));
  }

}
