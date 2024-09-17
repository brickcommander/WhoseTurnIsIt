import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import android.util.Base64
import com.brickcommander.whoseturnisit.model.Person
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GitHubJsonHandler(
    private val githubToken: String,
    private val repoOwner: String,
    private val repoName: String,
    private val filePath: String
) {
    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()
    private val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/contents/$filePath"
    private var fileSha: String? = null  // Variable to store the SHA

    // Fetch JSON file from GitHub
    fun fetchJsonFromGitHub(callback: (List<Person>?) -> Unit) {
        val request = Request.Builder()
            .url(apiUrl)
            .header("Authorization", "token $githubToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        println("Failed to fetch JSON: ${response.message}")
                        callback(null)
                        return
                    }

                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
                        fileSha = fileInfo.sha  // Save the SHA
                        val jsonContent = String(Base64.decode(fileInfo.content, Base64.DEFAULT))
                        val personListType = object : TypeToken<List<Person>>() {}.type
                        val persons: List<Person> = gson.fromJson(jsonContent, personListType)
                        callback(persons)
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }

    // Update JSON file on GitHub
    fun updateJsonOnGitHub(persons: List<Person>, commitMessage: String) {
        val updatedJson = gson.toJson(persons)
        val encodedContent = Base64.encodeToString(updatedJson.toByteArray(), Base64.DEFAULT)

        val sha = fileSha ?: run {
            println("SHA is not available. Please fetch the file first.")
            return
        }

        val requestBody = """
            {
              "message": "$commitMessage",
              "content": "$encodedContent",
              "sha": "$sha"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(apiUrl)
            .header("Authorization", "token $githubToken")
            .put(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("Successfully updated the JSON file on GitHub.")
                } else {
                    println("Failed to update the JSON file: ${response.body?.string()}")
                }
            }
        })
    }

    // Helper data class to store GitHub file metadata response
    data class GitHubFileResponse(
        val content: String,
        val sha: String
    )
}
