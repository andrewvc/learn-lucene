package lia.extsearch.sorting;

import org.apache.lucene.search.SortComparatorSource;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.ScoreDocComparator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

import java.io.IOException;

public class DistanceComparatorSource
    implements SortComparatorSource {
  private int x;
  private int y;

  public DistanceComparatorSource(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public ScoreDocComparator newComparator(
      IndexReader reader, String fieldname) throws IOException {
    return new DistanceScoreDocLookupComparator(
        reader, fieldname, x, y);
  }

  private static class DistanceScoreDocLookupComparator
      implements ScoreDocComparator {
    private float[] distances;

    public DistanceScoreDocLookupComparator(IndexReader reader,
               String fieldname, int x, int y) throws IOException {

      final TermEnum enumerator =
          reader.terms(new Term(fieldname, ""));
      distances = new float[reader.maxDoc()];
      if (distances.length > 0) {
        TermDocs termDocs = reader.termDocs();
        try {
          if (enumerator.term() == null) {
            throw new RuntimeException(
                "no terms in field " + fieldname);
          }
          do {
            Term term = enumerator.term();
            if (term.field() != fieldname) break;
            termDocs.seek(enumerator);
            while (termDocs.next()) {
              String[] xy = term.text().split(",");
              int deltax = Integer.parseInt(xy[0]) - x;
              int deltay = Integer.parseInt(xy[1]) - y;

              distances[termDocs.doc()] = (float) Math.sqrt(
                  deltax * deltax + deltay * deltay);
            }
          } while (enumerator.next());
        } finally {
          termDocs.close();
        }
      }
    }

    public int compare(ScoreDoc i, ScoreDoc j) {
      if (distances[i.doc] < distances[j.doc]) return -1;
      if (distances[i.doc] > distances[j.doc]) return 1;
      return 0;
    }

    public Comparable sortValue(ScoreDoc i) {
      return new Float(distances[i.doc]);
    }

    public int sortType() {
      return SortField.FLOAT;
    }
  }

  public String toString() {
    return "Distance from ("+x+","+y+")";
  }

}

