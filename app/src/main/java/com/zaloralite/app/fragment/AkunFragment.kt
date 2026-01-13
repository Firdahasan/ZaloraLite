package com.zaloralite.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zaloralite.app.LoginActivity
import com.zaloralite.app.databinding.FragmentAkunBinding

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            database = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            // Ambil data dari Realtime Database
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded) {
                        val name = snapshot.child("name").value.toString()
                        val email = snapshot.child("email").value.toString()

                        binding.tvUserName.text = name.uppercase()
                        binding.tvUserEmail.text = email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Logika Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}