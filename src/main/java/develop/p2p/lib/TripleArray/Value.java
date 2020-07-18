package develop.p2p.lib.TripleArray;

public class Value<L, M, R>
{
    L l;
    M m;
    R r;

    public Value(L l, M m, R r)
    {
        this.l = l;
        this.m = m;
        this.r = r;
    }

    public L getLeft()
    {
        return this.l;
    }

    public M getMiddle()
    {
        return this.m;
    }

    public R getRight()
    {
        return this.r;
    }
}