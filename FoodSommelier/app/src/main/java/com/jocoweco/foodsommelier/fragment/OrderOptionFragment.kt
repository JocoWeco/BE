package com.jocoweco.foodsommelier.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.feat.restaurant.OrderRequest
import com.jocoweco.foodsommelier.data.feat.restaurant.Sender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderOptionFragment : Fragment(R.layout.fragment_order_option) {

    private lateinit var foodTypeGroup: LinearLayout
    private lateinit var moodGroup: LinearLayout
    private lateinit var spicinessGroup: LinearLayout

    private lateinit var etPreferIngredients: EditText
    private lateinit var etUnpreferIngredients: EditText
    private lateinit var etKeyword: EditText

    private lateinit var btnSummit: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodTypeGroup = view.findViewById(R.id.food_type_group)
        moodGroup = view.findViewById(R.id.mood_group)
        spicinessGroup = view.findViewById(R.id.spiciness_group)

        etPreferIngredients = view.findViewById(R.id.input_preferred_ingredients)
        etUnpreferIngredients = view.findViewById(R.id.input_unpreferred_ingredients)
        etKeyword = view.findViewById(R.id.input_keywords)

        btnSummit = view.findViewById(R.id.submit_button)


        // 종류
        setupToggleGroup(foodTypeGroup)

        // 분위기
        setupToggleGroup(moodGroup)

        // 맵기
        setupToggleGroup(spicinessGroup)


        // 제출
        btnSummit.setOnClickListener {
            // 선택된 값들을 모두 읽어옴
            val selectedFoodType = getSelectedTextFromGroup(foodTypeGroup)
            val selectedMood = getSelectedTextFromGroup(moodGroup)
            val selectedSpiciness = getSelectedTextFromGroup(spicinessGroup)

            val preferredText = etPreferIngredients.text.toString()
            val unpreferredText = etUnpreferIngredients.text.toString()
            val keywordsText = etKeyword.text.toString()

            // TODO: 읽어온 값들을 데이터 클래스에 담아 서버로 전송 (API 호출)
            Log.d("OrderSheet", "음식종류: $selectedFoodType")
            Log.d("OrderSheet", "분위기: $selectedMood")
            Log.d("OrderSheet", "맵기: $selectedSpiciness")
            Log.d("OrderSheet", "선호: $preferredText, 비선호: $unpreferredText, 키워드: $keywordsText")

            Toast.makeText(context, "주문서가 제출되었습니다.", Toast.LENGTH_SHORT).show()

            val text = "음식종류:$selectedFoodType, 분위기:$selectedMood, 맵기:$selectedSpiciness, " +
                    "선호: $preferredText, 비선호: $unpreferredText, 키워드: $keywordsText\""

            // 서버로 전송
            sendMessageToAI(text)
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

    // ai로 전송
    private fun sendMessageToAI(requestMessage: String) {
        val orderRequest = OrderRequest(message = requestMessage)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.getApi(requireContext()).summitOrder(orderRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        navigateToNextFragment()

                    } else {
                        Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "서버 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 프래그먼트 이동
    private fun navigateToNextFragment() {
        val nextFragment = ResultFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_result, nextFragment)
            .addToBackStack(null)
            .commit()
    }
}