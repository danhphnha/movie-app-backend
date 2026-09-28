package com.example.movieapp.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ==================== DATA MODELS & DTOs ====================
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String?
)

data class PaymentInitRequest(
    val amount: Int,
    val orderInfo: String? = null
)

data class CreateReviewRequest(
    val slug: String,
    val movieTitle: String? = null,
    val rating: Double,
    val comment: String
)

data class ReplyRequest(
    val parentId: String,
    val comment: String,
    val slug: String? = null,
    val movieTitle: String? = null
)

data class CollectionCreateRequest(
    val collectionName: String
)

data class UpdateReplyRequest(
    val comment: String
)

data class MovieDto(
    val name: String? = null,
    val slug: String? = null,
    val poster_url: String? = null
)

data class CollectionFilmDto(
    val id: String? = null,
    val collection_name: String? = null,
    val userId: String? = null
)

data class CommentDto(
    val id: String? = null,
    val username: String? = null,
    val comment: String? = null,
    val rating: Double? = null,
    val userId: String? = null,
    val slug: String? = null,
    val movieTitle: String? = null,
    val parentId: String? = null,
    val timestamp: Any? = null
)

// ==================== AUTH INTERCEPTOR ====================
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            try {
                val tokenResult = Tasks.await(user.getIdToken(false))
                val token = tokenResult.token
                if (!token.isNullOrEmpty()) {
                    val authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    return chain.proceed(authenticatedRequest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return chain.proceed(originalRequest)
    }
}

// ==================== RETROFIT API SERVICE ====================
interface ApiService {
    @GET("api/getCollectionsByUser")
    fun getMyCollections(): Call<ApiResponse<List<CollectionFilmDto>>>

    @GET("api/getFilmsByCollectionId")
    fun getFilmsByCollectionId(@Query("collectionId") collectionId: String): Call<ApiResponse<List<MovieDto>>>

    @POST("api/addCollection")
    fun addCollection(@Body request: CollectionCreateRequest): Call<ApiResponse<CollectionFilmDto>>

    @POST("api/addFilmToCollection")
    fun addFilmToCollection(
        @Query("collectionId") collectionId: String,
        @Body movie: MovieDto
    ): Call<ApiResponse<String>>

    @GET("api/checkFilmInCollection")
    fun checkFilmInCollection(
        @Query("collectionId") collectionId: String,
        @Query("slug") slug: String
    ): Call<ApiResponse<Boolean>>

    @GET("api/checkFilmInUserCollections")
    fun checkFilmInUserCollections(@Query("slug") slug: String): Call<ApiResponse<Boolean>>

    @DELETE("api/deleteCollection")
    fun deleteCollection(@Query("collectionId") collectionId: String): Call<ApiResponse<String>>

    @DELETE("api/deleteFilmFromCollection")
    fun deleteFilmFromCollection(
        @Query("collectionId") collectionId: String,
        @Query("slug") slug: String
    ): Call<ApiResponse<String>>

    @GET("api/reviews/{slug}")
    fun getReviews(@Path("slug") slug: String): Call<ApiResponse<List<CommentDto>>>

    @GET("api/reviews/{slug}/average")
    fun getAverageRating(@Path("slug") slug: String): Call<ApiResponse<Map<String, Any>>>

    @POST("api/reviews")
    fun submitReview(@Body request: CreateReviewRequest): Call<ApiResponse<CommentDto>>

    @GET("api/reviews/my-review/{slug}")
    fun getMyReview(@Path("slug") slug: String): Call<ApiResponse<CommentDto>>

    @DELETE("api/reviews/{reviewId}")
    fun deleteReview(@Path("reviewId") reviewId: String): Call<ApiResponse<Void>>

    @POST("api/reviews/reply")
    fun addReply(@Body request: ReplyRequest): Call<ApiResponse<CommentDto>>

    @GET("api/reviews/{commentId}/replies")
    fun getReplies(@Path("commentId") commentId: String): Call<ApiResponse<List<CommentDto>>>

    @PUT("api/reviews/{reviewId}/reply")
    fun updateReply(
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReplyRequest
    ): Call<ApiResponse<Void>>

    @POST("api/pay")
    fun createPayment(@Body request: PaymentInitRequest): Call<ApiResponse<Map<String, String>>>
}

// ==================== RETROFIT CLIENT ====================
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
