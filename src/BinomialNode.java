import java.util.Random;

public class BinomialNode {
    private final int val;
    private BinomialNode child;
    private BinomialNode next;
    private BinomialNode prev;
    private int h;

    public BinomialNode(int val) {
        this.val = val;
    }

    public BinomialNode(int val, BinomialNode left, BinomialNode right) {
        this.val = val;
        this.prev = left;
        this.next = right;
    }

    public int getVal() {
        return val;
    }

    public BinomialNode getChild() {
        return child;
    }

    public BinomialNode getNext() {
        return next;
    }

    public BinomialNode getPrev() {
        return prev;
    }

    public int getH() {
        return h;
    }


}
