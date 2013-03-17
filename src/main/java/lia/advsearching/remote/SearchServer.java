package lia.advsearching.remote;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.RemoteSearchable;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.MultiSearcher;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class SearchServer {
  private static final String ALPHABET =
      "abcdefghijklmnopqrstuvwxyz";

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: SearchServer <basedir>");
      System.exit(-1);
    }

    String basedir = args[0];
    Searchable[] searchables = new Searchable[ALPHABET.length()];
    for (int i = 0; i < ALPHABET.length(); i++) {
      searchables[i] = new IndexSearcher(
          new File(basedir,
              "" + ALPHABET.charAt(i)).getAbsolutePath());
    }

    LocateRegistry.createRegistry(1099);

    Searcher multiSearcher = new MultiSearcher(searchables);
    RemoteSearchable multiImpl =
        new RemoteSearchable(multiSearcher);
    Naming.rebind("//localhost/LIA_Multi", multiImpl);

    Searcher parallelSearcher =
        new ParallelMultiSearcher(searchables);
    RemoteSearchable parallelImpl =
        new RemoteSearchable(parallelSearcher);
    Naming.rebind("//localhost/LIA_Parallel", parallelImpl);

    System.out.println("Server started");
  }
}
