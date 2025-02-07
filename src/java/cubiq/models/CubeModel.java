package cubiq.models;

import java.util.List;
import java.util.Observable;

public class CubeModel extends Observable {

    private List<int[]> edgePieces, cornerPieces;
   // private String solveString = "R2,D,B,R',B2,D,R,U,F',L',U',F2,D,R2,D2,B2,L2,F2,U',L2";


    public List<int[]> getEdgePieces() {
        return edgePieces;
    }

    public void setEdgePieces(List<int[]> edgePieces) {
        this.edgePieces = edgePieces;
    }

    public List<int[]> getCornerPieces() {
        return cornerPieces;
    }

    public void setCornerPieces(List<int[]> cornerPieces) {
        this.cornerPieces = cornerPieces;
    }



}

