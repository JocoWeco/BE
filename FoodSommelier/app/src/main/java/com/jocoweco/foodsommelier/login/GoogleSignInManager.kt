package com.jocoweco.foodsommelier.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.NoCredentialException
import com.auth0.android.jwt.JWT
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.jocoweco.foodsommelier.MainActivity
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.data.RetrofitInstance
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import com.jocoweco.foodsommelier.data.feat.login.GoogleLoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/* Google ID 토큰 가져오기 */

class GoogleSignInManager(
    private val context: Context
) {
    val TAG = "GoogleSignInActivity"

    private val webClientId: String = context.getString(R.string.default_web_client_id)
    val credentialManager = CredentialManager.create(context)
    private lateinit var email: String
    private lateinit var providerId: String
    private lateinit var name: String

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun signIn() {

        val googleIdOption = buildGoogleIdOption(filterAuthorized = true, autoSelect = true)
        val request = buildCredentialRequest(googleIdOption)

        CoroutineScope(Dispatchers.Main).launch {
            signInWithCredentialManager(request, fallback = true)
        }
    }

    private fun buildGoogleIdOption(
        filterAuthorized: Boolean,
        autoSelect: Boolean
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterAuthorized)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(autoSelect)
            .build()
    }

    private fun buildCredentialRequest(option: CredentialOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private suspend fun signInWithCredentialManager(
        request: GetCredentialRequest,
        fallback: Boolean = false
    ) {
        delay(250)

        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            Log.i(TAG, "Google Login Success")
            handleSignIn(result)

        } catch (e: NoCredentialException) {
            handleError("No credentials found", e)

            if (fallback) {
                Log.i(TAG, "Attempting fallback login...")

                val fallbackOption =
                    buildGoogleIdOption(filterAuthorized = false, autoSelect = false)
                val fallbackRequest = buildCredentialRequest(fallbackOption)

                signInWithCredentialManager(fallbackRequest, fallback = false)
            }

        } catch (e: GoogleIdTokenParsingException) {
            handleError("Token parsing failed", e)
        } catch (e: GetCredentialCustomException) {
            handleError("Custom exception", e)
        } catch (e: GetCredentialCancellationException) {
            handleError("Sign-in cancelled", e)
        } catch (e: Exception) {
            handleError("Unexpected error", e)
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val jwt = JWT(idToken)

                        providerId = jwt.getClaim("sub").toString()
                        name = jwt.getClaim("name").toString()
                        email = googleIdTokenCredential.id
                        Log.i(TAG, "Google ID Token: $idToken")
                        Log.i(TAG, "Google Email: $email, Provider: $providerId, Name: $name")

                        // 서버에 토큰 전송(로그인/회원가입)
                        sendTokenToServer(idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unsupported type of credential: ${credential.type}")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    // 예외 처리
    fun handleError(message: String, e: Exception) {
        Log.e(TAG, "$message: ${e.localizedMessage}", e)
        Toast.makeText(context, "Sign in failed: $message", Toast.LENGTH_SHORT).show()
    }

    // 서버로 토큰 전송
    private fun sendTokenToServer(idToken: String) {
        val request = GoogleLoginRequest(
            googleIdToken = idToken
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = RetrofitInstance.getApi(context).loginGoogle(request)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        val response = result.body()
                        if (response != null && response.uuid.isNotEmpty()) {
                            SharedPreferencesHelper.saveAuthToken(
                                context, response.uuid, response.accessToken, response.refreshToken
                            )
                            // 사용자 정보 있으면 main 페이지로 이동
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            context.startActivity(intent)
                            Toast.makeText(context, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        // 사용자 정보 없으면 register_social 페이지로 이동
                        val intent = Intent(context, RegisterSocialActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtra("providerId", providerId)
                        intent.putExtra("email", email)
                        intent.putExtra("name", name)

                        context.startActivity(intent)
                        Toast.makeText(context, "유저 정보가 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Google: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}