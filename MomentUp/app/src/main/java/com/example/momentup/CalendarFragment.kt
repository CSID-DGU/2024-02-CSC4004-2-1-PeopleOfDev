package com.example.momentup

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.momentup.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import org.threeten.bp.LocalDate

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

    // 선택된 날짜 변수
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment 레이아웃 설정
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CalendarView 초기화 및 설정
        setupCalendarView()

        val calendarView = binding.calendarView
        val textView = binding.textView

        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            val selectedDate = "${date.day}일 업로드"
            textView.text = selectedDate
        })

        // 업로드된 날짜 리스트
        val uploadDates = setOf(
            CalendarDay.from(2024, 12, 1),
            CalendarDay.from(2024, 12, 5)
        )

        // 업로드 기록 데코레이터 추가
        calendarView.addDecorator(UploadDayDecorator(requireContext(), uploadDates))
    }

    private fun setupCalendarView() {
        // 데코레이터 초기화
        val dayDecorator = DayDecorator(requireContext())
        val todayDecorator = TodayDecorator(requireContext())
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        var selectedMonthDecorator = SelectedMonthDecorator(CalendarDay.today().month)

        // 데코레이터 추가
        binding.calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator)

        // 커스텀 헤더 포맷 설정 (연/월 포맷)
        binding.calendarView.setTitleFormatter(TitleFormatter { day ->
            val year = day.year
            val month = day.month
            String.format("%d년 %d월", year, month) // 또는 "${year}년 ${month}월"
        })

        // 날짜 선택 리스너 설정
        binding.calendarView.setOnDateChangedListener { widget: MaterialCalendarView, date: CalendarDay, selected: Boolean ->
            if (selected) {
                selectedStartDate = date.date
                selectedEndDate = null // 단일 선택 시 종료 날짜는 비움
            } else {
                selectedStartDate = null
            }
        }

        // 월 변경 리스너 설정
        binding.calendarView.setOnMonthChangedListener { widget: MaterialCalendarView, date: CalendarDay ->
            // 기존에 설정되어 있던 Decorators 초기화
            binding.calendarView.removeDecorators()
            binding.calendarView.invalidateDecorators()

            // Decorators 추가
            selectedMonthDecorator = SelectedMonthDecorator(date.month)
            binding.calendarView.addDecorators(dayDecorator, todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator)
        }
    }

    // 선택된 날짜의 배경 설정
    private inner class DayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selected_day)

        override fun shouldDecorate(day: CalendarDay): Boolean = true // 필요한 경우 조건 추가 가능

        override fun decorate(view: DayViewFacade) {
            // 선택된 날짜 배경을 설정
            view.setSelectionDrawable(drawable!!)

            // 글꼴 색상을 #14CC7F로 설정
            view.addSpan(object : ForegroundColorSpan(Color.parseColor("#14CC7F")) {})
        }
    }

    // 오늘 날짜의 배경 설정
    private class TodayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_circle_gray)
        private val today = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean = day == today

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    // 업로드 된 기록이 있는 날의 배경 설정
    private inner class UploadDayDecorator(
        context: Context,
        private val uploadDays: Set<CalendarDay>
    ) : DayViewDecorator {

        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_uploaded_day)

        override fun shouldDecorate(day: CalendarDay): Boolean {
            // 업로드 기록이 있는 날짜인지 확인
            return uploadDays.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            // XML에 정의된 배경을 설정
            view.setBackgroundDrawable(drawable!!)

            // 글꼴 색상을 흰색으로 설정
            view.addSpan(object : ForegroundColorSpan(Color.WHITE) {})
        }
    }

    // 이번 달이 아닌 날짜를 비활성화
    private inner class SelectedMonthDecorator(private val selectedMonth: Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = day.month != selectedMonth

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black)))
        }
    }

    // 일요일의 색상 설정
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.day == 7 // 7 corresponds to Sunday
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object : ForegroundColorSpan(Color.RED) {})
        }
    }

    // 토요일의 색상 설정
    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.day == 6 // 6 corresponds to Saturday
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object : ForegroundColorSpan(Color.BLUE) {})
        }
    }
}
