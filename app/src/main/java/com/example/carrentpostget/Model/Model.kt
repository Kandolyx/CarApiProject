package com.example.carrentpostget.Model

import com.google.gson.annotations.SerializedName

data class Car(
    val carId: Int,
    val brandId: Int,
    val colorId: Int,
    val modelYear: String,
    val dailyPrice: Int,
    val description: String
)
data class ImageUrlResponse(
    val imageUrl: String
)
data class CarDetail(
    val id: Int,
    val brandName: String,
    val colorName: String,
    val modelYear: String,
    val dailyPrice: Int,
    val description: String
)

data class CarImage(
    @SerializedName("carImageId") val carImageId: Int?,
    @SerializedName("carId") val carId: Int,
    @SerializedName("imagePath") val imagePath: String?,
    @SerializedName("imageDate") val imageDate: String?
)
data class CarImages(
    val carImageId: Int,
    val carId: Int,
    val imagePath: String,
    val imageDate: String
)

data class Color(
    val colorId: Int,
    val colorName: String
)

data class Brand(
    val brandId: Int,
    val brandName: String
)


data class CarResponse(
    val data: List<Car>,
    val success: Boolean,
    val message: String
)

data class CarDetailResponse(
    val data: List<CarDetail>,
    val success: Boolean,
    val message: String)
data class CarImageResponse(
    val data: List<CarImage>,
    val success: Boolean,
    val message: String
)

data class ColorResponse(
    val data: List<Color>,
    val success: Boolean,
    val message: String
)

data class BrandResponse(
    val data: List<Brand>,
    val success: Boolean,
    val message: String
)
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

data class LoginResponse(
    val token: String,
    val expiration: String
)
