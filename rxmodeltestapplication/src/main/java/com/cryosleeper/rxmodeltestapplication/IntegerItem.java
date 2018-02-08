package com.cryosleeper.rxmodeltestapplication;

import com.cryosleeper.rxmodel.IRxModelItem;

/**
 *
 */

public class IntegerItem implements IRxModelItem {

    private int value;

    public IntegerItem(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getId() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return getId();
    }

    public enum IntegerCategory {
        Odd,
        Even
    }
}
