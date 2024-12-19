package de.dojodev.royalrangermanager.helper

import de.dojodev.royalrangermanager.controller.MainController
import de.dojodev.royalrangermanager.controller.SubController
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.controlsfx.control.Notifications
import org.controlsfx.glyphfont.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class FXHelper {

    companion object {
        private const val FXML_PATH = "/fxml/"
        private const val CSS_PATH = "/css/"
        private const val LANG_PATH = "/lang/"
        private const val ICON_PATH = "/icons/"

        private var bundle: ResourceBundle? = null
        private var stage: Stage = Stage()
        var logger = LoggerFactory.getLogger(FXHelper::class.java)
        private var success = ""
        private var successText = ""

        fun getStage(): Stage {
            return this.stage
        }

        fun loadFXML(name: String, title: String, width: Double, height: Double, stage: Stage = Stage(), css: String = "main.css", icon: String = "icon.png"): Stage {
            this.stage = stage
            val resource = FXHelper::class.java.getResource("$FXML_PATH$name")
            if(resource != null) {
                val loader = FXMLLoader(resource, getBundle())
                val scene = Scene(loader.load(), width, height)
                stage.scene = scene
                stage.title = title
                addStyleSheet(stage, css)
                addIcon(stage, icon)
                stage.show()
            }
            return stage
        }

        fun getFileChooser(create: Boolean, path: String = "", title: String = "", extensions: List<FileChooser.ExtensionFilter> = listOf()): String {
            val fc = FileChooser()
            fc.title = title
            if(path.isNotEmpty()) {
                fc.initialDirectory = File(path)
            }
            fc.extensionFilters.addAll(extensions)
            return if(create) {
                fc.showSaveDialog(this.stage)?.absolutePath ?: ""
            } else {
                fc.showOpenDialog(this.stage)?.absolutePath ?: ""
            }
        }

        fun printNotification(msg: Any, title: String = "") {
            val notifications = Notifications.create()
            notifications.owner(this.stage)
            notifications.position(Pos.BASELINE_CENTER)
            when(msg) {
                is Exception -> {
                    notifications.title(title.ifEmpty { "Exception" })
                    notifications.text(msg.message)
                    notifications.darkStyle().showError()
                    logger.error(title.ifEmpty { "Exception" }, msg)
                }
                is String -> {
                    notifications.title(title.ifEmpty { msg })
                    notifications.text(msg)
                    notifications.darkStyle().showInformation()
                    logger.info(msg)
                }
            }
        }

        fun printSuccess() {
            printNotification(successText, success)
        }

        fun addStyleSheet(stage: Stage, name: String = "main.css") {
            if(stage.scene != null) {
                val resource = FXHelper::class.java.getResource("$CSS_PATH$name")
                if(resource != null) {
                    stage.scene.stylesheets.add(resource.toExternalForm())
                }
            }
        }

        fun addIcon(stage: Stage, name: String = "icon.png") {
            val stream = FXHelper::class.java.getResourceAsStream("$ICON_PATH$name")
            if(stream != null) {
                val img = Image(stream)
                stage.icons.add(img)
            }
        }

        fun getBundle(force: Boolean = false, locale: Locale = Locale.getDefault()): ResourceBundle {
            if(force) {
                val urls = arrayOf(FXHelper::class.java.getResource(LANG_PATH))
                val loader = URLClassLoader(urls)
                bundle = if(locale.language == Locale.GERMAN.language) {
                    ResourceBundle.getBundle("lang", Locale.GERMAN, loader)
                } else {
                    ResourceBundle.getBundle("lang", Locale.ENGLISH, loader)
                }
                this.success = bundle?.getString("msg.success") ?: ""
                this.successText = bundle?.getString("msg.success.text") ?: ""
                return bundle!!
            } else {
                if(bundle != null) {
                    this.success = bundle?.getString("msg.success") ?: ""
                    this.successText = bundle?.getString("msg.success.text") ?: ""
                    return bundle!!
                } else {
                    val urls = arrayOf(FXHelper::class.java.getResource(LANG_PATH))
                    val loader = URLClassLoader(urls)
                    bundle = if(locale.language == Locale.GERMAN.language) {
                        ResourceBundle.getBundle("lang", Locale.GERMAN, loader)
                    } else {
                        ResourceBundle.getBundle("lang", Locale.ENGLISH, loader)
                    }
                    this.success = bundle?.getString("msg.success") ?: ""
                    this.successText = bundle?.getString("msg.success.text") ?: ""
                    return bundle!!
                }
            }
        }

        fun initSubControllers(mainController: MainController, controllers: List<SubController>) {
            controllers.forEach { it.init(mainController) }
        }

        fun setIcon(cmd: Button, glyph: FontAwesome.Glyph) {
            val icon = FontIcon("fa-${glyph.name.lowercase()}")
            icon.iconSize = 16
            cmd.graphic = icon
        }
    }
}