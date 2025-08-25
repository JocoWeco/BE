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
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.databinding.ActivityRegisterLocalBinding
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.constant.Gender
import com.jocoweco.foodsommelier.data.feat.login.CheckEmailRequest
import com.jocoweco.foodsommelier.data.feat.login.CheckLoginIdRequest
import com.jocoweco.foodsommelier.data.feat.login.CheckNicknameRequest
import com.jocoweco.foodsommelier.data.feat.register.RegisterLocalRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId

class RegisterLocalActivity : BaseActivity() {
    val TAG = "Register Local Activity"

    private lateinit var etLoginId: EditText
    private lateinit var btnCheckId: Button

    private lateinit var etPassword: EditText
    private lateinit var etPasswordRe: EditText

    private lateinit var etNickname: EditText
    private lateinit var btnCheckNickname: Button

    private lateinit var etEmail: EditText
    private lateinit var btnCheckEmail: Button

    private lateinit var tvBirth: TextView
    private lateinit var btnBirth: Button
    private var birth: String = ""

    private lateinit var radioGroupGender: RadioGroup
    private var selectedGender: Gender? = null

    private var enabledLoginId: Boolean = false
    private var enabledPassword: Boolean = false
    private var enabledNickname: Boolean = false
    private var enabledEmail: Boolean = false

    private lateinit var btnCreateUser: Button

    private val binding by lazy { ActivityRegisterLocalBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        etLoginId = binding.etLocalId
        btnCheckId = binding.btnEnableId
        etPassword = binding.etPassword
        etPasswordRe = binding.etPasswordRe

        etNickname = binding.etNickname
        btnCheckNickname = binding.btnEnableNickname
        etEmail = binding.etEmail
        btnCheckEmail = binding.btnEnableEmail
        tvBirth = binding.tvBirth
        btnBirth = binding.btnBirth
        radioGroupGender = binding.groupGender
        btnCreateUser = binding.btnCreateUser

        // 1. 아이디
        setUpLocalId()

        // 2. 비밀번호
        setUpPassword()

        // 3. 닉네임
        setUpNickname()

        // 4. 이메일
        setUpEmail()

        // 5. 생년월일
        setUpBirth()

        // 6. 성별
        setUpGender()

        // 가입 정보 서버로 전달
        createUser()
    }

    // 1. 아이디
    private fun setUpLocalId() {
        btnCheckId.setOnClickListener {
            val id = etLoginId.text.toString()
            if (id.isBlank()) return@setOnClickListener

            val request = CheckLoginIdRequest(loginId = id)

            lifecycleScope.launch {
                val result = runSafeApiCall(
                    onError = { msg -> showToast(msg) }) {

                    RetrofitInstance.getApi(this@RegisterLocalActivity)
                        .checkDuplicatedLoginId(request)
                }

                if (result?.body() == true) {
                    btnCheckId.text = "사용 가능"
                    enabledLoginId = true
                } else {
                    Toast.makeText(this@RegisterLocalActivity, "이미 사용 중인 아이디", Toast.LENGTH_SHORT)
                        .show()
                    enabledLoginId = false
                }
            }
        }

        etLoginId.doAfterTextChanged {
            if (enabledLoginId) {
                btnCheckId.text = "확인"
                enabledLoginId = false
            }
        }
    }

    // 2. 비밀번호
    private fun setUpPassword() {
        val passwordWatcher = {
            val pw = etPassword.text.toString()
            val rePw = etPasswordRe.text.toString()
            enabledPassword = pw.isNotBlank() && pw == rePw
        }

        etPassword.doAfterTextChanged { passwordWatcher() }
        etPasswordRe.doAfterTextChanged { passwordWatcher() }
    }

    // 3. 닉네임
    private fun setUpNickname() {
        btnCheckNickname.setOnClickListener {
            val nickname = etNickname.text.toString()
            if (nickname.isBlank()) return@setOnClickListener

            val request = CheckNicknameRequest(nickname = nickname)

            lifecycleScope.launch {
                val result = runSafeApiCall(
                    onError = { msg -> showToast(msg) }) {
                    RetrofitInstance.getApi(this@RegisterLocalActivity)
                        .checkDuplicatedNickname(request)
                }

                if (result?.body() == true) {
                    btnCheckNickname.text = "사용 가능"
                    enabledNickname = true
                } else {
                    Toast.makeText(this@RegisterLocalActivity, "이미 사용 중인 닉네임", Toast.LENGTH_SHORT)
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

    // 4. 이메일
    private fun setUpEmail() {
        btnCheckEmail.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isBlank()) return@setOnClickListener

            val request = CheckEmailRequest(email = email)

            lifecycleScope.launch {
                val result = runSafeApiCall(
                    onError = { msg -> showToast(msg) }) {
                    RetrofitInstance.getApi(this@RegisterLocalActivity)
                        .checkDuplicatedEmail(request)
                }

                if (result?.body() == true) {
                    btnCheckEmail.text = "사용 가능"
                    enabledEmail = true
                } else {
                    Toast.makeText(this@RegisterLocalActivity, "이미 사용 중인 이메일", Toast.LENGTH_SHORT)
                        .show()
                    enabledEmail = false
                }
            }
        }

        etEmail.doAfterTextChanged {
            if (enabledEmail) {
                btnCheckEmail.text = "확인"
                enabledEmail = false
            }
        }
    }

    // 5. 생년월일
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

    // 6. 성별
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

            if (!enabledLoginId) notConfirmedFields.add("아이디")
            if (!enabledPassword) notConfirmedFields.add("비밀번호")
            if (!enabledNickname) notConfirmedFields.add("닉네임")
            if (!enabledEmail) notConfirmedFields.add("이메일")
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

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterLocalActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendDataToServer() {
        val request = RegisterLocalRequest(
            loginId = etLoginId.text.toString(),
            password = etPassword.text.toString(),
            nickname = etNickname.text.toString(),
            email = etEmail.text.toString(),
            gender = selectedGender,
            birth = birth
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val registerResponse = runSafeApiCall(
                onError = { msg -> showToast(msg) }) {
                RetrofitInstance.getApi(this@RegisterLocalActivity).registerLocal(request)

            }
            withContext(Dispatchers.Main) {
                if (registerResponse?.isSuccessful == true) {
                    showToast("회원가입 성공")
                    val intent = Intent(this@RegisterLocalActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    showToast("회원가입 실패")
                }
            }

        }

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