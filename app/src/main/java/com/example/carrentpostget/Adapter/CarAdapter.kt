package com.example.carrentpostget.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carrentpostget.Model.Brand
import com.example.carrentpostget.Model.Car
import com.example.carrentpostget.Model.Color
import com.example.carrentpostget.R

class CarAdapter(
    private val carList: List<Car>,
    private val allBrands: List<Brand>,
    private val allColors: List<Color>,
    private val onDeleteCar: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandName: TextView = itemView.findViewById(R.id.tvBrandName)
        val colorName: TextView = itemView.findViewById(R.id.tvColorName)
        val modelYear: TextView = itemView.findViewById(R.id.tvModelYear)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        val brandName = allBrands.find { it.brandId == car.brandId }?.brandName ?: "Unknown"
        val colorName = allColors.find { it.colorId == car.colorId }?.colorName ?: "Unknown"

        holder.brandName.text = "Marka: $brandName"
        holder.colorName.text = "Renk: $colorName"
        holder.modelYear.text = "Yıl: ${car.modelYear}"

        holder.itemView.setOnClickListener {
            onDeleteCar(car)
        }
    }

    override fun getItemCount(): Int {
        return carList.size
    }
}