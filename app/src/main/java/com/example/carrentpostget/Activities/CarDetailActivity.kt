package com.example.carrentpostget.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.carrentpostget.Model.Car
import com.example.carrentpostget.Model.CarDetail
import com.example.carrentpostget.Model.CarImage
import com.example.carrentpostget.R
import com.example.carrentpostget.api.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarDetailActivity : AppCompatActivity() {

    private lateinit var brandTextView: TextView
    private lateinit var colorTextView: TextView
    private lateinit var modelYearTextView: TextView
    private lateinit var dailyPriceTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var deleteButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var imageView: ImageView

    private val retrofitClient = RetrofitClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_detail)
        enableEdgeToEdge()
        brandTextView = findViewById(R.id.marka)
        colorTextView = findViewById(R.id.renk)
        modelYearTextView = findViewById(R.id.modelYear)
        dailyPriceTextView = findViewById(R.id.gunlukUcret)
        descriptionTextView = findViewById(R.id.aciklama)
        deleteButton = findViewById(R.id.deleteBtn)
        selectImageButton = findViewById(R.id.selectImageButton)
        imageView = findViewById(R.id.imageView)
        val carId = intent.getIntExtra("CAR_ID", -1)
        if (carId != -1) {
            lifecycleScope.launch {
                try {
                    val response = retrofitClient.getCarDetail(carId)

                    if (response.isSuccessful) {
                        val carDetailResponse = response.body()

                        carDetailResponse?.let {
                            val carDetail = it.data.find { car -> car.id == carId }

                            carDetail?.let { detail ->
                                brandTextView.text = detail.brandName ?: "Bilinmiyor"
                                colorTextView.text = detail.colorName ?: "Bilinmiyor"
                                modelYearTextView.text = detail.modelYear ?: "Bilinmiyor"
                                dailyPriceTextView.text = "${detail.dailyPrice} TL"
                                descriptionTextView.text = detail.description ?: "Açıklama yok"
                                loadImage(carId)
                                deleteButton.setOnClickListener {
                                    lifecycleScope.launch {
                                        deleteCar(detail)
                                    }
                                }
                                selectImageButton.setOnClickListener {
                                    selectImage()
                                }
                            } ?: run {
                                Toast.makeText(this@CarDetailActivity, "Bu ID ile araba bulunamadı.", Toast.LENGTH_LONG).show()
                            }
                        } ?: run {
                            Toast.makeText(this@CarDetailActivity, "Veriler alınamadı.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@CarDetailActivity, "Bir hata oluştu: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CarDetailActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun deleteCar(carDetail: CarDetail) {
        try {
            val brandResponse = retrofitClient.getBrands()
            val colorResponse = retrofitClient.getColors()
            val imageResponse = retrofitClient.getCarImageId(carDetail.id)
            val brandId = brandResponse.body()?.data?.find { it.brandName == carDetail.brandName }?.brandId ?: 0
            val colorId = colorResponse.body()?.data?.find { it.colorName == carDetail.colorName }?.colorId ?: 0
            val carImages = imageResponse.body()?.data?.filter { it.carId == carDetail.id } ?: emptyList()
            for (carImage in carImages) {
                val deleteImageRequest = CarImage(
                    carImageId = carImage.carImageId,
                    carId = carImage.carId,
                    imagePath = carImage.imagePath,
                    imageDate = carImage.imageDate
                )
                val deleteImageResponse = retrofitClient.deleteCarImage(deleteImageRequest)
                if (!deleteImageResponse.isSuccessful) {
                    Toast.makeText(this, "Araba resmi silinemedi: ${deleteImageResponse.message()}", Toast.LENGTH_LONG).show()
                    return
                }
            }
            val car = Car(
                carId = carDetail.id,
                brandId = brandId,
                colorId = colorId,
                modelYear = carDetail.modelYear,
                dailyPrice = carDetail.dailyPrice,
                description = carDetail.description
            )
            val deleteResponse = retrofitClient.deleteCar(car)
            if (deleteResponse.isSuccessful) {
                Toast.makeText(this, "Araba başarıyla silindi.", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Silme işlemi başarısız oldu: ${deleteResponse.message()}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    private fun loadImage(carId: Int) {
        lifecycleScope.launch {
            try {
                val response = retrofitClient.getImageUrl(carId)
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.imageUrl
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@CarDetailActivity)
                            .load(imageUrl)
                            .apply(RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                            .fitCenter()
                            .into(imageView)
                    } //else {
                        //Toast.makeText(this@CarDetailActivity, "Resim bulunamadı", Toast.LENGTH_SHORT).show()
                    //}
                } else {
                    Toast.makeText(this@CarDetailActivity, "Bir hata oluştu: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarDetailActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                val carId = intent.getIntExtra("CAR_ID", -1)
                if (carId != -1) {
                    uploadImage(carId, selectedImageUri)
                }
            }
        }
    }
    private fun uploadImage(carId: Int, imageUri: Uri) {
        val file = getFileFromUri(imageUri)
        if (file == null) {
            Toast.makeText(this, "Resim dosyası alınamadı", Toast.LENGTH_SHORT).show()
            return
        }
        val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val carIdBody = carId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val imageDateBody = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePath = file.absolutePath.toRequestBody("text/plain".toMediaTypeOrNull())
        val carImageId = "0".toRequestBody("text/plain".toMediaTypeOrNull())
        lifecycleScope.launch {
            try {
                val response = retrofitClient.uploadCarImage(carIdBody, imageDateBody, imagePath, carImageId, body)
                if (response.isSuccessful) {
                    Toast.makeText(this@CarDetailActivity, "Resim başarıyla yüklendi.", Toast.LENGTH_LONG).show()
                    resetPage()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@CarDetailActivity, "Yükleme işlemi başarısız: ${response.message()}\n$errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarDetailActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun getFileFromUri(uri: Uri): File? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = getFileName(uri)
            val tempFile = File(cacheDir, fileName)
            val outputStream = FileOutputStream(tempFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "temp_image.jpg"
    }
    private fun resetPage() {
        finish()
        startActivity(intent)
    }
    companion object {
        private const val IMAGE_PICK_REQUEST = 1001
    }
}