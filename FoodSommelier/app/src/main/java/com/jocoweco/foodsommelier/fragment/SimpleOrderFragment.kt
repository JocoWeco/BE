package com.jocoweco.foodsommelier.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.jocoweco.foodsommelier.R

class SimpleOrderFragment : Fragment() {

    // 각 ToggleButton 그룹을 담을 LinearLayout 변수
    private lateinit var foodTypeGroup: LinearLayout
    private lateinit var moodGroup: LinearLayout
    private lateinit var spicinessGroup: LinearLayout

    // EditText 변수
    private lateinit var preferredInput: EditText
    private lateinit var unpreferredInput: EditText
    private lateinit var keywordsInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. UI 요소들과 코드 연결
        foodTypeGroup = view.findViewById(R.id.food_type_group) // XML에서 LinearLayout에 ID 추가 필요
        moodGroup = view.findViewById(R.id.mood_group)         // XML에서 LinearLayout에 ID 추가 필요
        spicinessGroup = view.findViewById(R.id.spiciness_group) // XML에서 LinearLayout에 ID 추가 필요

        preferredInput = view.findViewById(R.id.input_preferred_ingredients)
        unpreferredInput = view.findViewById(R.id.input_unpreferred_ingredients)
        keywordsInput = view.findViewById(R.id.input_keywords)

        val submitButton: Button = view.findViewById(R.id.submit_button)

        // 2. 각 ToggleButton 그룹에 '하나만 선택' 기능 설정
//        setupToggleGroup(foodTypeGroup)
//        setupToggleGroup(moodGroup)
//        setupToggleGroup(spicinessGroup)

        // 3. '제출하기' 버튼 클릭 이벤트 처리
        submitButton.setOnClickListener {
            // 선택된 값들을 모두 읽어옴
            val selectedFoodType = getSelectedTextFromGroup(foodTypeGroup)
            val selectedMood = getSelectedTextFromGroup(moodGroup)
            val selectedSpiciness = getSelectedTextFromGroup(spicinessGroup)

            val preferredText = preferredInput.text.toString()
            val unpreferredText = unpreferredInput.text.toString()
            val keywordsText = keywordsInput.text.toString()

            // TODO: 읽어온 값들을 데이터 클래스에 담아 서버로 전송 (API 호출)
            Log.d("OrderSheet", "음식종류: $selectedFoodType")
            Log.d("OrderSheet", "분위기: $selectedMood")
            Log.d("OrderSheet", "맵기: $selectedSpiciness")
            Log.d("OrderSheet", "선호: $preferredText, 비선호: $unpreferredText, 키워드: $keywordsText")


            Toast.makeText(context, "주문서가 제출되었습니다.", Toast.LENGTH_SHORT).show()

        }
    }

    /** LinearLayout 안의 ToggleButton들에게 '하나만 선택' 기능을 부여하는 함수 */
    private fun setupToggleGroup(group: LinearLayout) {
        // 그룹 내의 모든 ToggleButton을 가져옴
        val buttons = group.children.filterIsInstance<ToggleButton>().toList()
        buttons.forEach { button ->
            button.setOnClickListener {
                // 현재 버튼을 제외한 나머지 버튼들을 모두 선택 해제
                buttons.forEach { otherButton ->
                    if (otherButton != button) {
                        otherButton.isChecked = false
                    }
                }
                // 현재 버튼은 항상 선택 상태로 유지
                button.isChecked = true
            }
        }
    }

    /** 그룹 내에서 선택된 ToggleButton의 텍스트를 반환하는 함수 */
    private fun getSelectedTextFromGroup(group: LinearLayout): String? {
        return group.children
            .filterIsInstance<ToggleButton>()
            .find { it.isChecked }
            ?.text?.toString()
    }
}