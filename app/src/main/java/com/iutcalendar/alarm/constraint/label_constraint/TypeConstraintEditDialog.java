package com.iutcalendar.alarm.constraint.label_constraint;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.calendar.iutcalendar.R;
import com.iutcalendar.alarm.ClickForUpdateListener;

public class TypeConstraintEditDialog extends Dialog {

    RadioGroup radioGroup;
    ClickForUpdateListener updateListener;

    public TypeConstraintEditDialog(@NonNull Context context, ConstraintLabelAlarm constraintLabelAlarm, ClickForUpdateListener updateListener) {
        super(context);
        setContentView(R.layout.dialog_constraint_type_edit);
        this.updateListener = updateListener;

        radioGroup = findViewById(R.id.radio_groupe);

        setTextAndSelect(R.id.must_contain, ConstraintLabelAlarm.Containing.MUST_CONTAIN, constraintLabelAlarm);
        setTextAndSelect(R.id.must_not_contain, ConstraintLabelAlarm.Containing.MUST_NOT_CONTAIN, constraintLabelAlarm);
        setTextAndSelect(R.id.must_be_exactly, ConstraintLabelAlarm.Containing.MUST_BE_EXACTLY, constraintLabelAlarm);
        setTextAndSelect(R.id.must_not_be_exactly, ConstraintLabelAlarm.Containing.MUST_NOT_BE_EXACTLY, constraintLabelAlarm);

    }

    private void setTextAndSelect(@IdRes int btn, ConstraintLabelAlarm.Containing type, ConstraintLabelAlarm constraintLabelAlarm) {
        RadioButton radio = findViewById(btn);
        radio.setText(type.toString(getContext()));
        radio.setOnClickListener(view -> {
            constraintLabelAlarm.setTypeDeContraint(type);
            updateListener.update();
            this.dismiss();
        });


        if (constraintLabelAlarm.getTypeDeContraint().equals(type)) {
            radio.setChecked(true);
        }
    }


}
