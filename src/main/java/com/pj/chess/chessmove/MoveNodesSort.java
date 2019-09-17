package com.pj.chess.chessmove;

import com.pj.chess.BitBoard;
import com.pj.chess.movelist.MoveNodeList;

public class MoveNodesSort {
    public static final int TRANGODMOVE1 = 0, TRANGODMOVE2 = 7, KILLERMOVE1 = 1, KILLERMOVE2 = 8, OTHERALLMOVE = 2,
        EATMOVE = 3, OVER = -1, QUIESDEFAULT = -2;
    public static final int TRAN1 = 0, tran2 = 1, kill1 = 2, kill2 = 3, eatmove = 4, other = 5;
    public static int trancount1 = 0, trancount2 = 0, killcount1 = 0, killcount2 = 0, eatmovecount = 0, othercount = 0;
    public int currType;
    MoveNodeList tranGodMove;
    MoveNode[] KillerMove;
    MoveNodeList generalMoveList;
    MoveNodeList goodMoveList;
    ChessMoveAbs chessMove;
    boolean isChecked;
    MoveNodeList repeatMoveList = new MoveNodeList(4);
    BitBoard oppAttackSite;
    private int moveType, play, index;

    public MoveNodesSort(int play, MoveNodeList tranGodMove, MoveNode[] KillerMove, ChessMoveAbs chessMove,
                         boolean isChecked) {
        this.play = play;
        this.tranGodMove = tranGodMove;
        this.KillerMove = KillerMove;
        this.chessMove = chessMove;
        this.moveType = TRANGODMOVE1;
        this.isChecked = isChecked;
    }

    public MoveNodesSort(int play, ChessMoveAbs chessMove, boolean isChecked) {
        this.play = play;
        this.chessMove = chessMove;
        this.moveType = QUIESDEFAULT;
        this.isChecked = isChecked;
    }

    public MoveNode quiescNext() {
        MoveNode nextMoveNode = null;
        if (moveType == QUIESDEFAULT) {
            setMoveType(EATMOVE);

            if (index == 0) {
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                if (isChecked) {
                    //��������ȫ���߷�
                    setMoveType(OTHERALLMOVE);
                } else {
                    //�ǽ���ֻ���������ŷ����������
                    setMoveType(OVER);

                }
            }

            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
            } else {
                setMoveType(OVER);
            }

        } else if (moveType == EATMOVE) {
            if (index == 0) {
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                if (isChecked) {
                    //��������ȫ���߷�
                    setMoveType(OTHERALLMOVE);
                } else {
                    //�ǽ���ֻ���������ŷ����������
                    setMoveType(OVER);
                }
            }

            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
            } else {
                setMoveType(OVER);
            }

        } else if (moveType == OTHERALLMOVE) {
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
            } else {
                setMoveType(OVER);
            }

        }
        return nextMoveNode;
    }

    public MoveNode next() {
        MoveNode nextMoveNode = null;
        if (moveType == TRANGODMOVE1) {
            this.currType = TRAN1;
            nextMoveNode = tranGodMove.get(0);
            setMoveType(TRANGODMOVE2);
            if (chessMove.legalMove(play, nextMoveNode)) {
                trancount1++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = tran2;
            nextMoveNode = tranGodMove.get(1);
            setMoveType(KILLERMOVE1);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0))) {
                trancount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = kill1;
            nextMoveNode = KillerMove[0];
            setMoveType(KILLERMOVE2);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1))) {
                killcount1++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = kill2;
            nextMoveNode = KillerMove[1];
            setMoveType(EATMOVE);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1)) && !nextMoveNode.equals(KillerMove[0])) {
                killcount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = eatmove;
            if (index == 0) {
                oppAttackSite = chessMove.getOppAttackSite(play);
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                eatmovecount++;
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                setMoveType(OTHERALLMOVE);
            }

            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        } else if (moveType == TRANGODMOVE2) {
            this.currType = tran2;
            nextMoveNode = tranGodMove.get(1);
            setMoveType(KILLERMOVE1);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0))) {
                trancount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = kill1;
            nextMoveNode = KillerMove[0];
            setMoveType(KILLERMOVE2);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1))) {
                killcount1++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = kill2;
            nextMoveNode = KillerMove[1];
            setMoveType(EATMOVE);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1)) && !nextMoveNode.equals(KillerMove[0])) {
                killcount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = eatmove;
            if (index == 0) {
                oppAttackSite = chessMove.getOppAttackSite(play);
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                eatmovecount++;
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                setMoveType(OTHERALLMOVE);
            }

            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        } else if (moveType == KILLERMOVE1) {
            this.currType = kill1;
            nextMoveNode = KillerMove[0];
            setMoveType(KILLERMOVE2);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1))) {
                killcount1++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = kill2;
            nextMoveNode = KillerMove[1];
            setMoveType(EATMOVE);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1)) && !nextMoveNode.equals(KillerMove[0])) {
                killcount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = eatmove;
            if (index == 0) {
                oppAttackSite = chessMove.getOppAttackSite(play);
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                eatmovecount++;
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                setMoveType(OTHERALLMOVE);
            }

            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        } else if (moveType == KILLERMOVE2) {
            this.currType = kill2;
            nextMoveNode = KillerMove[1];
            setMoveType(EATMOVE);
            if (chessMove.legalMove(play, nextMoveNode) && !nextMoveNode.equals(tranGodMove.get(0)) && !nextMoveNode
                .equals(tranGodMove.get(1)) && !nextMoveNode.equals(KillerMove[0])) {
                killcount2++;
                repeatMoveList.add(nextMoveNode);
                return nextMoveNode;
            }

            this.currType = eatmove;
            if (index == 0) {
                oppAttackSite = chessMove.getOppAttackSite(play);
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                eatmovecount++;
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                setMoveType(OTHERALLMOVE);
            }

            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        } else if (moveType == EATMOVE) {
            this.currType = eatmove;
            if (index == 0) {
                oppAttackSite = chessMove.getOppAttackSite(play);
                genEatMoveList();
            }
            if (index < goodMoveList.size) {
                eatmovecount++;
                nextMoveNode = getSortAfterBestMove(goodMoveList);
                index++;
                return nextMoveNode;
            } else {
                setMoveType(OTHERALLMOVE);
            }

            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        } else if (moveType == OTHERALLMOVE) {
            this.currType = other;
            if (index == 0) {
                genNopMoveList();
            }
            if (index < generalMoveList.size) {
                othercount++;
                nextMoveNode = getSortAfterBestMove(generalMoveList);
                index++;
                return nextMoveNode;
            } else {
                moveType = OVER;
            }

        }
        return nextMoveNode;
    }

    public int getCurrTypeMoveSize() {
        if (this.currType == other) {
            return generalMoveList.size;
        } else if (this.currType == eatmove) {
            return goodMoveList.size;
        }
        return 100;
    }

    public boolean isOver() {
        return moveType == OVER;
    }

    public boolean isKillerMove() {
        return moveType == KILLERMOVE1 || moveType == KILLERMOVE2;
    }

    public int getMoveType() {
        return moveType;
    }

    private void setMoveType(int moveType) {
        this.moveType = moveType;
        this.index = 0;
    }

    private void genEatMoveList() {
        generalMoveList = new MoveNodeList(100);
        goodMoveList = new MoveNodeList(30);
        chessMove.setMoveNodeList(generalMoveList, goodMoveList, repeatMoveList, oppAttackSite);
        chessMove.genEatMoveList(play);
    }

    private void genNopMoveList() {
        chessMove.setMoveNodeList(generalMoveList, goodMoveList, repeatMoveList, oppAttackSite);
        chessMove.genNopMoveList(play);
    }

    public MoveNode getSortAfterBestMove(MoveNodeList allMoveNode) {
        int replaceIndex = index;
        for (int i = index + 1; i < allMoveNode.size; i++) {
            if (allMoveNode.get(i).score > allMoveNode.get(replaceIndex).score) {
                replaceIndex = i;
            }
        }
        if (replaceIndex != index) {
            MoveNode t = allMoveNode.get(index);
            allMoveNode.set(index, allMoveNode.get(replaceIndex));
            allMoveNode.set(replaceIndex, t);
        }
        return allMoveNode.get(index);
    }

}
	
	
	
	
	
	
	
	