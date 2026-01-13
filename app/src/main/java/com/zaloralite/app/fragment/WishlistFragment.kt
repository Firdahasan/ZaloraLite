package com.zaloralite.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zaloralite.app.adapter.CartAdapter
import com.zaloralite.app.databinding.FragmentWishlistBinding
import com.zaloralite.app.model.Product

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val wishlistItems = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvWishlist.layoutManager = LinearLayoutManager(context)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return

        db = FirebaseDatabase.getInstance().getReference("Wishlist").child(uid)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wishlistItems.clear()
                for (data in snapshot.children) {
                    val p = data.getValue(Product::class.java)
                    p?.let { wishlistItems.add(it) }
                }

                binding.rvWishlist.adapter = CartAdapter(wishlistItems) { product ->
                    db.child(product.id!!).removeValue().addOnSuccessListener {
                        if (isAdded) {
                            activity?.let {
                                Toast.makeText(it, "Barang dihapus", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}