package com.pj.chess.movelist;

import com.pj.chess.chessmove.MoveNode;

public class MoveNodeList {
    public MoveNode[] tables = null;
    public int size = 0;

    public MoveNodeList(int length) {
        if (tables == null) {
            tables = new MoveNode[length];
        }
        size = 0;
    }

    public MoveNodeList(MoveNodeList copy) {
        //		if(copy!=null){
        //			tables=new MoveNode[copy.size];
        //		}
    }

    public void clear() {
        size = 0;
    }

    public void set(int index, MoveNode moveNode) {
        tables[index] = moveNode;
    }

    public void add(MoveNode moveNode) {
        if (moveNode != null) {
            tables[size++] = moveNode;
        }
    }

    public MoveNode get(int index) {
        if (index < size) {return tables[index];} else {return null;}
    }

    public void addAll(MoveNodeList moveNodeList) {
        if (moveNodeList != null && moveNodeList.size > 0) {
            System.arraycopy(moveNodeList.tables, 0, tables, size, moveNodeList.size);
            size += moveNodeList.size;
        }
    }
}
