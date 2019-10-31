package pa1;

import api.TaggedVertex;

class Vertex extends TaggedVertex
{
    private String url;
    private int vertexNum;
    private int depth;
    private ArrayList<Vertex> ancestors;
    private ArrayList<Vertex> children;

    public Vertex(String initUrl, int initDepth)
    {
        super();
        url = initUrl;
        depth = initDepth;
        vertexNum = 0;
        ancestors = new ArrayList<Vertex>();
        childrent = new ArrayList<Vertex>();
    }

    public Vertex(String initUrl)
    {
        super();
        url = initUrl;
        depth = 0;
        vertexNum = 0;
        ancestors = new ArrayList<Vertex>();
        childrent = new ArrayList<Vertex>();
    }

    @Override
    public String getVertexData() { return url; }

    @Override
    public int getTagValue() { return ancestors.size(); }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public int getIndex() { return vertexNum; }

    public void setIndex(int index) { vertexNum = index; }

    public int getDepth() { return depth; }

    public void setDepth(int depth) { this.depth = depth; }

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