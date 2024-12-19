package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.helper.FXHelper
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.ToolBar
import net.synedra.validatorfx.Validator
import org.controlsfx.glyphfont.FontAwesome
import java.net.URL
import java.util.*

abstract class SubController : Initializable {
    protected lateinit var mainController: MainController
    protected var pbMain: ProgressBar? = null
    private val buttons =  mutableListOf<Button>()
    private var toolMain: ToolBar? = null
    protected var lang: ResourceBundle? = null
    protected var validator = Validator()

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        this.lang = p1
    }

    abstract fun initControls()

    protected abstract fun initButtons()
    protected abstract fun initBindings()
    protected abstract fun initValidator()

    fun init(mainController: MainController) {
        this.mainController = mainController
        this.toolMain = this.mainController.toolMain
        this.pbMain = this.mainController.pbMain
    }

    fun initIsActive() {
        this.initButtons()
        this.initBindings()
        this.validator.clear()
        this.initValidator()
    }

    fun initIsInActive() {
        this.validator.clear()
        this.buttons.forEach { btn -> this.toolMain?.items?.removeIf { it.id == btn.id } }
    }

    protected fun addIconButton(glyph: FontAwesome.Glyph, action: () -> Unit): Button {
        val button = Button()
        button.id = UUID.randomUUID().toString()
        FXHelper.setIcon(button, glyph)
        button.setOnAction { action() }
        this.toolMain?.items?.add(button)
        this.buttons.add(button)
        return button
    }
}