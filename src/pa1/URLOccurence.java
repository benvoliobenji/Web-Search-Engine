package pa1;

class URLOccurrence 
{
    private String url;
    private int numOccurrences;

    public URLOccurrence(String baseURL, int initOccurrences)
    {
        url = baseURL;
        numOccurrences = initOccurrences;
    }

    public String getURL() { return url; }
    
    public void setURL(String newURL) { url = newURL; }

    public int getNumOccurrences() { return numOccurrences; }

    public void setNumOccurrences(int occurrences) { numOccurrences = occurrences; }

    public void incrementOccurrences() { numOccurrences++; }
}