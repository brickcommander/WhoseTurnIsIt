package com.brickcommander.whoseturnisit.logic

import com.brickcommander.whoseturnisit.model.Person
import GitHubJsonHandler
import android.util.Log
import com.brickcommander.whoseturnisit.BuildConfig

class Calculate {

    private val githubToken = BuildConfig.GITHUB_TOKEN
    private val repoOwner = BuildConfig.REPO_OWNER
    private val repoName = BuildConfig.REPO_NAME
    private val filePersonsPath = BuildConfig.FILE_PERSONS_PATH
    private val fileWorkPath = BuildConfig.FILE_WORK_PATH

    // Create an instance of GitHubJsonHandler
    private val handler =
        GitHubJsonHandler(githubToken, repoOwner, repoName, filePersonsPath, fileWorkPath)

    companion object {
        const val TAG = "Calculate"
    }

    // Synchronous function to fetch JSON
    fun updateToLatestDB() {
        Log.i(TAG, "updateToLatestDB()")

        // Fetch data synchronously
        handler.fetchJsonFromGitHub()
    }


}
