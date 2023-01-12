import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize = 0;

    /**
     *
     * @param terms
     * @param weights
     * throws NullPointerException if either terms or weights are empty arrays
     * throws IllegalArgumentException if the arrays weights and terms are not of equal length
     * creates immutable instance with the given parameters
     */
    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("terms and weights are not the same length");
        }
        initialize(terms,weights);
    }


    /**
     *
     * @param prefix
     * @param k
     * @return a list of the top k words (based upon their weight) for a given prefix
     */
    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix == null) {
            throw new NullPointerException();
        }
        if (!myMap.containsKey(prefix)){
            return new ArrayList<Term>();
        }
        if (k == 0) {
            return new ArrayList<Term>();
        }
        if (k < 0 ) {
            throw new IllegalArgumentException("Illegal value of k:" + k);
        }
        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        
       List<Term> all = myMap.get(prefix);
       List<Term> list = all.subList(0, Math.min(k, all.size()));
        return list;
    }


    /**
     * intializes a hashmap that uses prefixes as keys and terms as values
     * sorts the values of of each key in reverse order by weight
     * @param terms is array of Strings for words in each Term
     * @param weights is corresponding weight for word in terms
     */
    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        for (int k = 0; k < terms.length; k++) {
            for (int i = 0; i <= Math.min(MAX_PREFIX, terms[k].length()); i++){
                String x = terms[k].substring(0,i);
                Term y = new Term(terms[k], weights[k]);
                myMap.putIfAbsent(x, new ArrayList<Term>());
                myMap.get(x).add(y);
                }
            }
        for (String key: myMap.keySet()) {
            Collections.sort(myMap.get(key),(Comparator.comparing(Term::getWeight).reversed()));
        }
        }

    /**
     * calculates the amount of storage used by myMap, by totaling both the
     * number of characters in each key plus the number of characters in each value (term)
     * and accounts for the double within each of these terms
     * @return mySize
     */
    @Override
    public int sizeInBytes() {
        if (mySize == 0) {
            for (String key : myMap.keySet()) {
                mySize +=  BYTES_PER_CHAR * key.length();
                for (Term t : myMap.get(key)) {
                    mySize += BYTES_PER_DOUBLE + BYTES_PER_CHAR * t.getWord().length();
                }
            }
        }
        return mySize;
    }
}

