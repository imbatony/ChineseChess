package com.pj.chess.chessmove;

import com.pj.chess.ChessConstant;
import com.pj.chess.chessparam.ChessParam;
import com.pj.chess.evaluate.EvaluateCompute;
import com.pj.chess.history.CHistoryHeuritic;
import com.pj.chess.zobrist.TranspositionTable;

import static com.pj.chess.ChessConstant.NOTHING;
import static com.pj.chess.ChessConstant.chessRoles;

public class ChessQuiescMove extends ChessMoveAbs {

    public ChessQuiescMove(ChessParam chessParam, TranspositionTable tranTable, EvaluateCompute evaluateCompute) {
        super(chessParam, tranTable, evaluateCompute);
    }

    /**
     * 记录下所有可走的方式
     *
     * @param srcSite
     * @param destSite
     * @param play
     */
    @Override
    public void savePlayChess(int srcSite, int destSite, int play) {
        int destChess = board[destSite];
        int srcChess = board[srcSite];
        MoveNode moveNode = null;
        if (destChess != NOTHING) {
            int destScore = 0;
            int srcScore = 0;
            destScore = EvaluateCompute.chessBaseScore[destChess] + evaluateCompute.chessAttachScore(
                chessRoles[destChess], destSite);
            //吃子
            if (destScore >= 150) {
                //要吃的柜子被对手保护
                srcScore = EvaluateCompute.chessBaseScore[srcChess] + evaluateCompute.chessAttachScore(
                    chessRoles[srcChess], srcSite);
                //按被吃棋子价值排序
                moveNode = new MoveNode(srcSite, destSite, srcChess, destChess, destScore - srcScore);
                goodMoveList.add(moveNode);
                return;
            }
        }
        //历吏表排序
        moveNode = new MoveNode(srcSite, destSite, srcChess, destChess,
            CHistoryHeuritic.cHistory[ChessConstant.chessRoles_eight[srcChess]][destSite]);
        //不吃子
        generalMoveList.add(moveNode);
    }
}
















