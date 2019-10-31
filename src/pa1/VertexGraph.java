package pa1;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import api.Graph;
import api.TaggedVertex;;

class VertexMap implements Graph
{
    private LinkedHashMap<String, Vertex> graph;
    private ArrayList<TaggedVertex> graphList;

    public VertexMap(HashMap<String, Vertex> newGraph)
    {
        graph = newGraph;

        Collection<Vertex> vertices = graph.values();
        ArrayList<Vertex> vertexData = new ArrayList<Vertex>(vertices);
        graphList = vertexData;
    }

    // TODO: Determine if vertexData() should be the exact same as vertexDataWithIncomingCounts()
    // It should be given the implementation of Vertex
    public ArrayList<Vertex> vertexData()
    {
        return graphList;
    }

    public ArrayList<TaggedVertex> vertexDataWithIncomingCounts()
    {
        // Since the implementation of graphList is already TaggedVertex, just return it
        return graphList;
    }

    public List<Integer> getNeighbors(int index)
    {
        ArrayList<Integer> neighborIndexList = new ArrayList<Integer>();

        Vertex ancestorVertex = graphList.get(index);
        ArrayList<Vertex> neighbors = ancestorVertex.getChildren();

        for(Vertex neighbor : neighbors)
        {
            neighborIndexList.add(neighbor.getIndex());
        }

        return neighborIndexList;
    }

    public List<Integer> getIncoming(int index)
    {
        ArrayList<Integer> incomingIndexList = new ArrayList<Integer>();

        Vertex childVertex = graphList.get(index);
        ArrayList<Vertex> incoming = childVertex.getAncestors();

        for(Vertex ancestor : ancestors)
        {
            incomingIndexList.add(ancestor.getIndex());
        }

        return incomingIndexList;
    }
}