package lia.extsearch.filters;

public class MockSpecialsAccessor implements SpecialsAccessor {
  private String[] isbns;

  public MockSpecialsAccessor(String[] isbns) {
    this.isbns = isbns;
  }

  public String[] isbns() {
    return isbns;
  }
}
