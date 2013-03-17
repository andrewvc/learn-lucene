package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.Vector;

public class ScoreTest extends LiaTestCase {
  private Directory directory;

  public void setUp() throws Exception {
    directory = new RAMDirectory();

    super.setUp();
  }

  public void testSimple() throws Exception {
    indexSingleFieldDocs(new Field[] {Field.Text("contents", "x")});
    IndexSearcher searcher = new IndexSearcher(directory);
    searcher.setSimilarity(new SimpleSimilarity());

    Query query = new TermQuery(new Term("contents", "x"));
    Explanation explanation = searcher.explain(query, 0);
    System.out.println(explanation);

    Hits hits = searcher.search(query);
    assertEquals(1, hits.length());

    assertEquals(1F, hits.score(0), 0.0);

    searcher.close();
  }

  private void indexSingleFieldDocs(Field[] fields) throws Exception {
    IndexWriter writer = new IndexWriter(directory,
        new WhitespaceAnalyzer(), true);
    for (int i = 0; i < fields.length; i++) {
      Document doc = new Document();
      doc.add(fields[i]);
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }

  public void testWildcard() throws Exception {
    indexSingleFieldDocs(new Field[]
      { Field.Text("contents", "wild"),
        Field.Text("contents", "child"),
        Field.Text("contents", "mild"),
        Field.Text("contents", "mildew") });

    IndexSearcher searcher = new IndexSearcher(directory);
    Query query = new WildcardQuery(new Term("contents", "?ild*"));
    Hits hits = searcher.search(query);
    assertEquals("child no match", 3, hits.length());

    assertEquals("score the same", hits.score(0),
                                   hits.score(1), 0.0);
    assertEquals("score the same", hits.score(1),
                                   hits.score(2), 0.0);
  }

  public void testFuzzy() throws Exception {
    indexSingleFieldDocs(new Field[] { Field.Text("contents", "fuzzy"),
                                       Field.Text("contents", "wuzzy")
                                     });

    IndexSearcher searcher = new IndexSearcher(directory);
    Query query = new FuzzyQuery(new Term("contents", "wuzza"));
    Hits hits = searcher.search(query);
    assertEquals("both close enough", 2, hits.length());

    assertTrue("wuzzy closer than fuzzy", hits.score(0) !=  hits.score(1));
    assertEquals("wuzza bear", "wuzzy", hits.doc(0).get("contents"));
  }

  public static class SimpleSimilarity extends Similarity {
    public float lengthNorm(String field, int numTerms) {
      return 1.0f;
    }

    public float queryNorm(float sumOfSquaredWeights) {
      return 1.0f;
    }

    public float tf(float freq) {
      return freq;
    }

    public float sloppyFreq(int distance) {
      return 2.0f;
    }

    public float idf(Vector terms, Searcher searcher) {
      return 1.0f;
    }

    public float idf(int docFreq, int numDocs) {
      return 1.0f;
    }

    public float coord(int overlap, int maxOverlap) {
      return 1.0f;
    }
  }

}
