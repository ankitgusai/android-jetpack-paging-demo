package com.ankit.workdaytest.networking.models

// JSON response model for GSON
data class ImageSearchResponse(val collection: Collection)

data class Collection(val items: List<Items>, val metadata: Metadata, val links: List<Links>)

data class Items(val data: List<Data>, val link: List<Link>)

data class Data(val title: String, val data: String, val keywords: List<String>)

data class Link(val href: String)

data class Metadata(val total_hits: Int)

data class Links(val rel: String, val prompt: String, val href: String)

