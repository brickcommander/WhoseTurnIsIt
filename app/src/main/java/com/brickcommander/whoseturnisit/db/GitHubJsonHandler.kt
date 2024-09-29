import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.time.LocalDate
import android.util.Base64
import android.util.Log
import com.brickcommander.whoseturnisit.BuildConfig
import com.brickcommander.whoseturnisit.logic.Calculate
import com.brickcommander.whoseturnisit.model.Person
import com.brickcommander.whoseturnisit.model.Work
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime

class GitHubJsonHandler() {
    private val githubToken = BuildConfig.GITHUB_TOKEN
    private val repoOwner = BuildConfig.REPO_OWNER
    private val repoName = BuildConfig.REPO_NAME
    private val filePersonPath = BuildConfig.FILE_PERSONS_PATH
    private val fileWorkPath = BuildConfig.FILE_WORK_PATH
    private val fileCacheWorkPath = BuildConfig.FILE_CACHEWORK_PATH

    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()

    private val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/contents/"
    private var fileSha: String? = null  // Variable to store the SHA

    companion object {
        const val TAG = "GitHubJsonHandler"
    }

    // Fetch JSON file from GitHub
    private fun fetchJsonFromGitHub(filePath: String): String? {
        Log.i(TAG, "fetchJsonFromGitHub : ${filePath} : START")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .build()

        Log.i(TAG, "calling Read API : ${filePath} : ${request.url}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (!response.isSuccessful) {
                println("Failed to fetch JSON : ${filePath} : ${response.message}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
            fileSha = fileInfo.sha  // Save the SHA

            val jsonContent = String(Base64.decode(fileInfo.content, Base64.DEFAULT))
            Log.i(TAG, "fetchJsonFromGitHub : ${filePath} : ${jsonContent}")
            return jsonContent
        } catch (e: IOException) {
            Log.i(TAG, "fetchJsonFromGitHub: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return null
        }
    }

    // Get File SHA
    private fun getFileSha(filePath: String): String? {
        Log.i(TAG, "getFileSha : ${filePath} : START")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .build()

        Log.i(TAG, "calling Read API for SHA : ${filePath} : ${request.url}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (!response.isSuccessful) {
                Log.i(TAG,"Failed to fetch JSON : ${filePath}: ${response.message}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val fileInfo = gson.fromJson(responseBody, GitHubFileResponse::class.java)
            return fileInfo.sha  // Save the SHA
        } catch (e: IOException) {
            Log.i(TAG, "getFileSha: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return null
        }
    }

    // Update JSON file on GitHub
    private fun updateJsonOnGitHub(updatedJson: String, filePath: String, commitMessage: String): Boolean {
        val encodedContent = Base64.encodeToString(updatedJson.toByteArray(), Base64.NO_WRAP)

        val sha = getFileSha(filePath) ?: run {
            Log.i(TAG, "Get SHA API Failed : ${filePath}. Unable to update the JSON file.")
            return false
        }

        Log.i(TAG, "updateJsonOnGitHub : ${filePath} : ${updatedJson} : ${sha} : ${commitMessage}")

        val requestBody = """
            {
              "message": "$commitMessage",
              "content": "$encodedContent",
              "sha": "$sha"
            }
        """.trimIndent()

        Log.i(TAG, "request body: $requestBody")

        val request = Request.Builder()
            .url(apiUrl+filePath)
            .header("Authorization", githubToken)
            .put(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        Log.i(TAG, "calling Update API : ${filePath} : ${request}")

        try {
            val response = client.newCall(request).execute() // Synchronous call
            if (response.isSuccessful) {
                Log.i(TAG, "Successfully updated the JSON file on GitHub : ${filePath}.")
                return true
            } else {
                Log.i(TAG, "Failed to update the JSON file : ${filePath} : ${response.body?.string()}")
                return false
            }
        } catch (e: IOException) {
            Log.i(TAG, "updateJsonOnGitHub: Exception Occured : REQ=${request} : FILEPATH=${filePath}")
            e.printStackTrace()
            return false
        }
    }

    fun getCacheWorkList(): MutableList<Work>? {
        try {
            Log.i(TAG, "getCacheWorkList")
            val jsonContent = fetchJsonFromGitHub(fileCacheWorkPath)
            val workListType = object : TypeToken<MutableList<Work>>() {}.type
            return gson.fromJson(jsonContent, workListType)
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : getCacheWorkList : ${e.message}")
            return mutableListOf()
        }
    }

    fun updateCacheWorkList(cacheWorkList: MutableList<Work>): Boolean {
        try {
            Log.i(TAG, "updateCacheWorkList : cacheWorkList=$cacheWorkList")
            val updatedJson = gson.toJson(cacheWorkList)
            return updateJsonOnGitHub(updatedJson, fileCacheWorkPath, "Update CacheWork : ${LocalDateTime.now()}")
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : updateCacheWorkList : ${e.message}")
            return false
        }
    }

    fun getWorkHistory(): MutableList<Work>? {
        try {
            Log.i(TAG, "getWorkHistory")
            val jsonContent = fetchJsonFromGitHub(fileWorkPath)
            val workListType = object : TypeToken<MutableList<Work>>() {}.type
            return gson.fromJson(jsonContent, workListType)
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : getWorkHistory : ${e.message}")
            return mutableListOf()
        }
    }

    fun updateWorkHistory(history: MutableList<Work>): Boolean {
        try {
            Log.i(TAG, "updateWorkHistory : history=$history")
            val updatedJson = gson.toJson(history)
            return updateJsonOnGitHub(updatedJson, fileWorkPath, "Update History : ${LocalDateTime.now()}")
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : updateWorkHistory : ${e.message}")
            return false
        }
    }

    fun getPersonList(): MutableList<Person>? {
        try {
            Log.i(TAG, "getPersonList")
            val jsonContent = fetchJsonFromGitHub(filePersonPath)
            val personListType = object : TypeToken<MutableList<Person>>() {}.type
            return gson.fromJson(jsonContent, personListType)
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : getPersonList : ${e.message}")
            return mutableListOf()
        }
    }

    fun updatePersonList(personList: MutableList<Person>): Boolean {
        try {
            Log.i(TAG, "updatePersonList : personList=$personList")
            val updatedJson = gson.toJson(personList)
            return updateJsonOnGitHub(updatedJson, filePersonPath, "Update personList : ${LocalDateTime.now()}")
        } catch (e: Exception) {
            Log.i(TAG, "Exception Occured : updatePersonList : ${e.message}")
            return false
        }
    }


    // Helper data class to store GitHub file metadata response
    private data class GitHubFileResponse(
        val content: String,
        val sha: String
    )
}
