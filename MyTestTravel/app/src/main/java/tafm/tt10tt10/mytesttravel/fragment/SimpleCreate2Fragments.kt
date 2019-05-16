package tafm.tt10tt10.mytesttravel.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TimePicker

class RequireTimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener{

    interface OnTimeSelectedListener{
        fun onSelected(hourOfDay: Int, minute: Int)
    }

    private lateinit var listener : OnTimeSelectedListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is RequireTimePickerFragment.OnTimeSelectedListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hourOfDay = 0
        val minute = 0
        return TimePickerDialog(context, android.R.style.Theme_Light_Panel, this, hourOfDay, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.onSelected(hourOfDay, minute)
    }
}

/*class DepartureFragment: Fragment(){

    companion object {
        fun createInstance(): DepartureFragment{
            return DepartureFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.sc2_departure, container, false)
    }
}*/