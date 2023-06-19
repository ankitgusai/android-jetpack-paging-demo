package com.ankit.workdaytest.networking.models

data class ImageAssetResponse(val collection: AssetCollection)

data class AssetCollection(val items: List<AssetItem>)

data class AssetItem(val href: String)