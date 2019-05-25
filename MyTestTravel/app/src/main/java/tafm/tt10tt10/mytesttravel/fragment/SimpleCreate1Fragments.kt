package tafm.tt10tt10.mytesttravel.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import android.widget.TimePicker
import org.jetbrains.anko.toast
import java.util.*

class DatePickerFragment :DialogFragment(), DatePickerDialog.OnDateSetListener{

    interface OnDateSelectedListener{
        fun onSelected(year: Int, month: Int, date: Int)
    }

    private lateinit var listener: OnDateSelectedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnDateSelectedListener){
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)
        val context = context
        if (context is Context){
            return DatePickerDialog(context, this, year, month, date)
        }
        return onCreateDialog(savedInstanceState)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, date: Int) {
        listener.onSelected(year, month, date)
    }
}

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener{

    interface OnTimeSelectedListener{
        fun onSelected(hourOfDay: Int, minute: Int)
    }

    private lateinit var listener : OnTimeSelectedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnTimeSelectedListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(context, this, hourOfDay, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.onSelected(hourOfDay, minute)
    }
}

class AddAlertDialogs :DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(context).apply {
            setMessage("7ケ所以上にはできません。")
            setPositiveButton("OK"){
                _, _ -> context.toast("OKをクリックしました。")
            }
        }
        return builder.create()
    }
}

class RemoveAlertDialogs :DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(context).apply {
            setMessage("0ケ所にはできません。")
            setPositiveButton("OK"){
                    _, _ -> context.toast("OKをクリックしました。")
            }
        }
        return builder.create()
    }
}

class DateDiffAlertDialogs :DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(context).apply {
            setMessage("1～7日間で設定してください。")
            setPositiveButton("OK"){
                    _, _ -> context.toast("OKをクリックしました。")
            }
        }
        return builder.create()
    }
}