package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.Rule
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.repositories.RuleRepository
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import java.time.LocalDate

class RulesController : SubController() {
    @FXML private lateinit var tblRules: TableView<Rule>

    @FXML private lateinit var txtRuleChildNumber: TextField
    @FXML private lateinit var txtRulePrice: TextField
    @FXML private lateinit var dtRuleStart: DatePicker

    private lateinit var cmdNew: Button
    private lateinit var cmdEdit: Button
    private lateinit var cmdDelete: Button
    private lateinit var cmdCancel: Button
    private lateinit var cmdSave: Button

    private var ruleRepository: RuleRepository? = null
    private var rule: Rule? = null

    override fun initControls() {
        this.ruleRepository = RuleRepository()
        this.tblRules.columns.clear()

        // start
        val startColumn = TableColumn<Rule, LocalDate>()
        startColumn.text = FXHelper.getBundle().getString("rules.start")
        startColumn.cellValueFactory = PropertyValueFactory("start")
        this.tblRules.columns.add(startColumn)

        // child-number
        val numberColumn = TableColumn<Rule, Int>()
        numberColumn.text = FXHelper.getBundle().getString("person.childNumber")
        numberColumn.cellValueFactory = PropertyValueFactory("childNumber")
        this.tblRules.columns.add(numberColumn)

        // price
        val priceColumn = TableColumn<Rule, Double>()
        priceColumn.text = FXHelper.getBundle().getString("rules.price")
        priceColumn.cellValueFactory = PropertyValueFactory("price")
        this.tblRules.columns.add(priceColumn)

        this.control(editMode = false, reset = true)

        this.tblRules.selectionModel.selectedItemProperty().addListener { _,_,it ->
            this.rule = it
        }
    }

    override fun initButtons() {
        this.cmdNew = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS) {
            try {
                this.rule = Rule(0, 0, 0.0, null)
                this.tblRules.selectionModel.clearSelection()
                this.control(true, reset = true)
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdEdit = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.EDIT) {
            try {
                this.control(true, reset = false)
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdDelete = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.MINUS) {
            try {
                if(this.rule != null) {
                    ruleRepository?.deleteRule(this.rule!!)
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdCancel = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.CLOSE) {
            try {
                this.tblRules.selectionModel.clearSelection()
                this.control(false, reset = true)
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdSave = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.SAVE) {
            try {
                if(!this.validator.containsErrors() && this.rule != null) {
                    this.rule?.start = Converter.localDateToDate(this.dtRuleStart.value)
                    this.rule?.price = this.txtRulePrice.text.toDouble()
                    this.rule?.childNumber = this.txtRuleChildNumber.text.toInt()

                    this.ruleRepository?.insertOrUpdate(this.rule!!)
                    this.control(false, reset = true)
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun initBindings() {
        this.cmdEdit.disableProperty().bindBidirectional(this.cmdDelete.disableProperty())
        this.cmdCancel.disableProperty().bindBidirectional(this.cmdSave.disableProperty())

        this.txtRuleChildNumber.disableProperty().bindBidirectional(this.txtRulePrice.disableProperty())
        this.txtRuleChildNumber.disableProperty().bindBidirectional(this.dtRuleStart.disableProperty())
    }

    override fun initValidator() {
        this.validator
            .createCheck()
            .dependsOn("childNumber", this.txtRuleChildNumber.textProperty())
            .withMethod { c ->
                try {
                    val childNumber = c.get<String>("childNumber")
                    if(childNumber.isEmpty()) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataChildNumber"))
                    } else {
                        val no = childNumber.toInt()
                        if(no < 1 || no > 20) {
                            c.error(FXHelper.getBundle().getString("sys.person.dataChildNumber"))
                        }
                    }

                } catch (_: Exception) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataChildNumber"))
                }
            }
            .decorates(this.txtRuleChildNumber)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("price", this.txtRulePrice.textProperty())
            .withMethod { c ->
                try {
                    val price = c.get<String>("price")
                    if(price.isEmpty()) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataPrice"))
                    } else {
                        val pr = price.toDouble()
                        if(pr < 0.0) {
                            c.error(FXHelper.getBundle().getString("sys.person.dataPrice"))
                        }
                    }
                } catch (_: Exception) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataPrice"))
                }
            }
            .decorates(this.txtRulePrice)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("start", this.dtRuleStart.valueProperty())
            .withMethod { c ->
                try {
                    val start = c.get<LocalDate?>("start")
                    if(start == null) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataStart"))
                    }
                } catch (_: Exception) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataStart"))
                }
            }

    }

    private fun control(editMode: Boolean, reset: Boolean) {
        this.cmdNew.isDisable = editMode
        this.cmdEdit.isDisable = editMode
        this.cmdSave.isDisable = !editMode
        this.txtRuleChildNumber.isDisable = !editMode

        if(reset) {
            this.tblRules.selectionModel.clearSelection()
            this.txtRuleChildNumber.text = ""
            this.txtRulePrice.text = ""
            this.dtRuleStart.value = null

            this.tblRules.selectionModel.clearSelection()
            this.tblRules.items.addAll(this.ruleRepository?.getRules() ?: listOf())
        } else {
            this.txtRuleChildNumber.text = rule?.childNumber.toString()
            this.txtRulePrice.text = rule?.price.toString()
            this.dtRuleStart.value = Converter.dateToLocalDate(rule?.start)
        }
    }
}