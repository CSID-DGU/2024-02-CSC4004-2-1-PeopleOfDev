package com.example.momentup

import android.content.Context

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.momentup.databinding.ActivityCalendarBinding
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DayFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.time.DayOfWeek
import org.threeten.bp.LocalDate


class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding

    // 선택된 날짜 변수
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_calendar)
        // CalendarView 초기화 및 설정
        setupCalendarView()
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        val textView = findViewById<TextView>(R.id.textView)
        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            val selectedDate = "${date.day}일 업로드"
            textView.text = selectedDate
        })
    }

    private fun setupCalendarView() {
        // 데코레이터 초기화
        val dayDecorator = DayDecorator(this)
        val todayDecorator = TodayDecorator(this)
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
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)
        override fun shouldDecorate(day: CalendarDay): Boolean = true
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
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

    // 이번 달이 아닌 날짜를 비활성화
    private inner class SelectedMonthDecorator(private val selectedMonth: Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = day.month != selectedMonth
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(this@CalendarActivity, R.color.black)))
        }
    }

    // 일요일의 색상 설정
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            // `day.day` is the numeric representation of the day (1 to 7), with Sunday being 7
            return day.day == 7 // 7 corresponds to Sunday
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object : ForegroundColorSpan(Color.RED) {})
        }
    }

    // 토요일의 색상 설정
    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            // `day.day` is the numeric representation of the day (1 to 7), with Saturday being 6
            return day.day == 6 // 6 corresponds to Saturday
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(object : ForegroundColorSpan(Color.BLUE) {})
        }
    }
}

