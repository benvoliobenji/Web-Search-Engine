package pa1;

class Vertex
{
    private String url;
    private int depth;
    private ArrayList<Vertex> ancestors;
    private ArrayList<Vertex> children;

    public Vertex(String initUrl, int initDepth)
    {
        url = initUrl;
        depth = initDepth;
    }

    public Vertex(String initUrl)
    {
        url = initUrl;
        depth = 0;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

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