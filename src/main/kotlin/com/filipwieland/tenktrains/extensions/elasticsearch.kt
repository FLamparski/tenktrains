package com.filipwieland.tenktrains.extensions

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.util.ObjectBuilder

inline fun <reified TDocument: Any> ElasticsearchClient.search(
    noinline block: ((SearchRequest.Builder) -> ObjectBuilder<SearchRequest>)
): SearchResponse<TDocument> {
    return this.search(block, TDocument::class.java)
}
