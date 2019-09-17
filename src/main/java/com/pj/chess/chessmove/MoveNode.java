package com.pj.chess.chessmove;

import com.pj.chess.ChessConstant;

import static com.pj.chess.ChessBoardMain.chessName;
import static com.pj.chess.ChessConstant.boardCol;
import static com.pj.chess.ChessConstant.boardRow;

public class MoveNode implements java.io.Serializable {
    public int destChess;
    public int srcChess;
    public int srcSite;
    public int destSite;
    public int score;
    public boolean isOppProtect = false;

    public MoveNode() {

    }


    public MoveNode(int srcSite, int destSite, int srcChess, int destChess, int score) {
        this.srcSite = srcSite;
        this.destSite = destSite;
        this.destChess = destChess;
        this.srcChess = srcChess;
        this.score = score;
    }

    //是否有吃子
    public boolean isEatChess() {
        return destChess != ChessConstant.NOTHING;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("\t原位置:").append(boardRow[srcSite]).append("行").append(
            boardCol[srcSite]).append("列  原棋子：").append(chessName[srcChess]).append("\t目标位置：").append(
            boardRow[destSite]).append("行  ").append(boardCol[destSite]).append("列   目标棋子：").append(
            destChess != ChessConstant.NOTHING
                ? chessName[destChess] : "无 \t");
        return sb.toString();

    }

    public boolean equals(MoveNode moveNode) {
        return moveNode != null
            &&
            (moveNode == this || (this.srcSite == moveNode.srcSite && this.destSite == moveNode.destSite));
    }
}
