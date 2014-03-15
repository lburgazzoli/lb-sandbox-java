package com.github.lburgazzoli.sandbox.hft.collections.test.values;

import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.serialization.BytesMarshallable;
import net.openhft.lang.model.Byteable;
import net.openhft.lang.model.Copyable;
import net.openhft.lang.values.LongValue;

import static net.openhft.lang.Compare.calcLongHashCode;
import static net.openhft.lang.Compare.isEqual;

/**
 *
 */
public class NativeLongValue implements LongValue, BytesMarshallable, Byteable, Copyable<LongValue> {
    private static final int VALUE = 0;

    private Bytes _bytes;
    private long _offset;

    @Override
    public void setValue(long _) {
        _bytes.writeLong(_offset + VALUE, _);
    }

    @Override
    public long getValue() {
        return _bytes.readLong(_offset + VALUE);
    }

    @Override
    public long addValue(long _) {
        return _bytes.addLong(_offset + VALUE, _);
    }

    @Override
    public long addAtomicValue(long _) {
        return _bytes.addAtomicLong(_offset + VALUE, _);
    }

    @Override
    public boolean compareAndSwapValue(long _1, long _2) {
        return _bytes.compareAndSwapLong(_offset + VALUE, _1, _2);
    }

    @Override
    public void copyFrom(net.openhft.lang.values.LongValue from) {
        setValue(from.getValue());
    }

    @Override
    public void writeMarshallable(Bytes out) {
        out.writeLong(getValue());
    }

    @Override
    public void readMarshallable(Bytes in) {
        setValue(in.readLong());
    }

    @Override
    public void bytes(Bytes bytes) {
        bytes(bytes, 0L);
    }

    @Override
    public void bytes(Bytes bytes, long offset) {
        this._bytes = bytes;
        this._offset = offset;
    }

    @Override
    public Bytes bytes() {
        return _bytes;
    }

    @Override
    public int maxSize() {
        return 8;
    }

    @Override
    public int hashCode() {
        long lhc = longHashCode();
        return (int) ((lhc >>> 32) ^ lhc);
    }

    public long longHashCode() {
        return calcLongHashCode(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongValue)) return false;
        LongValue that = (LongValue) o;

        if (!isEqual(getValue(), that.getValue())) return false;
        return true;
    }

    @Override
    public String toString() {
        return "LongValue {" +
                ", value= " + getValue() + " }";
    }
}
