package com.iutcalendar.alarm.condition.label_constraint;

import android.content.Context;
import androidx.annotation.NonNull;
import com.calendar.iutcalendar.R;
import com.iutcalendar.calendrier.EventCalendrier;

import java.io.Serializable;
import java.util.Objects;

public class AlarmConstraintLabel implements Serializable {
    private Containing typeDeContraint;
    private String constraintString;

    public boolean matchWith(EventCalendrier event) {
        switch (getTypeDeContraint()) {
            case MUST_NOT_CONTAIN:
                if (event.getNameEvent().contains(getContraintText())) {
                    return false;
                }
                break;
            case MUST_NOT_BE_EXACTLY:
                if (event.getNameEvent().equals(getContraintText())) {
                    return false;
                }
                break;
        }
        return true;
    }

    public enum Containing implements Serializable {
        MUST_NOT_CONTAIN,
        MUST_NOT_BE_EXACTLY,
        NONE;

        public String toString(Context context) {
            switch (this) {
                case MUST_NOT_CONTAIN:
                    return context.getResources().getString(R.string.must_not_contain);
                case MUST_NOT_BE_EXACTLY:
                    return context.getResources().getString(R.string.must_not_be_exactly);
                default:
                    return this.toString();
            }
        }
    }

    public AlarmConstraintLabel(Containing typeDeContraint, String constraintString) {
        this.typeDeContraint = typeDeContraint;
        this.constraintString = constraintString;
    }


    public Containing getTypeDeContraint() {
        return typeDeContraint;
    }

    public String getContraintText() {
        return constraintString;
    }

    public void setTypeDeContraint(Containing typeDeContraint) {
        this.typeDeContraint = typeDeContraint;
    }

    public void setConstraintString(String constraintString) {
        this.constraintString = constraintString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmConstraintLabel that = (AlarmConstraintLabel) o;
        return typeDeContraint == that.typeDeContraint && Objects.equals(constraintString, that.constraintString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDeContraint, constraintString);
    }

    @NonNull
    @Override
    public String toString() {
        return typeDeContraint + " : " + constraintString;
    }
}
