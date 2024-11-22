package de.dojodev.royalrangermanager.controller

import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import java.net.URL
import java.util.*

abstract class SubController : Initializable {
    protected lateinit var mainController: MainController
    protected var pbMain: ProgressBar? = null
    protected var cmdNew: Button? = null
    protected var cmdEdit: Button? = null
    protected var cmdDelete: Button? = null
    protected var cmdSave: Button? = null
    protected var cmdCancel: Button? = null
    protected var lang: ResourceBundle? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        this.lang = p1
        this.init()
    }

    abstract fun initControls()

    abstract fun init()

    fun init(mainController: MainController) {
        this.mainController = mainController
        this.pbMain = this.mainController.pbMain
        this.cmdNew = this.mainController.cmdNew
        this.cmdEdit = this.mainController.cmdEdit
        this.cmdDelete = this.mainController.cmdDelete
        this.cmdCancel = this.mainController.cmdCancel
        this.cmdSave = this.mainController.cmdSave
    }
}