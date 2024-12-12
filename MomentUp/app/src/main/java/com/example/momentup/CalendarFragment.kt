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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.momentup.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.*

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private var selectedDate: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarView()
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
        val uploadDates = setOf(
            CalendarDay.from(2024, 12, 1),
            CalendarDay.from(2024, 12, 5),
        )

        // 데코레이터 추가
        calendarView.addDecorators(
            DefaultDayDecorator(requireContext()),
            TodayDecorator(requireContext()),
            SundayDecorator(),
            SaturdayDecorator(),
            UploadDayDecorator(requireContext(), uploadDates),
            DisabledDayDecorator(requireContext(), uploadDates),
            selectedDecorator
        )

        // 날짜 선택 리스너
        calendarView.setOnTitleClickListener { false }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                selectedDate = date
                selectedDecorator.setDate(date)
                calendarView.invalidateDecorators()
                binding.textView.text = "${date.day}일 업로드"
            }
        }
    }

    // 기본 배경 데코레이터
    private class DefaultDayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_rounded_rectangle)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return true  // 모든 날짜에 적용
        }

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
        }
    }

    // 오늘 날짜 데코레이터
    private class TodayDecorator(context: Context) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val successColor = ContextCompat.getColor(context, R.color.success)
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_today)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == today
        }

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }

            view.addSpan(ForegroundColorSpan(successColor))
        }
    }

    // 업로드된 날짜 데코레이터
    private class UploadDayDecorator(
        context: Context,
        private val uploadDays: Set<CalendarDay>
    ) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_uploaded_day)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return uploadDays.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
            view.addSpan(ForegroundColorSpan(Color.WHITE))
        }
    }

    // X 아이콘 날짜 데코레이터
    private class DisabledDayDecorator(
        context: Context,
        private val uploadDates: Set<CalendarDay>
    ) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val xDrawable = ContextCompat.getDrawable(context, R.drawable.ic_x)
        private val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.calendar_rounded_rectangle)

        // LayerDrawable 생성
        private val layerDrawable = LayerDrawable(arrayOf(
            backgroundDrawable,  // 기본 배경
            xDrawable           // X 아이콘
        )).apply {
            // X 아이콘의 위치와 크기 조정
            setLayerInset(1, 8, 8, 8, 8)  // left, top, right, bottom
        }

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.isBefore(today) && !uploadDates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.setBackgroundDrawable(layerDrawable)
        }
    }

    // 선택된 날짜 데코레이터
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

    // 일요일 데코레이터 (빨간색 텍스트)
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.date.dayOfWeek.value == 7
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    // 토요일 데코레이터 (파란색 텍스트)
    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.date.dayOfWeek.value == 6
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }
}