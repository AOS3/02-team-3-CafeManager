package com.lion.cafemanager_1.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import com.lion.cafemanager_1.MainActivity
import com.lion.cafemanager_1.R
import com.lion.cafemanager_1.databinding.FragmentCakeBookingBinding
import com.lion.cafemanager_1.util.FragmentName

class CakeBookingFragment : Fragment() {

    private lateinit var fragmentCakeBookingBinding: FragmentCakeBookingBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var calendarView: CalendarView
    private lateinit var diaryTextView: TextView
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var updateBtn: Button
    private lateinit var diaryContent: TextView
    private lateinit var title: TextView
    private lateinit var contextEditText: EditText

    private var fname: String = ""
    private var str: String = ""
    private val userID: String = "user" // Example userID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentCakeBookingBinding = FragmentCakeBookingBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        // Binding 초기화
        calendarView = fragmentCakeBookingBinding.calendarView
        diaryTextView = fragmentCakeBookingBinding.diaryTextView
        saveBtn = fragmentCakeBookingBinding.saveBtn
        deleteBtn = fragmentCakeBookingBinding.deleteBtn
        updateBtn = fragmentCakeBookingBinding.updateBtn
        diaryContent = fragmentCakeBookingBinding.diaryContent
        title = fragmentCakeBookingBinding.title
        contextEditText = fragmentCakeBookingBinding.contextEditText

        toobalCake()

        // CalendarView 이벤트 리스너 설정
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            diaryTextView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
            diaryContent.visibility = View.INVISIBLE
            updateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            contextEditText.setText("")
            checkDay(year, month, dayOfMonth, userID)
        }

        // 저장 버튼 클릭 리스너 설정
        saveBtn.setOnClickListener {
            saveDiary(fname)
            contextEditText.visibility = View.INVISIBLE
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            str = contextEditText.text.toString()
            diaryContent.text = str
            diaryContent.visibility = View.VISIBLE
        }

        return fragmentCakeBookingBinding.root
    }

    fun checkDay(cYear: Int, cMonth: Int, cDay: Int, userID: String) {
        fname = "$userID$cYear-${cMonth + 1}-$cDay.txt"
        Log.d("CakeBookingFragment", "Checking diary for file: $fname")

        try {
            val fileInputStream = mainActivity.openFileInput(fname)
            val fileData = ByteArray(fileInputStream.available())
            fileInputStream.read(fileData)
            fileInputStream.close()

            str = String(fileData)
            contextEditText.visibility = View.INVISIBLE
            diaryContent.visibility = View.VISIBLE
            diaryContent.text = str
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE

            // 수정 버튼 클릭 리스너 설정
            updateBtn.setOnClickListener {
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                contextEditText.setText(str)
                saveBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
            }

            // 삭제 버튼 클릭 리스너 설정
            deleteBtn.setOnClickListener {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                contextEditText.setText("")
                contextEditText.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                removeDiary(fname)
            }
        } catch (e: Exception) {
            Log.e("CakeBookingFragment", "Error reading file: $fname", e)
            diaryContent.visibility = View.INVISIBLE
            updateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
            saveBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
        }
    }

    // 다이어리 저장 함수
    fun saveDiary(readDay: String?) {
        try {
            val fileOutputStream = mainActivity.openFileOutput(readDay, Context.MODE_PRIVATE)
            val content = contextEditText.text.toString()
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
            Log.d("CakeBookingFragment", "Diary saved to file: $readDay")
        } catch (e: Exception) {
            Log.e("CakeBookingFragment", "Error saving file: $readDay", e)
        }
    }

    // 다이어리 삭제 함수
    fun removeDiary(readDay: String?) {
        try {
            // 해당 파일을 빈 파일로 덮어씌우기 (실제로 삭제하는 코드)
            val fileOutputStream = mainActivity.openFileOutput(readDay, Context.MODE_PRIVATE)
            fileOutputStream.write("".toByteArray())  // 빈 내용으로 덮어쓰기
            fileOutputStream.close()
            Log.d("CakeBookingFragment", "Diary removed for file: $readDay")
        } catch (e: Exception) {
            Log.e("CakeBookingFragment", "Error removing file: $readDay", e)
        }
    }

    // Toolbar 설정
    fun toobalCake() {
        fragmentCakeBookingBinding.apply {
            toolbarCakeBooking.setNavigationIcon(R.drawable.arrow_back_24px)

            // 뒤로가기 버튼 설정
            toolbarCakeBooking.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.CAKE_BOOKING_FRAGMENT)
            }
        }
    }
}

