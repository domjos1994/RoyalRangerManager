package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.helper.FXHelper
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.ProgressBar
import javafx.scene.control.RadioButton
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class ApiController : Initializable {
    @FXML private lateinit var rbImport: RadioButton
    @FXML private lateinit var rbExport: RadioButton
    @FXML private lateinit var cmdFile: Button
    @FXML private lateinit var txtFile: TextField
    @FXML private lateinit var cmbType: ComboBox<String>

    @FXML private lateinit var tblColumns: TableView<Item>
    @FXML private lateinit var colNo: TableColumn<Item, Int>
    @FXML private lateinit var colTitle: TableColumn<Item, String>
    @FXML private lateinit var colColumn: TableColumn<Item, String>
    @FXML private lateinit var colValue: TableColumn<Item, String>
    @FXML private lateinit var colColumnValue: TableColumn<Item, String>

    @FXML private lateinit var pbProcess: ProgressBar
    @FXML private lateinit var cmdExecute: Button

    private val items = mutableListOf<String>()

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        this.init()

        this.cmdFile.setOnAction {
            val extensions = if(this.rbExport.isSelected) {
                listOf(
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.txt"), "*.txt"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.csv"), "*.csv"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.xml"), "*.xml"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.pdf"), "*.pdf"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.vcf"), "*.vcf"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.ics"), "*.ics")
                )
            } else {
                listOf(
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.txt"), "*.txt"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.csv"), "*.csv"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.xml"), "*.xml"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.vcf"), "*.vcf"),
                    FileChooser.ExtensionFilter(FXHelper.getBundle().getString("api.file.ics"), "*.ics")
                )
            }
            val title = if(this.rbExport.isSelected)
                FXHelper.getBundle().getString("api.export")
            else
                FXHelper.getBundle().getString("api.import")

            this.txtFile.text = FXHelper.getFileChooser(
                this.rbExport.isSelected,
                "",
                title,
                extensions
            )
        }

        this.txtFile.textProperty().addListener { _,_,value ->
            this.cmdExecute.isDisable = !Files.isRegularFile(Path.of(URI.create(value)))
            this.tblColumns.isVisible = (value.lowercase().endsWith(".txt") || value.lowercase().endsWith(".csv")) && this.rbImport.isSelected
            this.fillTable()
        }

        this.rbExport.selectedProperty().addListener { _ ->
            val value = this.txtFile.text
            this.tblColumns.isVisible = (value.lowercase().endsWith(".txt") || value.lowercase().endsWith(".csv")) && this.rbImport.isSelected
            this.fillTable()
        }
    }

    private fun init() {
        this.items.clear()
        this.items.add(FXHelper.getBundle().getString("person.firstName"))
        this.items.add(FXHelper.getBundle().getString("person.middleName"))
        this.items.add(FXHelper.getBundle().getString("person.lastName"))
        this.items.add(FXHelper.getBundle().getString("user.gender"))
        this.items.add(FXHelper.getBundle().getString("user.email"))
        this.items.add(FXHelper.getBundle().getString("person.birthdate"))
        this.items.add(FXHelper.getBundle().getString("person.medicine"))
        this.items.add(FXHelper.getBundle().getString("person.phone"))
        this.items.add(FXHelper.getBundle().getString("person.street"))
        this.items.add(FXHelper.getBundle().getString("person.locality"))
        this.items.add(FXHelper.getBundle().getString("person.team"))
        this.items.add(FXHelper.getBundle().getString("team.description"))
        this.items.add(FXHelper.getBundle().getString("team.note"))
        this.items.add("")

        this.tblColumns.isEditable = true
        this.colNo.cellValueFactory = PropertyValueFactory("no")
        this.colTitle.cellValueFactory = PropertyValueFactory("title")
        this.colColumn.cellValueFactory = PropertyValueFactory("column")
        this.colColumn.cellFactory = ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(this.items))

        this.cmbType.items.clear()
        this.cmbType.items.add(FXHelper.getBundle().getString("api.type.contacts"))
        this.cmbType.items.add(FXHelper.getBundle().getString("api.type.events"))
        this.cmbType.selectionModel.select(FXHelper.getBundle().getString("api.type.contacts"))

        this.rbImport.isSelected = true
        this.cmdExecute.isDisable = true
        this.tblColumns.isVisible = false
    }

    private fun fillTable() {
        this.tblColumns.items.clear()
        try {
            val content = Files.readString(Path.of(URI.create(this.txtFile.text)))
            val header = content.split("\n")[0]
            val columns = header.split(";")
            var number = 1
            columns.forEach { column ->
                var item = ""
                this.items.forEach { if(it.lowercase().contains(column.lowercase())) item = it}
                this.tblColumns.items.add(Item(number, column, item))
                number += 1
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }

    companion object {

        fun createDialog() {
            val loader = FXMLLoader(LoginController::class.java.getResource("/fxml/api.fxml"), FXHelper.getBundle())
            val root = loader.load<Parent>()
            val scene = Scene(root)
            val stage = Stage()
            stage.title = FXHelper.getBundle().getString("main.system.api")
            stage.initModality(Modality.NONE)
            stage.initStyle(StageStyle.DECORATED)
            stage.initOwner(FXHelper.getStage())
            stage.scene = scene
            stage.showAndWait()
        }
    }

    private data class Item(val no: Int, val title: String, val column: String)
}