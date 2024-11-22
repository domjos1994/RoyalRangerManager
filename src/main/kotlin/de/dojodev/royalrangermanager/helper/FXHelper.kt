package de.dojodev.royalrangermanager.helper

import de.dojodev.royalrangermanager.controller.MainController
import de.dojodev.royalrangermanager.controller.SubController
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.net.URLClassLoader
import java.util.Locale
import java.util.ResourceBundle

@Suppress("MemberVisibilityCanBePrivate")
class FXHelper {

    companion object {
        private const val FXML_PATH = "/fxml/"
        private const val CSS_PATH = "/css/"
        private const val LANG_PATH = "/lang/"
        private const val ICON_PATH = "/icons/"

        private var bundle: ResourceBundle? = null
        private var stage: Stage = Stage()

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
                return bundle!!
            } else {
                if(bundle != null) {
                    return bundle!!
                } else {
                    val urls = arrayOf(FXHelper::class.java.getResource(LANG_PATH))
                    val loader = URLClassLoader(urls)
                    bundle = if(locale.language == Locale.GERMAN.language) {
                        ResourceBundle.getBundle("lang", Locale.GERMAN, loader)
                    } else {
                        ResourceBundle.getBundle("lang", Locale.ENGLISH, loader)
                    }
                    return bundle!!
                }
            }
        }

        fun initSubControllers(mainController: MainController, controllers: List<SubController>) {
            controllers.forEach { it.init(mainController) }
        }
    }
}