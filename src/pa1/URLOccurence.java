package pa1;

class URLOccurrence 
{
    private String url;
    private int indegree;
    private int numOccurrences;

    public URLOccurrence(String baseURL, int urlIndegree, int initOccurrences)
    {
        url = baseURL;
        indegree = urlIndegree;
        numOccurrences = initOccurrences;
    }

    public int getRank() { return indegree * numOccurrences; }

    public String getURL() { return url; }
    
    public void setURL(String newURL) { url = newURL; }

    public int getIndegree() { return indegree; }

    public void setIndegree(int newIndegree) { indegree = newIndegree; }

    public int getNumOccurrences() { return numOccurrences; }

    public void setNumOccurrences(int occurrences) { numOccurrences = occurrences; }

    public void incrementOccurrences() { numOccurrences++; }
}