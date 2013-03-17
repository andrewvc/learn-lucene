package lia.advsearching;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import junit.framework.TestCase;

public class MultiSearcherTest extends TestCase {
  private IndexSearcher[] searchers;

  public void setUp() throws Exception {
    String[] animals = { "aardvark", "beaver", "coati",
                       "dog", "elephant", "frog", "gila monster",
                       "horse", "iguana", "javelina", "kangaroo",
                       "lemur", "moose", "nematode", "orca",
                       "python", "quokka", "rat", "scorpion",
                       "tarantula", "uromastyx", "vicuna",
                       "walrus", "xiphias", "yak", "zebra"};

    Analyzer analyzer = new WhitespaceAnalyzer();

    Directory aTOmDirectory = new RAMDirectory();
    Directory nTOzDirectory = new RAMDirectory();

    IndexWriter aTOmWriter = new IndexWriter(aTOmDirectory,
                                            analyzer, true);
    IndexWriter nTOzWriter = new IndexWriter(nTOzDirectory,
                                            analyzer, true);

    for (int i=0; i < animals.length; i++) {
      Document doc = new Document();
      String animal = animals[i];
      doc.add(Field.Keyword("animal", animal));
      if (animal.compareToIgnoreCase("n") < 0) {
        aTOmWriter.addDocument(doc);
      } else {
        nTOzWriter.addDocument(doc);
      }
    }

    aTOmWriter.close();
    nTOzWriter.close();

    searchers = new IndexSearcher[2];
    searchers[0] = new IndexSearcher(aTOmDirectory);
    searchers[1] = new IndexSearcher(nTOzDirectory);
  }

  public void testMulti() throws Exception {

    MultiSearcher searcher = new MultiSearcher(searchers);

    // range spans documents across both indexes
    Query query = new RangeQuery(new Term("animal", "h"),
                                 new Term("animal", "t"), true);

    Hits hits = searcher.search(query);
    assertEquals("tarantula not included", 12, hits.length());
  }
}
