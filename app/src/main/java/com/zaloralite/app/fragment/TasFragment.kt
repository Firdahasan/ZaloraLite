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
import com.zaloralite.app.R
import com.zaloralite.app.adapter.CartAdapter
import com.zaloralite.app.databinding.FragmentTasBinding
import com.zaloralite.app.model.Product
import java.text.NumberFormat
import java.util.*

class TasFragment : Fragment(R.layout.fragment_tas) {

    private var _binding: FragmentTasBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference
    private var totalPrice: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCart.layoutManager = LinearLayoutManager(context)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db = FirebaseDatabase.getInstance().getReference("Cart").child(uid)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<Product>()
                totalPrice = 0
                for (data in snapshot.children) {
                    val p = data.getValue(Product::class.java)
                    p?.let {
                        cartItems.add(it)
                        totalPrice += it.price ?: 0
                    }
                }

                val formattedPrice = formatRupiah(totalPrice)
                binding.txtTotalHarga.text = getString(R.string.total_format, formattedPrice)

                binding.rvCart.adapter = CartAdapter(cartItems) { product ->
                    db.child(product.id!!).removeValue().addOnSuccessListener {
                        if (isAdded) {
                            activity?.let {
                                Toast.makeText(it, "Barang dihapus", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.btnCheckout.setOnClickListener {
            if (totalPrice > 0) {
                Toast.makeText(context, "Pesanan Berhasil Dibuat!", Toast.LENGTH_LONG).show()
                db.removeValue()
            } else {
                Toast.makeText(context, "Tas belanja masih kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatRupiah(number: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp ").replace(",00", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}