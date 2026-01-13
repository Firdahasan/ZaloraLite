package com.zaloralite.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zaloralite.app.R
import com.zaloralite.app.model.Product

class CartAdapter(
    private val products: List<Product>,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProductCart)
        val name: TextView = view.findViewById(R.id.txtProductNameCart)
        val price: TextView = view.findViewById(R.id.txtProductPriceCart)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_wishlist, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = "Rp ${item.price}"
        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.img)

        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = products.size
}