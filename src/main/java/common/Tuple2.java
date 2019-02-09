package common;

public class Tuple2<A, B> {

	protected A a;
    protected B b;

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A get1() {
        return this.a;
    }

    public B get2() {
        return this.b;
    }
}
