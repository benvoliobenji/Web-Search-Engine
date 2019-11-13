package pa1;

import api.TaggedVertex;

import java.util.ArrayList;

/**
 * A much more involved data structure than RankVertex. This data structure is what Crawler uses in the Graph
 * data structure, and contains much more information than is required. This structure also extends the
 * TaggedVertex of type String abstract class, where the data is the url, and the tag value is the indegree of the
 * url the vertex represents.
 * @author Benjamin Vogel
 */
class Vertex extends TaggedVertex<String>
{
    private String url;
    private int vertexNum;
    private int depth;
    private boolean isSeed = false;
    private ArrayList<Vertex> ancestors;
    private ArrayList<Vertex> children;

    /**
     * Constructs a Vertex object with a depth.
     * @param initUrl
     *      The URL associated with this vertex
     * @param initDepth
     *      The depth the vertex is in the graph
     */
    public Vertex(String initUrl, int initDepth)
    {
        super(initUrl, initDepth);
        url = initUrl;
        depth = initDepth;
        vertexNum = 0;
        ancestors = new ArrayList<Vertex>();
        children = new ArrayList<Vertex>();
    }

    /**
     * Constructs a vertex object with just the URL.
     * @param initUrl
     *      THe URL associated with this vertex
     */
    public Vertex(String initUrl)
    {
        super(initUrl, 0);
        url = initUrl;
        depth = 0;
        ancestors = new ArrayList<Vertex>();
        children = new ArrayList<Vertex>();
    }

    @Override
    public String getVertexData() { return url; }

    @Override
    public int getTagValue() {
        if (!isSeed)
        {
            return ancestors.size();
        }
        else
        {
            return ancestors.size() + 1;
        }
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public int getIndex() { return vertexNum; }

    public void setIndex(int index) { vertexNum = index; }

    public int getDepth() { return depth; }

    public void setDepth(int depth) { this.depth = depth; }

    public boolean getIsSeed() { return isSeed; }

    public void setIsSeed(boolean IsSeed) { isSeed = IsSeed; }

    public ArrayList<Vertex> getAncestors() { return ancestors; }

    public void addAncestor(Vertex newAncestor) { ancestors.add(newAncestor); }

    public ArrayList<Vertex> getChildren() { return children; }

    public void addChild(Vertex newChild) { children.add(newChild); }

    
    public boolean equals(Object object) {
        if (this == object) return true;

        if (object == null || getClass() != object.getClass()) return false;

        if (!super.equals(object)) return false;

        Vertex vertex = (Vertex) object;
        return getUrl().equals(vertex.getUrl());
    }
}