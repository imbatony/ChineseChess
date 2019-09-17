package com.pj.chess;

import com.pj.chess.chessmove.MoveNode;
import com.pj.chess.chessparam.ChessParam;
import com.pj.chess.evaluate.EvaluateCompute;
import com.pj.chess.evaluate.EvaluateComputeEndGame;
import com.pj.chess.evaluate.EvaluateComputeMiddleGame;
import com.pj.chess.history.CHistoryHeuritic;
import com.pj.chess.searchengine.PrincipalVariation;
import com.pj.chess.searchengine.SearchEngine;
import com.pj.chess.zobrist.TranspositionTable;

import java.util.Timer;
import java.util.TimerTask;

import static com.pj.chess.ChessConstant.*;

/**
 * @author pengjiu 调度类，选择合适 的AI引擎,评估函数
 */
public class AICoreHandler {
    private static final int MDFSEARCHENGINETYPE = 1;
    private static final int PRINCIPALVARIATIONTYPE = 2;
    //中局
    private static final int MIDDLE_GAME = 1;
    //残局
    private static final int END_GAME = 2;
    private static int mtdfV = -90;
    NodeLink moveHistory;
    private SearchEngine seEngine = null;
    private ChessParam chessParam;
    private int depth;
    private Timer timerMonitoring;
    private long time;

    public void run() {
        run(false);
    }

    public void run(boolean isGuess) {
        TranspositionTable.setDefaultHashSize();
        long beginTime = System.currentTimeMillis();
        moveBegin();
        mtdfV = seEngine.searchMove(-maxScore, maxScore, depth);
        //如果是猜测着法 置换表和历史表 对于下次搜索有利不清除
        if (!isGuess) {
            moveEnd();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(" 耗时：" + (endTime - beginTime) + "毫秒\t 分数:" + mtdfV + "\t叶子节点：" + seEngine.count);
        if (timerMonitoring != null) {timerMonitoring.cancel();}

    }

    public void setLocalVariable(ComputerLevel cLevel, ChessParam chessParam, NodeLink moveHistory) {
        this.depth = cLevel.depth;
        this.time = cLevel.time;
        this.moveHistory = moveHistory;
        this.chessParam = new ChessParam(chessParam);
        seEngine = searchEngineFactory(PRINCIPALVARIATIONTYPE);
    }

    public void guessRun(MoveNode guessMoveNode) {
        seEngine.chessMove.moveOperate(guessMoveNode);
        this.run(true);
        seEngine.chessMove.unMoveOperate(guessMoveNode);
    }

    public SearchEngine searchEngineFactory(int type) {
        SearchEngine se = null;
        EvaluateCompute evaluateCompute = null;
        int phase = getPhase();
        if (phase == MIDDLE_GAME) {
            evaluateCompute = new EvaluateComputeMiddleGame(chessParam);
        } else if (phase == END_GAME) {
            evaluateCompute = new EvaluateComputeEndGame(chessParam);
            depth++; //残局多搜索一层
        }
        se = new PrincipalVariation(chessParam, evaluateCompute, new TranspositionTable(), moveHistory);
        return se;
    }

    /*
     * 局势(中局，残局)
     */
    private int getPhase() {
        int redChessNum = 0, blackChessNum = 0;
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.CHARIOT);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.KNIGHT);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.GUN);
        redChessNum += chessParam.getChessesNum(REDPLAYSIGN, ChessConstant.SOLDIER) > 3 ? 1 : 0;

        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.CHARIOT);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.KNIGHT);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.GUN);
        blackChessNum += chessParam.getChessesNum(BLACKPLAYSIGN, ChessConstant.SOLDIER) > 3 ? 1 : 0;

        if ((redChessNum + blackChessNum) < 7) {
            return END_GAME;
        } else {
            return MIDDLE_GAME;
        }

    }

    public void moveBegin() {
        //卒随着攻击子力的减少他的价值上升
        EvaluateCompute.chessBaseScore[27] = EvaluateCompute.chessBaseScore[28] = EvaluateCompute.chessBaseScore[29] =
            EvaluateCompute.chessBaseScore[30] = EvaluateCompute.chessBaseScore[31] = (EvaluateCompute.SOLDIERSCORE
                + (11 - chessParam.getAttackChessesNum(BLACKPLAYSIGN)) * 8);
        EvaluateCompute.chessBaseScore[43] = EvaluateCompute.chessBaseScore[44] = EvaluateCompute.chessBaseScore[45] =
            EvaluateCompute.chessBaseScore[46] = EvaluateCompute.chessBaseScore[47] = (EvaluateCompute.SOLDIERSCORE
                + (11 - chessParam.getAttackChessesNum(REDPLAYSIGN)) * 8);

        /****当棋子量少时马的价值提升*****/
        EvaluateCompute.chessBaseScore[36] = EvaluateCompute.chessBaseScore[35] = EvaluateCompute.chessBaseScore[20] =
            EvaluateCompute.chessBaseScore[19] = (EvaluateCompute.KNIGHTSCORE
                + (32 - chessParam.getAllChessesNum()) * 6);
        //炮的价值下降
        EvaluateCompute.chessBaseScore[21] = EvaluateCompute.chessBaseScore[22] = EvaluateCompute.chessBaseScore[37] =
            EvaluateCompute.chessBaseScore[38] = (EvaluateCompute.GUNSCORE - (32 - chessParam.getAllChessesNum()) * 6);

    }

    public void moveEnd() {
        for (int i = 0; i < CHistoryHeuritic.cHistory.length; i++) {
            for (int j = 0; j < CHistoryHeuritic.cHistory[i].length; j++) {
                CHistoryHeuritic.cHistory[i][j] /= 512;
            }
        }
        //设置置换表过期
        TranspositionTable.cleanTranZobrist();
    }

    public void setStop() {
        if (seEngine != null) {
            seEngine.isStop = true;
        }
        //停止时间监控
        if (timerMonitoring != null) {
            timerMonitoring.cancel();
        }
    }

    public void launchTimer() {
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                setStop();
            }
        };
        timerMonitoring = new Timer();
        timerMonitoring.schedule(myTask, time);
    }
}












