package com.zaloralite.app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zaloralite.app.DetailActivity
import com.zaloralite.app.R
import com.zaloralite.app.adapter.ProductAdapter
import com.zaloralite.app.model.Product

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var db: DatabaseReference
    private val productList = mutableListOf<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        rv.layoutManager = GridLayoutManager(context, 2)

        db = FirebaseDatabase.getInstance().getReference("Products")

        // MENGGUNAKAN SingleValueEvent agar lebih ringan dan tidak loop
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                productList.clear()
                for (data in snapshot.children) {
                    val p = data.getValue(Product::class.java)
                    p?.let { productList.add(it) }
                }

                val adapter = ProductAdapter(productList,
                    { product ->
                        val intent = Intent(requireContext(), DetailActivity::class.java)
                        intent.putExtra("EXTRA_PRODUCT", product) // Mengirim objek product via Parcelable
                        startActivity(intent)
                    },
                    // Callback 2: Klik Hati Kecil di Home -> Langsung Wishlist (seperti logika sebelumnya)
                    { product -> addToWishlist(product) }
                )
                rv.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addToCart(p: Product) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val productId = p.id ?: return

        FirebaseDatabase.getInstance().getReference("Cart").child(uid).child(productId).setValue(p)
            .addOnSuccessListener {
                if (isAdded) {
                    activity?.let {
                        Toast.makeText(it, "${p.brand} Masuk ke Tas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun addToWishlist(p: Product) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val productId = p.id ?: return

        FirebaseDatabase.getInstance().getReference("Wishlist").child(uid).child(productId).setValue(p)
            .addOnSuccessListener {
                if (isAdded) {
                    activity?.let {
                        Toast.makeText(it, "${p.name} ditambah ke Wishlist", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}