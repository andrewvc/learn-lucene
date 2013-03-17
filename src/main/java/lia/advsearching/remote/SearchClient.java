package lia.advsearching.remote;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;

import java.rmi.Naming;
import java.util.Date;
import java.util.HashMap;

public class SearchClient {
  private static HashMap searcherCache = new HashMap();

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: SearchClient <query>");
      System.exit(-1);
    }

    String word = args[0];

    // warm up the VM with several of each search
    for (int i=0; i < 5; i++) {
      search("LIA_Multi", word);
      search("LIA_Parallel", word);
    }
  }

  private static void search(String name, String word)
      throws Exception {
    TermQuery query = new TermQuery(new Term("word", word));

    MultiSearcher searcher =
        (MultiSearcher) searcherCache.get(name);

    if (searcher == null) {
      searcher =
          new MultiSearcher(new Searchable[]{lookupRemote(name)});
      searcherCache.put(name, searcher);
    }    

    long begin = new Date().getTime();
    Hits hits = searcher.search(query);
    long end = new Date().getTime();

    System.out.print("Searched " + name +
        " for '" + word + "' (" + (end - begin) + " ms): ");

    if (hits.length() == 0) {
      System.out.print("<NONE FOUND>");
    }

    for (int i = 0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      String[] values = doc.getValues("syn");
      for (int j = 0; j < values.length; j++) {
        System.out.print(values[j] + " ");
      }
    }
    System.out.println();
    System.out.println();

    // DO NOT CLOSE searcher!
  }

  private static Searchable lookupRemote(String name)
      throws Exception {
    return (Searchable) Naming.lookup("//localhost/" + name);
  }
}
