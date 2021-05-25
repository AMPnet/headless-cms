package com.ampnet.headlesscmsservice.service

import com.ampnet.headlesscmsservice.service.pojo.TextListResponse
import com.ampnet.headlesscmsservice.service.pojo.TextResponse
import com.ampnet.headlesscmsservice.service.pojo.TextUpdateServiceRequest

interface TextService {
    fun findByCoop(coop: String, key: String?, languge: String?): TextListResponse
    fun updateText(request: TextUpdateServiceRequest): TextResponse
}
