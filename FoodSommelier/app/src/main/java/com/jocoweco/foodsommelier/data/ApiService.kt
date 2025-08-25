package com.jocoweco.foodsommelier.data

import com.jocoweco.foodsommelier.data.feat.login.CheckEmailRequest
import com.jocoweco.foodsommelier.data.feat.login.CheckLoginIdRequest
import com.jocoweco.foodsommelier.data.feat.login.CheckNicknameRequest
import com.jocoweco.foodsommelier.data.feat.login.GoogleLoginRequest
import com.jocoweco.foodsommelier.data.feat.login.LoginLocalRequest
import com.jocoweco.foodsommelier.data.feat.token.TokenResponse
import com.jocoweco.foodsommelier.data.feat.register.RegisterLocalRequest
import com.jocoweco.foodsommelier.data.feat.register.RegisterSocialRequest
import com.jocoweco.foodsommelier.data.feat.restaurant.OrderRequest
import com.jocoweco.foodsommelier.data.feat.restaurant.OrderResponse
import com.jocoweco.foodsommelier.data.feat.restaurant.Restaurant
import com.jocoweco.foodsommelier.data.feat.token.RefreshTokenRequest
import com.jocoweco.foodsommelier.data.feat.user.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    /* 일반 */
    // 일반 로그인
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginLocalRequest): Response<TokenResponse>

    // 일반 회원가입
    @POST("api/auth/register")
    suspend fun registerLocal(@Body request: RegisterLocalRequest): Response<Unit>

    // 아이디 확인
    @POST("api/auth/register/check-id")
    suspend fun checkDuplicatedLoginId(@Body request: CheckLoginIdRequest): Response<Boolean>

    // 이메일 확인
    @POST("api/auth/register/check-email")
    suspend fun checkDuplicatedEmail(@Body request: CheckEmailRequest): Response<Boolean>

    // 닉네임 확인
    @POST("api/auth/register/check-nickname")
    suspend fun checkDuplicatedNickname(@Body request: CheckNicknameRequest): Response<Boolean>


    /* 구글 */
    // 구글 로그인
    @POST("api/auth/google/login")
    suspend fun loginGoogle(@Body request: GoogleLoginRequest): Response<TokenResponse>

    // 구글 회원가입
    @POST("api/auth/google/register")
    suspend fun registerGoogle(@Body request: RegisterSocialRequest): Response<TokenResponse>


    // 토큰 재발급
    @POST("api/auth/reissue")
    suspend fun reissue(@Body request: RefreshTokenRequest): Response<TokenResponse>

    // 제외 메뉴

    // 회원 정보 수정

    // 로그아웃

    // 회원 탈퇴


    // 정보 조회
    @GET("api/user/info")
    suspend fun fetchUserInfo(): Response<String>

    @GET("api/user/info")
    suspend fun getUserInfo(): Response<UserData>


    // 최근 본 식당 목록을 가져오는 API (주소는 백엔드에 맞게 수정 필요)
    @GET("api/restaurants/recent")
    suspend fun fetchRecentRestaurants(): List<Restaurant>

    // 가게 조회
    @POST("api/order")
    suspend fun summitOrder(@Body request: OrderRequest): Response<OrderResponse>
}