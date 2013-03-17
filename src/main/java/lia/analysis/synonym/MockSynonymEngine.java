package lia.analysis.synonym;

import java.util.HashMap;

public class MockSynonymEngine implements SynonymEngine {
  private static HashMap map = new HashMap();

  static {
    map.put("quick", new String[] {"fast", "speedy"});
    map.put("jumps", new String[] {"leaps", "hops"});
    map.put("over", new String[] {"above"});
    map.put("lazy", new String[] {"apathetic", "sluggish"});
    map.put("dogs", new String[] {"canines", "pooches"});
  }

  public String[] getSynonyms(String s) {
    return (String[]) map.get(s);
  }
}
