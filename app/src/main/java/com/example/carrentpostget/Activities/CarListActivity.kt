package com.example.carrentpostget.Activities
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carrentpostget.Adapter.CarAdapter
import com.example.carrentpostget.R
import com.example.carrentpostget.api.RetrofitClient
import kotlinx.coroutines.launch
class CarListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var ekleBtn: Button

    private val ADD_CAR_REQUEST_CODE = 1
    private val DELETE_CAR_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_list)
        enableEdgeToEdge()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ekleBtn = findViewById(R.id.ekleBtn)

        ekleBtn.setOnClickListener {
            val intent = Intent(this, PostCarActivity::class.java)
            startActivityForResult(intent, ADD_CAR_REQUEST_CODE)
        }

        fetchCarsAndUpdateRecyclerView()
    }

    private fun fetchCarsAndUpdateRecyclerView() {
        lifecycleScope.launch {
            try {
                val carResponse = RetrofitClient.instance.getCars()
                val brandResponse = RetrofitClient.instance.getBrands()
                val colorResponse = RetrofitClient.instance.getColors()

                if (carResponse.isSuccessful && brandResponse.isSuccessful && colorResponse.isSuccessful) {
                    val cars = carResponse.body()?.data ?: emptyList()
                    val brands = brandResponse.body()?.data ?: emptyList()
                    val colors = colorResponse.body()?.data ?: emptyList()

                    carAdapter = CarAdapter(cars, brands, colors) { car ->
                        val intent = Intent(this@CarListActivity, CarDetailActivity::class.java)
                        intent.putExtra("CAR_ID", car.carId)
                        startActivityForResult(intent, DELETE_CAR_REQUEST_CODE)
                    }
                    recyclerView.adapter = carAdapter
                } else {
                    Toast.makeText(this@CarListActivity, "Veriler alınırken bir hata oluştu.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CarListActivity, "Bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_CAR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchCarsAndUpdateRecyclerView()
        }
    }
}