package develop.p2p.lib.TripleArray;

import java.util.*;

public class TripleArray<L, M, R>
{
    private final ArrayList<L> left;
    private final ArrayList<M> middle;
    private final ArrayList<R> right;

    public TripleArray()
    {
        this.left = new ArrayList<>();
        this.middle = new ArrayList<>();
        this.right = new ArrayList<>();
    }

    public boolean containsLeft(L l)
    {
        return this.left.contains(l);
    }

    public boolean containsMiddle(M m)
    {
        return this.middle.contains(m);
    }

    public boolean containsRight(R r)
    {
        return this.right.contains(r);
    }

    public void add(L l, M m, R r)
    {
        this.left.add(l);
        this.middle.add(m);
        this.right.add(r);
    }

    public Value<L, M, R> get(int index)
    {
        if (this.left.size() < index)
            return null;

        return new Value(this.left.get(index), this.middle.get(index), this.right.get(index));
    }
}