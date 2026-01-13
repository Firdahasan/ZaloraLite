package com.zaloralite.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zaloralite.app.databinding.ActivityDetailBinding
import com.zaloralite.app.model.Product
import java.text.NumberFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menggunakan View Binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Menerima data produk dari Intent menggunakan Parcelable
        currentProduct = intent.getParcelableExtra<Product>("EXTRA_PRODUCT")

        // 2. Tampilkan data ke UI jika produk tidak null
        currentProduct?.let { product ->
            displayProductDetails(product)
        } ?: run {
            Toast.makeText(this, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
            finish() // Tutup activity jika data null
        }

        // 3. Setup Tombol Aksi
        binding.btnBack.setOnClickListener { finish() } // Kembali ke halaman sebelumnya

        binding.btnAddToCart.setOnClickListener {
            currentProduct?.let { addToFirebase("Cart", it) }
        }

        binding.btnDetailWishlist.setOnClickListener {
            currentProduct?.let { addToFirebase("Wishlist", it) }
        }
    }

    private fun displayProductDetails(product: Product) {
        binding.tvDetailBrand.text = product.brand
        binding.tvDetailName.text = product.name
        binding.tvDetailDesc.text = product.description ?: "Tidak ada deskripsi tersedia."

        // Format Rupiah
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        binding.tvDetailPrice.text = numberFormat.format(product.price).replace(",00", "")

        // Load gambar besar dengan Glide
        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(binding.imgDetailProduct)
    }

    // Fungsi reusable untuk menambah ke Cart atau Wishlist
    private fun addToFirebase(node: String, product: Product) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = product.id ?: return
        binding.btnAddToCart.isEnabled = false // Cegah klik ganda
        binding.btnAddToCart.text = "MEMPROSES..."

        FirebaseDatabase.getInstance().getReference(node)
            .child(user.uid)
            .child(productId)
            .setValue(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil ditambahkan ke $node", Toast.LENGTH_SHORT).show()
                resetButtonState()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                resetButtonState()
            }
    }

    private fun resetButtonState() {
        binding.btnAddToCart.isEnabled = true
        binding.btnAddToCart.text = "TAMBAH KE TAS"
    }
}