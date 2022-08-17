package com.iutcalendar.alarm.condition.label_constraint

import android.content.Context
import com.iutcalendar.calendrier.EventCalendrier
import com.univlyon1.tools.agenda.R
import java.io.Serializable
import java.util.*

class AlarmConstraintLabel(var typeDeContraint: Containing, var contraintText: String) : Serializable {
    fun matchWith(event: EventCalendrier): Boolean {
        when (typeDeContraint) {
            Containing.MUST_NOT_CONTAIN -> if (event.nameEvent.contains(contraintText)) {
                return false
            }

            Containing.MUST_NOT_BE_EXACTLY -> if (event.nameEvent == contraintText) {
                return false
            }

        }
        return true
    }

    fun setConstraintString(constraintString: String) {
        contraintText = constraintString
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AlarmConstraintLabel
        return typeDeContraint == that.typeDeContraint && contraintText == that.contraintText
    }

    override fun hashCode(): Int {
        return Objects.hash(typeDeContraint, contraintText)
    }

    override fun toString(): String {
        return "$typeDeContraint : $contraintText"
    }

    enum class Containing : Serializable {
        MUST_NOT_CONTAIN, MUST_NOT_BE_EXACTLY;

        fun toString(context: Context?): String {
            return when (this) {
                MUST_NOT_CONTAIN -> context!!.resources.getString(R.string.must_not_contain)
                MUST_NOT_BE_EXACTLY -> context!!.resources.getString(R.string.must_not_be_exactly)
            }
        }
    }
}