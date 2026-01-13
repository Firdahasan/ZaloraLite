package com.zaloralite.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String? = null,
    val name: String? = null,
    val brand: String? = null,
    val price: Long? = 0,
    val imageUrl: String? = null,
    val description: String? = null
) : Parcelable