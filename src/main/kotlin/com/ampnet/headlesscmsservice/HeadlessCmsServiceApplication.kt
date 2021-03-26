package com.ampnet.headlesscmsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HeadlessCmsServiceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<HeadlessCmsServiceApplication>(*args)
}
