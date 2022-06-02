package com.iutcalendar.alarm.constraint.label_constraint;

import android.content.Context;
import androidx.annotation.NonNull;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.constraint.AlarmCondtion;

import java.io.Serializable;
import java.util.Objects;

public class ConstraintLabelAlarm implements Serializable {
    private Containing typeDeContraint;
    private String contraintRegex;
    private AlarmCondtion parent;

    public enum Containing implements Serializable {
        MUST_CONTAIN,
        MUST_NOT_CONTAIN,
        MUST_BE_EXACTLY,
        MUST_NOT_BE_EXACTLY,
        NONE;



        public String toString(Context context){
            switch (this){
                case MUST_CONTAIN:
                    return context.getResources().getString(R.string.must_contain);
                case MUST_NOT_CONTAIN:
                    return context.getResources().getString(R.string.must_not_contain);
                case MUST_BE_EXACTLY:
                    return context.getResources().getString(R.string.must_be_exactly);
                case MUST_NOT_BE_EXACTLY:
                    return context.getResources().getString(R.string.must_not_be_exactly);
                default:
                    return this.toString();
            }
        }
    }
    public ConstraintLabelAlarm(AlarmCondtion parent, Containing typeDeContraint, String contraintRegex) {
        this.typeDeContraint = typeDeContraint;
        this.contraintRegex = contraintRegex;
        this.parent = parent;
    }


    public Containing getTypeDeContraint() {
        return typeDeContraint;
    }

    public String getContraintRegex() {
        return contraintRegex;
    }

    public void setTypeDeContraint(Containing typeDeContraint) {
        this.typeDeContraint = typeDeContraint;
    }

    public void setContraintRegex(String contraintRegex) {
        this.contraintRegex = contraintRegex;
    }

    public void remove(){
        parent.removeConstraint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintLabelAlarm that = (ConstraintLabelAlarm) o;
        return typeDeContraint == that.typeDeContraint && Objects.equals(contraintRegex, that.contraintRegex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeDeContraint, contraintRegex);
    }
}
