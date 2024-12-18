package de.dojodev.royalrangermanager.controller

import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import javafx.scene.control.ToolBar
import org.controlsfx.glyphfont.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon
import java.net.URL
import java.util.*

abstract class SubController : Initializable {
    protected lateinit var mainController: MainController
    protected var pbMain: ProgressBar? = null
    private val buttons =  mutableListOf<Button>()
    private var toolMain: ToolBar? = null
    protected var lang: ResourceBundle? = null

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        this.lang = p1
    }

    abstract fun initControls()

    protected abstract fun initButtons()
    protected abstract fun initBindings()

    fun init(mainController: MainController) {
        this.mainController = mainController
        this.toolMain = this.mainController.toolMain
        this.pbMain = this.mainController.pbMain
    }

    fun initIsActive() {
        this.initButtons()
        this.initBindings()
    }

    fun initIsInActive() {
        this.buttons.forEach { btn -> this.toolMain?.items?.removeIf { it.id == btn.id } }
    }

    protected fun addIconButton(glyph: FontAwesome.Glyph, action: () -> Unit): Button {
        val button = Button()
        button.id = UUID.randomUUID().toString()
        this.setIcon(button, glyph)
        button.setOnAction { action() }
        this.toolMain?.items?.add(button)
        this.buttons.add(button)
        return button
    }

    private fun setIcon(cmd: Button, glyph: FontAwesome.Glyph) {
        val icon = FontIcon("fa-${glyph.name.lowercase()}")
        icon.iconSize = 16
        cmd.graphic = icon
    }
}