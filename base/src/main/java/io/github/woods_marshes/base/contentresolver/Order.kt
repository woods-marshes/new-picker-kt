package io.github.woods_marshes.base.contentresolver

enum class Order(val sqlKeyword: String) {
    Ascending(sqlKeyword = "ASC"),
    Descending(sqlKeyword = "DESC")
}