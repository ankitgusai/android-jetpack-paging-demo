package com.ankit.nasaimages.networking.models

/**
 * API response model for /asset endpoint
 */
data class ImageAssetResponse(val collection: AssetCollection)

data class AssetCollection(val items: List<AssetItem>)

data class AssetItem(val href: String)