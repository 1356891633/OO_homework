package expr;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public interface Factor {
    LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> getTerms();
}
