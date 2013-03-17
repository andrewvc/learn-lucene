package lia.analysis.synonym;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WordNetSynonymEngine implements SynonymEngine {
  RAMDirectory directory;
  IndexSearcher searcher;

  public WordNetSynonymEngine(File index) throws IOException {
    directory = new RAMDirectory(FSDirectory.getDirectory(index, false));
    searcher = new IndexSearcher(directory);
  }

  public String[] getSynonyms(String word) throws IOException {

    ArrayList synList = new ArrayList();

    Hits hits = searcher.search(new TermQuery(new Term("word", word)));

    for (int i = 0; i < hits.length(); i++) {
      Document doc = hits.doc(i);

      String[] values = doc.getValues("syn");

      for (int j = 0; j < values.length; j++) {
        synList.add(values[j]);
      }
    }

    return (String[]) synList.toArray(new String[0]);
  }
}
