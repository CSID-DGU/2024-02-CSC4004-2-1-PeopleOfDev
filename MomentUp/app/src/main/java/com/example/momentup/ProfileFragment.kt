package com.example.momentup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.momentup.databinding.FragmentProfileBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.time.DayOfWeek
import java.time.LocalDate

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: CalendarDay? = null
    private val uploadsByDate = mapOf(
        CalendarDay.from(2024, 12, 1) to listOf("김동국의 STUDY", "이민지의 WORKOUT"),
        CalendarDay.from(2024, 12, 5) to listOf("박지성의 READING")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarView()
        updateUploadList(emptyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCalendarView() {
        val calendarView = binding.calendarView

        // 선택된 날짜 데코레이터 인스턴스 생성
        val selectedDecorator = SelectedDayDecorator(requireContext())

        // 헤더 포맷 설정
        calendarView.setTitleFormatter { day ->
            val title = SpannableString("${day.year}  ${day.month}월")
            title.setSpan(
                RelativeSizeSpan(0.7f),
                0,
                day.year.toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title
        }

        // 업로드된 날짜들 설정
        val uploadDates = uploadsByDate.keys

        // 데코레이터 추가
        calendarView.addDecorators(
            DefaultDayDecorator(requireContext()),
            TodayDecorator(requireContext()),
            SundayDecorator(),
            SaturdayDecorator(),
            UploadDayDecorator(requireContext(), uploadDates.toSet()),
            DisabledDayDecorator(requireContext(), uploadDates.toSet()),
            selectedDecorator
        )

        // 날짜 선택 리스너
        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                selectedDate = date
                selectedDecorator.setDate(date)
                calendarView.invalidateDecorators()
                binding.textView.text = "${date.day}일 업로드"

                // 선택된 날짜에 맞는 업로드 목록 갱신
                val uploads = uploadsByDate[date] ?: emptyList()
                updateUploadList(uploads)
            }
        }
    }

    private fun updateUploadList(uploadList: List<String>) {
        val uploadContainer = binding.uploadContainer
        uploadContainer.removeAllViews()

        for (upload in uploadList) {
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.upload_item, uploadContainer, false)
            val textView = itemView.findViewById<TextView>(R.id.uploadText)
            textView.text = upload
            uploadContainer.addView(itemView)
        }
    }

    // Calendar Decorators
    private class DefaultDayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_rounded_rectangle)

        override fun shouldDecorate(day: CalendarDay): Boolean = true

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
        }
    }

    private class TodayDecorator(context: Context) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val successColor = ContextCompat.getColor(context, R.color.success)
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_today)

        override fun shouldDecorate(day: CalendarDay): Boolean = day == today

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
            view.addSpan(ForegroundColorSpan(successColor))
        }
    }

    private class UploadDayDecorator(
        context: Context,
        private val uploadDays: Set<CalendarDay>
    ) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_uploaded_day)

        override fun shouldDecorate(day: CalendarDay): Boolean = uploadDays.contains(day)

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
            view.addSpan(ForegroundColorSpan(Color.WHITE))
        }
    }

    private class DisabledDayDecorator(
        context: Context,
        private val uploadDates: Set<CalendarDay>
    ) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val xDrawable = ContextCompat.getDrawable(context, R.drawable.ic_x)
        private val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.calendar_rounded_rectangle)

        private val layerDrawable = LayerDrawable(arrayOf(
            backgroundDrawable,
            xDrawable
        )).apply {
            setLayerInset(1, 8, 8, 8, 8)
        }

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isBefore(today) && !uploadDates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(layerDrawable)
        }
    }

    private class SelectedDayDecorator(context: Context) : DayViewDecorator {
        private var selectedDate: CalendarDay? = null
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selected_day)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return selectedDate != null && day == selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
        }

        fun setDate(date: CalendarDay?) {
            this.selectedDate = date
        }
    }

    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.date.dayOfWeek.value == 7
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.date.dayOfWeek.value == 6
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }
}