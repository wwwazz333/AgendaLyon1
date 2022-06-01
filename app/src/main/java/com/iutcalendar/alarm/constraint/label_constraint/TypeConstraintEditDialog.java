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
import com.iutcalendar.alarm.constraint.ConstraintAlarm;

public class TypeConstraintEditDialog extends Dialog {

    RadioGroup radioGroup;

    public TypeConstraintEditDialog(@NonNull Context context, ConstraintLabelAlarm constraintLabelAlarm, ClickForUpdateListener updateListener) {
        super(context);
        setContentView(R.layout.dialog_constraint_type_edit);

        radioGroup = findViewById(R.id.radio_groupe);

        setTextAndSelect(R.id.must_contain, ConstraintLabelAlarm.Containing.MUST_CONTAIN, constraintLabelAlarm);
        setTextAndSelect(R.id.must_not_contain, ConstraintLabelAlarm.Containing.MUST_NOT_CONTAIN, constraintLabelAlarm);
        setTextAndSelect(R.id.must_be_exactly, ConstraintLabelAlarm.Containing.MUST_BE_EXACTLY, constraintLabelAlarm);
        setTextAndSelect(R.id.must_not_be_exactly, ConstraintLabelAlarm.Containing.MUST_NOT_BE_EXACTLY, constraintLabelAlarm);


        findViewById(R.id.submitBtn).setOnClickListener(view -> {
            ConstraintLabelAlarm.Containing newType;
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.must_contain:
                    newType = ConstraintLabelAlarm.Containing.MUST_CONTAIN;
                    break;
                case R.id.must_not_contain:
                    newType = ConstraintLabelAlarm.Containing.MUST_NOT_CONTAIN;
                    break;
                case R.id.must_be_exactly:
                    newType = ConstraintLabelAlarm.Containing.MUST_BE_EXACTLY;
                    break;
                case R.id.must_not_be_exactly:
                    newType = ConstraintLabelAlarm.Containing.MUST_NOT_BE_EXACTLY;
                    break;
                default:
                    newType = ConstraintLabelAlarm.Containing.NONE;
            }
            constraintLabelAlarm.setTypeDeContraint(newType);
            updateListener.update();
            dismiss();
        });
    }

    private void setTextAndSelect(@IdRes int btn, ConstraintLabelAlarm.Containing type, ConstraintLabelAlarm constraintLabelAlarm) {
        RadioButton radio = findViewById(btn);
        radio.setText(type.toString());

        Log.d("Constraint", String.valueOf(constraintLabelAlarm.getTypeDeContraint().equals(type)));

        if (constraintLabelAlarm.getTypeDeContraint().equals(type)) {
            radio.setChecked(true);
        }
    }


}
