package com.example.retrofit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.example.retrofit.API.Product
import com.example.retrofit.databinding.ProductDesignBinding

class ProductAdapter(private val productList:List<Product>) :RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    inner class ProductViewHolder(val binding:ProductDesignBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductDesignBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = productList[position]
        holder.apply {
            binding.productName.text = item.title
            binding.price.text = ("$${item.price}")
            binding.rating.text = "Rating ${item.rating}"
            binding.stock.text = "Stock ${item.stock}"
            binding.productImg.load(item.thumbnail){
                crossfade(true)
            }
            binding.id.text = item.id.toString()
        }
    }
}