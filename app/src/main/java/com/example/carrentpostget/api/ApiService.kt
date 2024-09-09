package com.example.carrentpostget.api

import com.example.carrentpostget.Model.Brand
import com.example.carrentpostget.Model.BrandResponse
import com.example.carrentpostget.Model.Car
import com.example.carrentpostget.Model.CarDetailResponse
import com.example.carrentpostget.Model.CarImage
import com.example.carrentpostget.Model.CarImageResponse
import com.example.carrentpostget.Model.CarResponse
import com.example.carrentpostget.Model.Color
import com.example.carrentpostget.Model.ColorResponse
import com.example.carrentpostget.Model.ImageUrlResponse
import com.example.carrentpostget.Model.LoginRequest
import com.example.carrentpostget.Model.LoginResponse
import com.example.carrentpostget.Model.RegisterRequest
import com.example.carrentpostget.Model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("api/Cars/getall")
    suspend fun getCars(): Response<CarResponse>
    @GET("api/Cars/getbyid")
    suspend fun getCarById(@Query("carId") carId: Int): Response<CarResponse>
    @GET("api/Brands/getbyid")
    suspend fun getBrandById(@Query("brandId") brandId: Int): Response<BrandResponse>
    @GET("api/Colors/getbyid")
    suspend fun getColorById(@Query("colorId") colorId: Int): Response<ColorResponse>
    @GET("api/Cars/getcardetail")
    suspend fun getCarDetail(@Query("carId") carId: Int): Response<CarDetailResponse>
    @GET("api/Cars/getimageid")
    suspend fun getCarImageId(@Query("carId") carId: Int): Response<CarImageResponse>
    @GET("api/CarImages/getimageurl")
    suspend fun getImageUrl(@Query("carId") carId: Int): Response<ImageUrlResponse>

    @GET("api/Colors/getall")
    suspend fun getColors(): Response<ColorResponse>

    @GET("api/Brands/getall")
    suspend fun getBrands(): Response<BrandResponse>

    @POST("api/Cars/add")
    suspend fun postCar(@Body car: Car): Response<Car>

    @POST("api/Colors/add")
    suspend fun postColor(@Body color: Color): Response<Color>

    @POST("api/Brands/add")
    suspend fun postBrand(@Body brand: Brand): Response<Brand>
    @Multipart
    @POST("api/CarImages/add")
    suspend fun uploadCarImage(
        @Part("CarId") carId: RequestBody,
        @Part("ImageDate") imageDate: RequestBody,
        @Part("ImagePath") imagePath: RequestBody,
        @Part("CarImageId") carImageId: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Response<CarImage>

    @POST("api/Cars/delete")
    suspend fun deleteCar(@Body car: Car): Response<CarResponse>
    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @POST("api/CarImages/delete")
    suspend fun deleteCarImage(@Body carImage: CarImage):Response<CarImageResponse>
}