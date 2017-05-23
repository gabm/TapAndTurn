package com.gabm.tapandturn;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;

/**
 * Created by gabm on 23/05/17.
 */

public class AbsoluteOrientation {
    public enum Enum {
        Portrait,
        Reverse_Portrait,
        Landscape,
        Reverse_Landscape
    }

    public AbsoluteOrientation(Enum valEnum) {
        _enum = valEnum;
    }

    public boolean isPortrait() {
        return this.equals(Enum.Portrait);
    }

    public boolean isReversePortrait() {
        return this.equals(Enum.Reverse_Portrait);
    }

    public boolean isLandscape() {
        return this.equals(Enum.Landscape);
    }

    public boolean isReverseLandscape() {
        return this.equals(Enum.Reverse_Landscape);
    }

    public boolean equals(AbsoluteOrientation.Enum otherEnum) {
        return _enum == otherEnum;
    }

    public boolean equals(AbsoluteOrientation other) {
        return _enum == other._enum;
    }

    public int toActivityInfoOrientation() {
        if (_enum == Enum.Reverse_Landscape)
            return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;

        if (_enum == Enum.Landscape)
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        if (_enum == Enum.Reverse_Portrait)
            return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;

        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public String toString() {
        if (_enum == Enum.Portrait)
            return "Portrait";
        if (_enum == Enum.Reverse_Portrait)
            return "Reverse_Portrait";
        if (_enum == Enum.Landscape)
            return "Landscape";

        return "ReverseLandscape";
    }

    private Enum _enum;
}
