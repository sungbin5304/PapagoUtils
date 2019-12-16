package com.sungbin.fake.nusty.tynus.utils

import com.sungbin.fake.nusty.tynus.utils.Utils.readData
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

@Suppress("NonAsciiCharacters")
object Papago {
    private fun getLanguageCode(text: String?): String? {
        return try {
            val id = "rGknnSNS3p_EfppfXh9z"
            val secret = "xPW2BCiXtp"
            if (id === "null" || secret === "null") return null
            val query = URLEncoder.encode(text, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/papago/detectLangs"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("X-Naver-Client-Id", id)
            con.setRequestProperty("X-Naver-Client-Secret", secret)
            val postParams = "query=$query"
            con.doOutput = true
            val wr = DataOutputStream(con.outputStream)
            wr.writeBytes(postParams)
            wr.flush()
            wr.close()
            val responseCode = con.responseCode
            val br: BufferedReader
            br = if (responseCode == 200) {
                BufferedReader(InputStreamReader(con.inputStream))
            } else {
                BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String?
            val response = StringBuffer()
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            br.close()
            response.toString().split("\":\"").toTypedArray()[1].split("\"\\}")
                .toTypedArray()[0]
        } catch (e: Exception) {
            e.message
        }
    }

    fun translate(target: String?, text: String?): String {
        return try {
            val sourceLang = getLanguageCode(text) ?: return "어플 설정에서 API 설정을 해 주세요."
            val targetCode = try {
                Language.valueOf(target!!).value
            } catch (e: Exception) {
                return "타겟 언어가 잘못됬습니다.\n지원하는 언어 : " + Language.values().contentToString()
            }
            val id = "rGknnSNS3p_EfppfXh9z"
            val secret = "xPW2BCiXtp"
            val query = URLEncoder.encode(text, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/papago/n2mt"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("X-Naver-Client-Id", id)
            con.setRequestProperty("X-Naver-Client-Secret", secret)
            val postParams =
                "source=$sourceLang&target=$targetCode&text=$query"
            con.doOutput = true
            val wr = DataOutputStream(con.outputStream)
            wr.writeBytes(postParams)
            wr.flush()
            wr.close()
            val responseCode = con.responseCode
            val br: BufferedReader
            br = if (responseCode == 200) {
                BufferedReader(InputStreamReader(con.inputStream))
            } else {
                BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String?
            val response = StringBuffer()
            while (br.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            br.close()
            val result = response.toString()
            if (result.contains("translatedText")) {
                result.split("translatedText\":\"").toTypedArray()[1]
                    .split("\"\\}\\}\\}").toTypedArray()[0]
            } else {
                "번역중 오류 발생!\n\n" + result.split("errorMessage\":\"").toTypedArray()[1].split(
                    "\",\"errorCode"
                ).toTypedArray()[0]
            }
        } catch (e: Exception) {
            "번역중 오류 발생!\n\n" + e.message
        }
    }

    private enum class Language(var value: String) {
        한글("ko"), 한국어("ko"), 영어("en"), 중국어간체("zh-CN"), 중국어번체("zh-TW"), 스페인어("es"), 프랑스어("fr"), 베트남어(
            "vi"
        ),
        태국어("th"), 인도네시아어("id");
    }
}