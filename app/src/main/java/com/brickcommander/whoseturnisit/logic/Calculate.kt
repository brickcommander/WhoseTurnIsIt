package com.brickcommander.whoseturnisit.logic

import com.brickcommander.whoseturnisit.model.Person
import GitHubJsonHandler

class Calculate {
    // HardCoded, needs to be removed later
    private val githubToken = "<token>"
    private val repoOwner = "brickcommander"
    private val repoName = "Android-App"
    private val filePath = "data.json"

    // Create an instance of GitHubJsonHandler
    private val handler = GitHubJsonHandler(githubToken, repoOwner, repoName, filePath)

    fun main() {
        handler.fetchJsonFromGitHub { persons ->
            if (persons != null) {
                println("Fetched persons:")
                persons.forEach { person ->
                    println(person.toString())
                }

                // Example of modifying the list
                val updatedList = persons.toMutableList()
                persons.forEach { person ->
                    person.increaseScore(6)
                }

                persons.forEach { person ->
                    println(person.toString())
                }

                // Update the JSON file on GitHub
                handler.updateJsonOnGitHub(updatedList, "Updated JSON with new person")
            } else {
                println("Failed to fetch persons.")
            }
        }
    }
}


