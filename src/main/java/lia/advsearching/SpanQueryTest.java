package lia.advsearching;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class SpanQueryTest extends TestCase {
  private RAMDirectory directory;
  private IndexSearcher searcher;
  private IndexReader reader;

  private SpanTermQuery quick;
  private SpanTermQuery brown;
  private SpanTermQuery red;
  private SpanTermQuery fox;
  private SpanTermQuery lazy;
  private SpanTermQuery sleepy;
  private SpanTermQuery dog;
  private SpanTermQuery cat;
  private Analyzer analyzer;

  protected void setUp() throws Exception {
    directory = new RAMDirectory();

    analyzer = new WhitespaceAnalyzer();
    IndexWriter writer = new IndexWriter(directory,
        analyzer, true);

    Document doc = new Document();
    doc.add(Field.Text("f",
        "the quick brown fox jumps over the lazy dog"));
    writer.addDocument(doc);

    doc = new Document();
    doc.add(Field.Text("f",
        "the quick red fox jumps over the sleepy cat"));
    writer.addDocument(doc);

    writer.close();

    searcher = new IndexSearcher(directory);
    reader = IndexReader.open(directory);

    quick = new SpanTermQuery(new Term("f", "quick"));
    brown = new SpanTermQuery(new Term("f", "brown"));
    red = new SpanTermQuery(new Term("f", "red"));
    fox = new SpanTermQuery(new Term("f", "fox"));
    lazy = new SpanTermQuery(new Term("f", "lazy"));
    sleepy = new SpanTermQuery(new Term("f", "sleepy"));
    dog = new SpanTermQuery(new Term("f", "dog"));
    cat = new SpanTermQuery(new Term("f", "cat"));
  }

  private void assertOnlyBrownFox(Query query)throws Exception {
    Hits hits = searcher.search(query);
    assertEquals(1, hits.length());
    assertEquals("wrong doc", 0, hits.id(0));
  }

  private void assertBothFoxes(Query query) throws Exception {
    Hits hits = searcher.search(query);
    assertEquals(2, hits.length());
  }

  private void assertNoMatches(Query query) throws Exception {
    Hits hits = searcher.search(query);
    assertEquals(0, hits.length());
  }

  public void testSpanTermQuery() throws Exception {
    assertOnlyBrownFox(brown);
    dumpSpans(brown);
  }

  public void testSpanFirstQuery() throws Exception {
    SpanFirstQuery sfq = new SpanFirstQuery(brown, 2);
    assertNoMatches(sfq);

    dumpSpans(sfq);

    sfq = new SpanFirstQuery(brown, 3);
    dumpSpans(sfq);
    assertOnlyBrownFox(sfq);
  }

  public void testSpanNearQuery() throws Exception {
    SpanQuery[] quick_brown_dog =
        new SpanQuery[]{quick, brown, dog};
    SpanNearQuery snq =
        new SpanNearQuery(quick_brown_dog, 0, true);
    assertNoMatches(snq);
    dumpSpans(snq);

    snq = new SpanNearQuery(quick_brown_dog, 4, true);
    assertNoMatches(snq);
    dumpSpans(snq);

    snq = new SpanNearQuery(quick_brown_dog, 5, true);
    assertOnlyBrownFox(snq);
    dumpSpans(snq);

    // interesting - even a sloppy phrase query would require
    // more slop to match
    snq = new SpanNearQuery(new SpanQuery[]{lazy, fox}, 3, false);
    assertOnlyBrownFox(snq);
    dumpSpans(snq);

    PhraseQuery pq = new PhraseQuery();
    pq.add(new Term("f", "lazy"));
    pq.add(new Term("f", "fox"));
    pq.setSlop(4);
    assertNoMatches(pq);

    pq.setSlop(5);
    assertOnlyBrownFox(pq);
  }

  public void testSpanNotQuery() throws Exception {
    SpanNearQuery quick_fox =
        new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
    assertBothFoxes(quick_fox);
    dumpSpans(quick_fox);

    SpanNotQuery quick_fox_dog = new SpanNotQuery(quick_fox, dog);
    assertBothFoxes(quick_fox_dog);
    dumpSpans(quick_fox_dog);

    SpanNotQuery no_quick_red_fox =
        new SpanNotQuery(quick_fox, red);
    assertOnlyBrownFox(no_quick_red_fox);
    dumpSpans(no_quick_red_fox);
  }

  public void testSpanOrQuery() throws Exception {
    SpanNearQuery quick_fox =
        new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);

    SpanNearQuery lazy_dog =
        new SpanNearQuery(new SpanQuery[]{lazy, dog}, 0, true);

    SpanNearQuery sleepy_cat =
        new SpanNearQuery(new SpanQuery[]{sleepy, cat}, 0, true);

    SpanNearQuery qf_near_ld =
        new SpanNearQuery(
            new SpanQuery[]{quick_fox, lazy_dog}, 3, true);
    assertOnlyBrownFox(qf_near_ld);
    dumpSpans(qf_near_ld);

    SpanNearQuery qf_near_sc =
        new SpanNearQuery(
            new SpanQuery[]{quick_fox, sleepy_cat}, 3, true);
    dumpSpans(qf_near_sc);

    SpanOrQuery or = new SpanOrQuery(
        new SpanQuery[]{qf_near_ld, qf_near_sc});
    assertBothFoxes(or);
    dumpSpans(or);
  }

  public void testPlay() throws Exception {
    SpanOrQuery or = new SpanOrQuery(new SpanQuery[]{quick, fox});
    dumpSpans(or);

    SpanNearQuery quick_fox =
        new SpanNearQuery(new SpanQuery[]{quick, fox}, 1, true);
    SpanFirstQuery sfq = new SpanFirstQuery(quick_fox, 4);
    dumpSpans(sfq);

    dumpSpans(new SpanTermQuery(new Term("f", "the")));

    SpanNearQuery quick_brown =
        new SpanNearQuery(new SpanQuery[]{quick, brown}, 0, false);
    dumpSpans(quick_brown);

  }

  private void dumpSpans(SpanQuery query) throws IOException {
    Spans spans = query.getSpans(reader);
    System.out.println(query + ":");
    int numSpans = 0;

    Hits hits = searcher.search(query);
    float[] scores = new float[2];
    for (int i = 0; i < hits.length(); i++) {
      scores[hits.id(i)] = hits.score(i);
    }

    while (spans.next()) {
      numSpans++;

      int id = spans.doc();
      Document doc = reader.document(id);

      // for simplicity - assume tokens are in sequential,
      // positions, starting from 0
      Token[] tokens = AnalyzerUtils.tokensFromAnalysis(
          analyzer, doc.get("f"));
      StringBuffer buffer = new StringBuffer();
      buffer.append("   ");
      for (int i = 0; i < tokens.length; i++) {
        if (i == spans.start()) {
          buffer.append("<");
        }
        buffer.append(tokens[i].termText());
        if (i + 1 == spans.end()) {
          buffer.append(">");
        }
        buffer.append(" ");
      }
      buffer.append("(" + scores[id] + ") ");
      System.out.println(buffer);
//      System.out.println(searcher.explain(query, id));
    }

    if (numSpans == 0) {
      System.out.println("   No spans");
    }
    System.out.println();
  }
}
