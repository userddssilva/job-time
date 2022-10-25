package br.com.experimental.jobtime

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.experimental.jobtime.databinding.FragmentFirstBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


class FirstFragment : Fragment() {
    private val TAG = FirstFragment::class.java.simpleName
    private var time: SimpleDateFormat? = null
    private var date: SimpleDateFormat? = null
    private var clockTime: Calendar? = null
    private var clockStaticTime: Calendar? = null
    private var clockThread: Thread? = null

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val localDateTimeNow = LocalDateTime.now()

//        val picker =
//            MaterialTimePicker.Builder()
//                .setTimeFormat(TimeFormat.CLOCK_24H)
//                .setHour(12)
//                .setMinute(10)
//                .build()
//        fragmentManager?.let { picker.show(it, "tag") };

        val dateFormat = "EE, dd MMM yyyy"
        val timeFormat = "HH:mm:ss"

        time = SimpleDateFormat(timeFormat, Locale.getDefault())
        date = SimpleDateFormat(dateFormat, Locale.getDefault())

        clockStaticTime = Calendar.getInstance()
        clockStaticTime?.set(Calendar.HOUR_OF_DAY, 0)
        clockStaticTime?.set(Calendar.MINUTE, 0)
        clockStaticTime?.set(Calendar.SECOND, 0)
        clockStaticTime?.add(Calendar.HOUR_OF_DAY, 8)
        clockStaticTime?.add(Calendar.MINUTE, 17)

        clockTime = Calendar.getInstance()
        clockTime?.set(Calendar.HOUR_OF_DAY, 0)
        clockTime?.set(Calendar.MINUTE, 0)
        clockTime?.set(Calendar.SECOND, 0)

        syncClockUi()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun syncClockUi() {
        val clockUi = Runnable {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    updateClock()
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }

        clockThread = Thread(clockUi)
        clockThread?.start()
    }

    private fun updateClock() {
        activity?.runOnUiThread {
            try {
                val c: Calendar = Calendar.getInstance()
                binding.timeTv.text = time?.format(c.time)
                binding.dateTv.text = date?.format(c.time)

                clockStaticTime?.add(Calendar.SECOND, -1)
                binding.timeWorkedTv.text = clockStaticTime?.time?.let { time?.format(it) }

                clockTime?.add(Calendar.SECOND, 1)
                binding.timeToWorkTv.text = clockTime?.time?.let { time?.format(it) }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}

fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
}

fun dateToCalendar(date: Date): Calendar {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal
}