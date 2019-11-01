package pa1;

import api.TaggedVertex;

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