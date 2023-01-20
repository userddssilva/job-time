package br.com.experimental.jobtime

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.experimental.jobtime.databinding.FragmentFirstBinding
import java.text.SimpleDateFormat
import java.util.*

class FirstFragment : Fragment() {
    companion object {
        private val TAG = FirstFragment::class.java.simpleName
    }

    private var isStartedJob: Boolean = false

    private var time: SimpleDateFormat? = null
    private var date: SimpleDateFormat? = null

    private var clockWorkedHours: Calendar? = null
    private var clockTimeNow: Calendar? = null
    private var clockLeftHours: Calendar? = null

    private var clockThread: Thread? = null

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        init()
        syncClockUi()
        return binding.root
    }

    private fun init() {
        val dateFormat = "EE, dd MMM yyyy"
        val timeFormat = "HH:mm:ss"

        time = SimpleDateFormat(timeFormat, Locale.getDefault())
        date = SimpleDateFormat(dateFormat, Locale.getDefault())

        clockLeftHours = Calendar.getInstance()
        clockLeftHours?.set(Calendar.HOUR_OF_DAY, 0)
        clockLeftHours?.set(Calendar.MINUTE, 0)
        clockLeftHours?.set(Calendar.SECOND, 0)
        clockLeftHours?.add(Calendar.HOUR_OF_DAY, 8)
        clockLeftHours?.add(Calendar.MINUTE, 17)

        clockWorkedHours = Calendar.getInstance()
        clockWorkedHours?.set(Calendar.HOUR_OF_DAY, 0)
        clockWorkedHours?.set(Calendar.MINUTE, 0)
        clockWorkedHours?.set(Calendar.SECOND, 0)

        clockTimeNow = Calendar.getInstance()
        binding.dateTv.text = clockTimeNow?.time?.let { date?.format(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddTime.setOnClickListener {
            if (!isStartedJob) {
                binding.startJobTimeTv.text = clockTimeNow?.time?.let { time?.format(it) }
                binding.startJobTimeTv.setTextColor(Color.BLACK)
                clockTimeNow?.add(Calendar.HOUR_OF_DAY, 4)
                binding.startBreakJobTimeTv.text = clockTimeNow?.time?.let { time?.format(it) }
                clockTimeNow?.add(Calendar.HOUR_OF_DAY, 1)
                binding.endBreakJobTimeTv.text = clockTimeNow?.time?.let { time?.format(it) }
                clockTimeNow?.add(Calendar.HOUR_OF_DAY, 4)
                clockTimeNow?.add(Calendar.MINUTE, 17)
                binding.endJobTimeTv.text = clockTimeNow?.time?.let { time?.format(it) }
                isStartedJob = true
            }
        }
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
                clockTimeNow = Calendar.getInstance()

                if (isStartedJob) {
                    clockLeftHours?.add(Calendar.SECOND, -1)
                    clockWorkedHours?.add(Calendar.SECOND, 1)
                    binding.workedHours.text = clockWorkedHours?.time?.let { time?.format(it) }
                    binding.leftHours.text = clockLeftHours?.time?.let { time?.format(it) }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}