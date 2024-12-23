package com.example.retrofit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit.API.ApiInterface
import com.example.retrofit.API.Product
import com.example.retrofit.API.ProductsData
import com.example.retrofit.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter : ProductAdapter
    private val productList = mutableListOf<Product>()
    private var limit = 30
    private var skip = 0
    private var totalProduct = 0
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ProductAdapter(productList)
        binding.productRv.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.productRv.adapter = adapter

        fetchProducts()
        binding.productRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    if (skip < totalProduct) {
                        showLoading()
                        fetchProducts() // Load More Data
                    }
                }
            }
        })

    }

    private fun fetchProducts() {
        if (isLoading) return
        isLoading = true
        showLoading()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getProducts(limit,skip)
        retrofitData.enqueue(object : Callback<ProductsData?> {
            @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
            override fun onResponse(p0: Call<ProductsData?>, response: Response<ProductsData?>) {
                hideLoading()
                val responseBody = response.body()
                totalProduct = responseBody?.total?:0
                val newProducts = responseBody?.products?: emptyList()
                productList.addAll(newProducts)
                adapter.notifyDataSetChanged()
                skip += limit
                isLoading = false
            }

            override fun onFailure(p0: Call<ProductsData?>, p1: Throwable) {
                Toast.makeText(this@MainActivity, p1.localizedMessage, Toast.LENGTH_SHORT).show()
                isLoading = false
                hideLoading()
            }
        })

    }
    private fun showLoading(){
        binding.progressBar.isVisible = true
        binding.loadingMoreTv.isVisible = true
    }
    private fun hideLoading(){
        binding.progressBar.isVisible = false
        binding.loadingMoreTv.isVisible = false
    }
}