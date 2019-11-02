package pa1;

/**
 * A data structure used to keep track of the url, indegree of the url, and the
 * number of occurrences while building the inverted index. These objects are the ones being
 * contained within the inverted index.
 * @author Benjamin Vogel
 */
class URLOccurrence 
{
    private String url;
    private int indegree;
    private int numOccurrences;

    /**
     * Constructs the URLOccurrence data structure. This does no more than hold the url, the indegree, and the
     * number of occurrences, but will also be used to calculate the rank.
     * @param baseURL
     * @param urlIndegree
     * @param initOccurrences
     */
    public URLOccurrence(String baseURL, int urlIndegree, int initOccurrences)
    {
        url = baseURL;
        indegree = urlIndegree;
        numOccurrences = initOccurrences;
    }

    /**
     * Returns the rank of the word in that url. The rank is calculated by multiplying the indegree of the url
     * by the number of times the word appeared in said url.
     * @return
     *      The rank of the url for the given word.
     */
    public int getRank() { return indegree * numOccurrences; }

    public String getURL() { return url; }
    
    public void setURL(String newURL) { url = newURL; }

    public int getIndegree() { return indegree; }

    public void setIndegree(int newIndegree) { indegree = newIndegree; }

    public int getNumOccurrences() { return numOccurrences; }

    public void setNumOccurrences(int occurrences) { numOccurrences = occurrences; }

    public void incrementOccurrences() { numOccurrences++; }
}