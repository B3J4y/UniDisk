package de.unidisk.common.datastructures;

/**
 * Created by dehne on 08.04.2016.
 */
public class Pair<T> {
    private T _1;
    private T _2;

    public Pair(T _1, T _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T get_1() {
        return _1;
    }

    public void set_1(T _1) {
        this._1 = _1;
    }

    public T get_2() {
        return _2;
    }

    public void set_2(T _2) {
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?> pair = (Pair<?>) o;

        if (!get_1().equals(pair.get_1())) return false;
        return get_2().equals(pair.get_2());

    }

    @Override
    public int hashCode() {
        int result = get_1().hashCode();
        result = 31 * result + get_2().hashCode();
        return result;
    }
}


