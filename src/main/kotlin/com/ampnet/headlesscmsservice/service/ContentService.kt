package com.ampnet.headlesscmsservice.service

import com.ampnet.headlesscmsservice.service.pojo.ContentListResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentResponse
import com.ampnet.headlesscmsservice.service.pojo.ContentUpdateServiceRequest

interface ContentService {
    fun findByCoop(coop: String, key: String?, language: String?): ContentListResponse
    fun updateContent(request: ContentUpdateServiceRequest): ContentResponse
    fun deleteContent(coop: String, key: String, language: String)
}
