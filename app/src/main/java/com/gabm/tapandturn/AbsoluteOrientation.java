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

    public AbsoluteOrientation add(RelativeOrientation.Enum relativeOrientation) {
        return new AbsoluteOrientation(add(_enum, relativeOrientation));
    }

    public RelativeOrientation diff(AbsoluteOrientation.Enum to) {
        return new RelativeOrientation(diff(_enum, to));
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

    private static RelativeOrientation.Enum diff(AbsoluteOrientation.Enum from, AbsoluteOrientation.Enum to) {
        if (from == to)
            return RelativeOrientation.Enum.None0;

        if (from == AbsoluteOrientation.Enum.Portrait) {
            if (to == AbsoluteOrientation.Enum.Landscape)
                return RelativeOrientation.Enum.Left90;

            if (to == AbsoluteOrientation.Enum.Reverse_Landscape)
                return RelativeOrientation.Enum.Right90;

            if (to == AbsoluteOrientation.Enum.Reverse_Portrait)
                return RelativeOrientation.Enum.Flip180;
        }

        if (from == AbsoluteOrientation.Enum.Reverse_Portrait) {
            if (to == AbsoluteOrientation.Enum.Reverse_Landscape)
                return RelativeOrientation.Enum.Left90;

            if (to == AbsoluteOrientation.Enum.Landscape)
                return RelativeOrientation.Enum.Right90;

            if (to == AbsoluteOrientation.Enum.Portrait)
                return RelativeOrientation.Enum.Flip180;
        }

        if (from == AbsoluteOrientation.Enum.Landscape) {
            if (to == AbsoluteOrientation.Enum.Reverse_Portrait)
                return RelativeOrientation.Enum.Left90;

            if (to == AbsoluteOrientation.Enum.Portrait)
                return RelativeOrientation.Enum.Right90;

            if (to == AbsoluteOrientation.Enum.Reverse_Landscape)
                return RelativeOrientation.Enum.Flip180;
        }

        if (from == AbsoluteOrientation.Enum.Reverse_Landscape) {
            if (to == AbsoluteOrientation.Enum.Portrait)
                return RelativeOrientation.Enum.Left90;

            if (to == AbsoluteOrientation.Enum.Reverse_Portrait)
                return RelativeOrientation.Enum.Right90;

            if (to == AbsoluteOrientation.Enum.Landscape)
                return RelativeOrientation.Enum.Flip180;
        }

        return RelativeOrientation.Enum.None0;
    }

    private static AbsoluteOrientation.Enum add(AbsoluteOrientation.Enum absoluteOrientation, RelativeOrientation.Enum relativeOrientation) {
        if (relativeOrientation == RelativeOrientation.Enum.None0)
            return absoluteOrientation;

        if (absoluteOrientation == AbsoluteOrientation.Enum.Portrait) {
            if (relativeOrientation == RelativeOrientation.Enum.Left90)
                return AbsoluteOrientation.Enum.Landscape;

            if (relativeOrientation == RelativeOrientation.Enum.Right90)
                return AbsoluteOrientation.Enum.Reverse_Landscape;

            if (relativeOrientation == RelativeOrientation.Enum.Flip180)
                return AbsoluteOrientation.Enum.Reverse_Portrait;
        }

        if (absoluteOrientation == AbsoluteOrientation.Enum.Reverse_Portrait) {
            if (relativeOrientation == RelativeOrientation.Enum.Left90)
                return AbsoluteOrientation.Enum.Reverse_Landscape;

            if (relativeOrientation == RelativeOrientation.Enum.Right90)
                return AbsoluteOrientation.Enum.Landscape;

            if (relativeOrientation == RelativeOrientation.Enum.Flip180)
                return AbsoluteOrientation.Enum.Portrait;
        }

        if (absoluteOrientation == AbsoluteOrientation.Enum.Landscape) {
            if (relativeOrientation == RelativeOrientation.Enum.Left90)
                return AbsoluteOrientation.Enum.Reverse_Portrait;

            if (relativeOrientation == RelativeOrientation.Enum.Right90)
                return AbsoluteOrientation.Enum.Portrait;

            if (relativeOrientation == RelativeOrientation.Enum.Flip180)
                return AbsoluteOrientation.Enum.Reverse_Landscape;
        }

        if (absoluteOrientation == AbsoluteOrientation.Enum.Reverse_Landscape) {
            if (relativeOrientation == RelativeOrientation.Enum.Left90)
                return AbsoluteOrientation.Enum.Portrait;

            if (relativeOrientation == RelativeOrientation.Enum.Right90)
                return AbsoluteOrientation.Enum.Reverse_Portrait;

            if (relativeOrientation == RelativeOrientation.Enum.Flip180)
                return AbsoluteOrientation.Enum.Landscape;
        }

        // this should never happen
        return AbsoluteOrientation.Enum.Landscape;
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
