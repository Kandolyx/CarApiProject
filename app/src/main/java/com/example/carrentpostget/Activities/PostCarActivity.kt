package com.example.carrentpostget.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carrentpostget.Model.Brand
import com.example.carrentpostget.Model.Car
import com.example.carrentpostget.Model.Color
import com.example.carrentpostget.R
import com.example.carrentpostget.api.RetrofitClient
import kotlinx.coroutines.launch

class PostCarActivity : AppCompatActivity() {

    private lateinit var spinnerBrand: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var etModelYear: EditText
    private lateinit var etDailyPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSubmit: Button

    private lateinit var brands: List<Brand>
    private lateinit var colors: List<Color>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_car)
        enableEdgeToEdge()

        spinnerBrand = findViewById(R.id.spinnerBrand)
        spinnerColor = findViewById(R.id.spinnerColor)
        etModelYear = findViewById(R.id.etModelYear)
        etDailyPrice = findViewById(R.id.etDailyPrice)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmit)

        loadBrandAndColorData()

        btnSubmit.setOnClickListener {
            val selectedBrandName = spinnerBrand.selectedItem as String
            val selectedColorName = spinnerColor.selectedItem as String

            val selectedBrand = brands.first { it.brandName == selectedBrandName }
            val selectedColor = colors.first { it.colorName == selectedColorName }

            val modelYear = etModelYear.text.toString()
            val dailyPrice = etDailyPrice.text.toString().toInt()
            val description = etDescription.text.toString()

            lifecycleScope.launch {
                try {
                    val car = Car(0, selectedBrand.brandId, selectedColor.colorId, modelYear, dailyPrice, description)
                    val carResponse = RetrofitClient.instance.postCar(car)

                    if (carResponse.isSuccessful) {
                        Toast.makeText(this@PostCarActivity, "Araç başarıyla eklendi!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@PostCarActivity, CarListActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = carResponse.errorBody()?.string() ?: "Hata açıklaması mevcut değil"
                        val statusCode = carResponse.code()
                        Toast.makeText(
                            this@PostCarActivity,
                            "Araç eklenirken bir hata oluştu: HTTP $statusCode. Yanıt: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PostCarActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadBrandAndColorData() {
        lifecycleScope.launch {
            try {
                val brandResponse = RetrofitClient.instance.getBrands()
                val colorResponse = RetrofitClient.instance.getColors()

                if (brandResponse.isSuccessful && colorResponse.isSuccessful) {
                    brands = brandResponse.body()?.data ?: emptyList()
                    colors = colorResponse.body()?.data ?: emptyList()

                    val brandAdapter = ArrayAdapter(this@PostCarActivity, android.R.layout.simple_spinner_item, brands.map { it.brandName })
                    brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerBrand.adapter = brandAdapter

                    val colorAdapter = ArrayAdapter(this@PostCarActivity, android.R.layout.simple_spinner_item, colors.map { it.colorName })
                    colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerColor.adapter = colorAdapter

                } else {
                    Toast.makeText(this@PostCarActivity, "Brand veya Color verileri alınırken bir hata oluştu.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@PostCarActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}