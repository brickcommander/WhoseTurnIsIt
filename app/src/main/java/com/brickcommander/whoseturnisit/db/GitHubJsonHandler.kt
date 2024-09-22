import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import android.util.Base64
import android.util.Log
import com.brickcommander.whoseturnisit.data.SharedData
import com.brickcommander.whoseturnisit.model.Person
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GitHubJsonHandler(
    private val githubToken: String,
    private val repoOwner: String,
    private val repoName: String,
    private val filePersonsPath: String,
    private val fileWorkPath: String
) {
    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()
    private val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/contents/$filePersonsPath"
    private var fileSha: String? = null  // Variable to store the SHA

    companion object {
        const val TAG = "GitHubJsonHandler"
    }

    // Fetch JSON file from GitHub
    fun fetchJsonFromGitHub(){
        val request = Request.Builder()
            .url(apiUrl)
            .header("Authorization", githubToken)
            .build()

        Log.i(TAG, "calling Read API: ${request.toString()}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (!response.isSuccessful) {
                println("Failed to fetch JSON: ${response.message}")
                return
            }

            val responseBody = response.body?.string() ?: return
            val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
            fileSha = fileInfo.sha  // Save the SHA
            val jsonContent = String(Base64.decode(fileInfo.content, Base64.DEFAULT))
            val personListType = object : TypeToken<List<Person>>() {}.type
            Log.i(TAG, "fetchJsonFromGitHub: SUCCESS")
            SharedData.personsList = gson.fromJson(jsonContent, personListType)  // Return list of persons
        } catch (e: IOException) {
            Log.i(TAG, "fetchJsonFromGitHub: Exception Occured : REQ=${request}")
            e.printStackTrace()
        }
    }

    // Update JSON file on GitHub
    fun updateJsonOnGitHub(persons: List<Person>, commitMessage: String) {
        val updatedJson = gson.toJson(persons)
        val encodedContent = Base64.encodeToString(updatedJson.toByteArray(), Base64.NO_WRAP)

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

        Log.i(TAG, "request body: $requestBody")

        val request = Request.Builder()
            .url(apiUrl)
            .header("Authorization", githubToken)
            .put(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        Log.i(TAG, "calling Update API: ${request}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (response.isSuccessful) {
                println("Successfully updated the JSON file on GitHub.")
            } else {
                println("Failed to update the JSON file: ${response.body?.string()}")
            }
        } catch (e: IOException) {
            Log.i(TAG, "updateJsonOnGitHub: Exception Occured : REQ=${request}")
            e.printStackTrace()
        }
    }


    // Helper data class to store GitHub file metadata response
    data class GitHubFileResponse(
        val content: String,
        val sha: String
    )
}
