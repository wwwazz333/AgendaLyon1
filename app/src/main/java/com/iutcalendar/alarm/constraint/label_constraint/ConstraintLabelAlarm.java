package com.iutcalendar.alarm.constraint.label_constraint;

import java.io.Serializable;

public class ConstraintLabelAlarm implements Serializable {
    private Containing typeDeContraint;
    private String contraintRegex;

    public enum Containing implements Serializable {
        MUST_CONTAIN,
        MUST_NOT_CONTAIN,
        MUST_BE_EXACTLY,
        MUST_NOT_BE_EXACTLY,
        NONE
    }
    public ConstraintLabelAlarm(Containing typeDeContraint, String contraintRegex) {
        this.typeDeContraint = typeDeContraint;
        this.contraintRegex = contraintRegex;
    }


    public Containing getTypeDeContraint() {
        return typeDeContraint;
    }

    public String getContraintRegex() {
        return contraintRegex;
    }
}
