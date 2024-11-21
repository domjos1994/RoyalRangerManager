package de.dojodev.royalrangermanager

import de.dojodev.royalrangermanager.db.mapper.PersonMapper
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.InitializationHelper
import javafx.application.Application
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        InitializationHelper.initBatis()
        //val mapper = InitializationHelper.getSession()?.getMapper(PersonMapper::class.java)
        //val items = mapper?.getPersons()

        val resource = FXHelper.getBundle()
        FXHelper.loadFXML("main.fxml", resource.getString("name"), 600.0, 400.0, stage)
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}