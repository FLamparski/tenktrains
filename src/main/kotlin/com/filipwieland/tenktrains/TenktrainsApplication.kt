package com.filipwieland.tenktrains

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TenktrainsApplication

fun main(args: Array<String>) {
	runApplication<TenktrainsApplication>(*args)
}
