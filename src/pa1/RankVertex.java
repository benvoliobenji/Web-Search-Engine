package pa1;

import api.TaggedVertex;

/**
 * A basic class designed to hold the rank and the url of a given word.
 * This is designed to be the vertex used in the Index class methods.
 * @author Benjamin Vogel
 */
class RankVertex extends TaggedVertex<String>
{
    private String url;
    private int rank;

    public RankVertex(String newUrl, int newRank)
    {
        super(newUrl, newRank);
        url = newUrl;
        rank = newRank;
    }

    @Override
    public String getVertexData()
    {
        return url;
    }

    @Override
    public int getTagValue()
    {
        return rank;
    }

}