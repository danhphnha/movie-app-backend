# 📱 Movie App Frontend SDK (Production-Ready Architecture)

Cấu trúc thư mục mã nguồn Frontend Android được thiết kế theo chuẩn **Clean Architecture / Google Modern Android Architecture Guidelines**:

---

## 📂 Cấu trúc thư mục chuẩn Production

```text
com.example.movieapp/
│
├── core/                                    # Cấu hình lõi (Network, Security, Utils)
│   └── network/
│       ├── ApiResponse.java                 # Standard Response Wrapper {success, message, data, timestamp}
│       ├── AuthInterceptor.java             # Tự động gắn Firebase Bearer Token vào Header
│       └── ApiClient.java                   # Retrofit & OkHttpClient Singleton
│
└── data/                                    # Tầng Dữ liệu (Data Layer)
    ├── remote/                              # Tương tác với Remote API
    │   ├── api/                             # Retrofit Interfaces chia theo Domain
    │   │   ├── CollectionApiService.java
    │   │   ├── ReviewApiService.java
    │   │   └── PaymentApiService.java
    │   ├── request/                         # Request DTOs
    │   │   ├── CreateCollectionRequest.java
    │   │   ├── CreateReviewRequest.java
    │   │   ├── ReplyReviewRequest.java
    │   │   ├── UpdateReplyRequest.java
    │   │   └── InitPaymentRequest.java
    │   └── response/                        # Response Models
    │       ├── MovieDto.java
    │       ├── CollectionDto.java
    │       ├── CommentDto.java
    │       └── PaymentUrlDto.java
    │
    └── repository/                          # Repository Pattern (Cầu nối giữa Data & UI/ViewModel)
        ├── CollectionRepository.java
        ├── ReviewRepository.java
        └── PaymentRepository.java
```

---

## 🚀 Cách sử dụng từ Activity / Fragment / ViewModel

### 1. Khởi tạo Repository:
```java
// Trong ViewModel hoặc Activity
private final CollectionRepository collectionRepo = new CollectionRepository();
private final ReviewRepository reviewRepo = new ReviewRepository();
private final PaymentRepository paymentRepo = new PaymentRepository();
```

---

### 2. Tạo thanh toán VNPay:
```java
paymentRepo.createPaymentUrl(50000, "Nâng cấp gói VIP", new Callback<ApiResponse<Map<String, String>>>() {
    @Override
    public void onResponse(Call<ApiResponse<Map<String, String>>> call, Response<ApiResponse<Map<String, String>>> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            String paymentUrl = response.body().getData().get("paymentUrl");
            // Mở link trong Chrome Custom Tabs hoặc Trình duyệt
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            startActivity(intent);
        } else {
            Toast.makeText(context, "Lỗi tạo thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<Map<String, String>>> call, Throwable t) {
        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

---

### 3. Gửi đánh giá phim:
```java
reviewRepo.submitReview("avatar-2", "Avatar: Dòng Chảy Của Nước", 5.0, "Phim kỹ xảo quá tuyệt vời!", new Callback<ApiResponse<CommentDto>>() {
    @Override
    public void onResponse(Call<ApiResponse<CommentDto>> call, Response<ApiResponse<CommentDto>> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            Toast.makeText(context, "Đã gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<CommentDto>> call, Throwable t) {
        Toast.makeText(context, "Gửi thất bại", Toast.LENGTH_SHORT).show();
    }
});
```

---

### 4. Lấy danh sách bộ sưu tập của người dùng:
```java
collectionRepo.getMyCollections(new Callback<ApiResponse<List<CollectionDto>>>() {
    @Override
    public void onResponse(Call<ApiResponse<List<CollectionDto>>> call, Response<ApiResponse<List<CollectionDto>>> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            List<CollectionDto> collections = response.body().getData();
            // Cập nhật lên RecyclerView Adapter
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<List<CollectionDto>>> call, Throwable t) {
        // Xử lý lỗi
    }
});
```
