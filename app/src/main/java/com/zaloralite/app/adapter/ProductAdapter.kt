package com.zaloralite.app.adapter

import android.view.LayoutInflater
import android.view.View // Pastikan android.view.View yang diimport
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide untuk memuat gambar
import com.zaloralite.app.R
import com.zaloralite.app.model.Product

class ProductAdapter(
    private val list: List<Product>,
    private val onClick: (Product) -> Unit,
    private val onWishlistClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val brand: TextView = view.findViewById(R.id.txtBrand)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val img: ImageView = view.findViewById(R.id.imgProduct)
        val btnWishlist: ImageView = view.findViewById(R.id.btnAddToWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.brand.text = item.brand ?: "ZALORA"
        holder.price.text = "Rp ${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.img)

        holder.itemView.setOnClickListener { onClick(item) }
        holder.btnWishlist.setOnClickListener { onWishlistClick(item) }
    }

    override fun getItemCount() = list.size
}