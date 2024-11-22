package de.dojodev.royalrangermanager

import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.DBHelper
import javafx.application.Application
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        DBHelper.initBatis()

        val resource = FXHelper.getBundle()
        FXHelper.loadFXML("main.fxml", resource.getString("name"), 600.0, 400.0, stage)
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}