package com.jocoweco.foodsommelier.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.jocoweco.foodsommelier.BaseActivity
import com.jocoweco.foodsommelier.MainActivity
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.constant.Gender
import com.jocoweco.foodsommelier.data.feat.login.CheckNicknameRequest
import com.jocoweco.foodsommelier.data.feat.register.RegisterSocialRequest
import com.jocoweco.foodsommelier.databinding.ActivityRegisterSocialBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId

class RegisterSocialActivity : BaseActivity() {
    val TAG = "Register Social Activity"

    private lateinit var etNickname: EditText
    private lateinit var btnCheckNickname: Button

    private lateinit var etEmail: EditText

    private lateinit var tvBirth: TextView
    private lateinit var btnBirth: Button
    private var birth: String = ""

    private lateinit var radioGroupGender: RadioGroup
    private var selectedGender: Gender? = null

    private var enabledNickname: Boolean = false

    private lateinit var btnCreateUser: Button

    private lateinit var email: String
    private lateinit var providerId: String
    private lateinit var name: String
    private val binding by lazy { ActivityRegisterSocialBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        etNickname = binding.etNickname
        btnCheckNickname = binding.btnEnableNickname
        etEmail = binding.etEmail
        tvBirth = binding.tvBirth
        btnBirth = binding.btnBirth
        radioGroupGender = binding.groupGender
        btnCreateUser = binding.btnCreateUser

        val intent = Intent(this, MainActivity::class.java)
        providerId = intent.getStringExtra("providerId").toString()
        email = intent.getStringExtra("email").toString()
        name = intent.getStringExtra("name").toString()

        // 1. 닉네임
        setUpNickname()

        // 2. 이메일 _변경불가
        etEmail.setText(email)
        etEmail.isFocusable = false
        etEmail.isClickable = false

        // 3. 생년월일
        setUpBirth()

        // 4. 성별
        setUpGender()

        // 유저 생성 버튼
        createUser()
    }

    // 1. 닉네임
    private fun setUpNickname() {
        btnCheckNickname.setOnClickListener {
            val nickname = etNickname.text.toString()
            if (nickname.isBlank()) return@setOnClickListener

            val request = CheckNicknameRequest(nickname = nickname)

            lifecycleScope.launch {
                val result = runSafeApiCall(
                    onError = { msg -> showToast(msg) }) {
                    RetrofitInstance.getApi(this@RegisterSocialActivity).checkDuplicatedNickname(request)
                }

                if (result?.body() == true) {
                    btnCheckNickname.text = "사용 가능"
                    enabledNickname = true
                } else {
                    Toast.makeText(this@RegisterSocialActivity, "이미 사용 중인 닉네임", Toast.LENGTH_SHORT)
                        .show()
                    enabledNickname = false
                }
            }
        }

        etNickname.doAfterTextChanged {
            if (enabledNickname) {
                btnCheckNickname.text = "확인"
                enabledNickname = false
            }
        }
    }

    // 3. 생년월일
    @SuppressLint("DefaultLocale")
    private fun setUpBirth() {
        btnBirth.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("날짜를 선택하세요")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                birth = selectedDate.toString()
                tvBirth.text = birth
            }

            datePicker.show(supportFragmentManager, "date_picker")

        }
    }

    // 4. 성별
    private fun setUpGender() {
        radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.btn_gender_male -> Gender.MALE
                R.id.btn_gender_female -> Gender.FEMALE
                else -> null
            }
        }
    }

    // 유저 생성
    private fun createUser() {
        btnCreateUser.setOnClickListener {
            val notConfirmedFields = mutableListOf<String>()

            if (!enabledNickname) notConfirmedFields.add("닉네임")
            if (birth == "") notConfirmedFields.add("생년월일")
            if (selectedGender == null) notConfirmedFields.add("성별")

            if (notConfirmedFields.isEmpty()) {
                // 모두 true일 때
                sendDataToServer()

            } else {
                val message = "다음 항목 확인 필요: " + notConfirmedFields.joinToString(", ")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun sendDataToServer() {
        val request = RegisterSocialRequest(
            providerId = providerId,
            name = name,
            nickname = etNickname.text.toString(),
            email = email,
            gender = selectedGender,
            birth = birth
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val registerResponse = runSafeApiCall(
                onError = { msg -> showToast(msg) }) {
                RetrofitInstance.getApi(this@RegisterSocialActivity).registerGoogle(request)

            }
            withContext(Dispatchers.Main) {
                if (registerResponse?.isSuccessful == true) {
                    showToast("회원가입 성공")
                    val intent = Intent(this@RegisterSocialActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    showToast("회원가입 실패")
                }
            }

        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterSocialActivity, message, Toast.LENGTH_SHORT).show()
    }

    suspend fun <T> runSafeApiCall(
        onError: (String) -> Unit = {},
        block: suspend () -> Response<T>
    ): Response<T>? {
        return try {
            block()
        } catch (e: IOException) {
            val msg = "네트워크 오류 발생: ${e.message}"
            withContext(Dispatchers.Main) { onError(msg) }
            onError(msg)
            null
        } catch (e: HttpException) {
            val msg = "HTTP 오류 발생: ${e.code()} ${e.message}"
            withContext(Dispatchers.Main) { onError(msg) }
            onError(msg)
            null
        } catch (e: Exception) {
            val msg = "기타 오류 발생: ${e.message}"
            Log.e("Error", msg)
            withContext(Dispatchers.Main) { onError(msg) }
            null
        }
    }
}