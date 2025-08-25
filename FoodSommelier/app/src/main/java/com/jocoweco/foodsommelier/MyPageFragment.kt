package com.jocoweco.foodsommelier

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import com.jocoweco.foodsommelier.data.feat.restaurant.Restaurant
import com.jocoweco.foodsommelier.data.feat.restaurant.RestaurantAdapter
import com.jocoweco.foodsommelier.login.LoginActivity
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {

    // 뷰들을 나중에 사용하기 위해 변수로 선언
//    private lateinit var nicknameTextView: TextView
//    private lateinit var userIdTextView: TextView
//    private lateinit var birthdateTextView: TextView
//    private lateinit var restaurantsRecyclerView: RecyclerView
//    private lateinit var logoutButton: TextView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_mypage, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // 1. XML의 뷰들과 코드 연결
//        nicknameTextView = view.findViewById(R.id.tv_nickname)
//        userIdTextView = view.findViewById(R.id.tv_email)
//        birthdateTextView = view.findViewById(R.id.tv_birthdate)
//        restaurantsRecyclerView = view.findViewById(R.id.rv_recent_restaurants)
//        logoutButton = view.findViewById(R.id.tv_logout)
//
//        // 2. 로그아웃 버튼 클릭 시 signOutAndGoToLogin 함수 호출
//        logoutButton.setOnClickListener {
//            signOutAndGoToLogin()
//        }
//
//        // 3. 서버에서 데이터 가져오기 시작
//        fetchMyPageData()
//    }
//
//    private fun fetchMyPageData() {
//        // Fragment의 생명주기에 맞춰 안전하게 코루틴 실행
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                // ✅ SharedPreferences에서 저장된 실제 토큰 불러오기
//                val token = SharedPreferencesHelper.getAccessToken(requireContext())
//                if (token == null) {
//                    // 토큰이 없으면 로그인 정보가 없는 것이므로, 로그인 화면으로 보냄
//                    Toast.makeText(context, "로그인 정보가 만료되었습니다.", Toast.LENGTH_SHORT).show()
//                    signOutAndGoToLogin()
//                    return@launch
//                }
//
//                // ✅ 실제 토큰을 사용하여 API 호출 ("Bearer " 접두사 포함)
//                val apiToken = "Bearer $token"
//                val userInfo = RetrofitInstance.api.fetchUserInfo(apiToken)
//                val restaurantList = RetrofitInstance.api.fetchRecentRestaurants(apiToken)
//
//                // UI 업데이트
////                updateUserInfoUI()
////                setupRecyclerView(restaurantList)
//
//            } catch (e: Exception) {
//                // 오류 발생 시 사용자에게 알림
//                Toast.makeText(context, "데이터 로딩 실패: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
////    private fun updateUserInfoUI(userData: Response<String>) {
////        nicknameTextView.text = userData.username
////        userIdTextView.text = "아이디: ${userData.userId}"
////        birthdateTextView.text = "생년월일: ${userData.birthDate}"
////    }
//
//    private fun setupRecyclerView(restaurantList: List<Restaurant>) {
//        restaurantsRecyclerView.adapter = RestaurantAdapter(restaurantList)
//        restaurantsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//    }
//
//    /**
//     * 구글 로그아웃, 내부 토큰 삭제, 로그인 화면 이동을 처리하는 함수
//     */
//    private fun signOutAndGoToLogin() {
//        // 구글 로그아웃을 위해 GoogleSignInClient 객체 생성
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//
//        // 1. 구글 계정 로그아웃
//        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
//            // 2. SharedPreferences에 저장된 내부 토큰 삭제
//            SharedPreferencesHelper.clearToken(requireContext())
//
//            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
//
//            // 3. 로그인 액티비티로 이동
//            val intent = Intent(context, LoginActivity::class.java)
//            // 기존의 모든 액티비티를 스택에서 제거 (뒤로가기 방지)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
//    }
}