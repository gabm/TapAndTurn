package com.gabm.tapandturn;

/**
 * Created by gabm on 23/05/17.
 */

public class RelativeOrientation {
    public enum Enum {
        None0,
        Left90,
        Right90,
        Flip180
    }

    public RelativeOrientation(Enum valEnum) {
        _enum = valEnum;
    }

    public String toString() {
        if (_enum == Enum.Left90)
            return "Left90";
        if (_enum == Enum.Right90)
            return "Right90";
        if (_enum == Enum.Flip180)
            return "Flip180";

        return "None0";
    }

    public boolean equals(RelativeOrientation other) {
        return _enum == other._enum;
    }


    private Enum _enum;
}
