package lia.extsearch.filters;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;

import java.io.IOException;
import java.util.BitSet;

import lia.extsearch.filters.SpecialsAccessor;

public class SpecialsFilter extends Filter {
  private SpecialsAccessor accessor;

  public SpecialsFilter(SpecialsAccessor accessor) {
    this.accessor = accessor;
  }

  public BitSet bits(IndexReader reader) throws IOException {
    BitSet bits = new BitSet(reader.maxDoc());

    String[] isbns = accessor.isbns();

    int[] docs = new int[1];
    int[] freqs = new int[1];

    for (int i = 0; i < isbns.length; i++) {
      String isbn = isbns[i];
      if (isbn != null) {
        TermDocs termDocs =
            reader.termDocs(new Term("isbn", isbn));
        int count = termDocs.read(docs, freqs);
        if (count == 1) {
          bits.set(docs[0]);

        }
      }
    }

    return bits;
  }

  public String toString() {
    return "SpecialsFilter";
  }
}
