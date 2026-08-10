package com.challa.web.photo.dto

data class ListPhotosSliceResponse(val photos: List<ListPhotosResponse>, val hasNext: Boolean)
