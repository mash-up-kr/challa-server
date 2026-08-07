package com.challa.web.photo.dto

data class PhotosEnvelope<T : Any>(val photos: List<T>)
