package de.dojodev.royalrangermanager.helper

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Class to create, open and save projects
 * @author Dominic Joas
 * @since 1.0
 */
class Project {
    private var path: StringProperty
    private var isOpen: Boolean = false
    private val settings = Settings()
    private lateinit var tmp: String
    private val files = mutableListOf<String>()

    /**
     * Initialize with the last opened project
     * or not
     */
    init {

        // open last project on init
        this.path = SimpleStringProperty("")

        this.path.value = this.settings.getSetting(Settings.KEY_LAST_PROJECT, "")
        if(this.path.value.isNotEmpty()) {
            this.open(path.value)
        }
    }

    companion object {
        private var project: Project? = null

        /**
         * Singleton
         */
        fun get(): Project {
            if(project != null) {
                return project!!
            } else {
                this.project = Project()
                return this.project!!
            }
        }
    }

    /**
     * Shows if project is open
     * @return is project open
     */
    fun isOpen(): Boolean {
        return this.isOpen
    }

    /**
     * Current Path of Project
     * @return path of project
     */
    fun getPath(): StringProperty {
        return this.path
    }

    /**
     * Opens an available Project
     * @param path path to project (*.rrm)
     */
    fun open(path: String) {

        // reset basic data
        this.files.clear()
        this.path.value = path

        val buffer = ByteArray(1024)

        // file exists and is a file not a directory
        val file = File(path)
        if(file.exists() && file.isFile) {

            // create temporary folder
            this.tmp = this.createTempDirectory()
            val stream = FileInputStream(file)

            // open file as zip
            val zipStream = ZipInputStream(stream)
            var entry = zipStream.nextEntry
            while(entry != null) {

                // unpack the files in the zip
                val name = this.tmp + File.separatorChar + entry.name
                val oStream = FileOutputStream(name)
                var len = zipStream.read(buffer)
                while( len > 0) {
                    oStream.write(buffer, 0, len)
                    len = zipStream.read(buffer)
                }
                oStream.close()

                // adds file-name to list
                this.files.add(entry.name)
                entry = zipStream.nextEntry
            }
            zipStream.close()
            stream.close()

            // project is open
            this.isOpen = true
        } else {
            this.isOpen = false
        }
    }

    /**
     * Create a new Project
     * @param path path to project (*.rrm)
     */
    fun create(path: String) {

        // reset basic data
        this.files.clear()
        this.path.value = path

        // create temporary folder
        this.tmp = this.createTempDirectory()

        // read config file from resources
        val inputStream = Project::class.java.getResourceAsStream("/config/config.properties")

        // copy stream to temporary directory
        if(inputStream != null) {
            val p = this.tmp + File.separatorChar + "config.properties"
            Files.copy(inputStream, Path.of(File(p).toURI()))
            inputStream.close()
            this.files.add("config.properties")

            // project is open
            this.isOpen = true
        }
    }

    /**
     * Write back data to project
     */
    fun save() {
        if(this.isOpen) {
            this.closeZip()
        }
    }

    /**
     * Close an open project (*.rrm)
     */
    fun close() {

        // project must be open
        if(this.isOpen) {

            // close zip file
            this.closeZip()

            // write back settings last project and recent list
            val oldSetting = this.settings.getSetting(Settings.KEY_LAST_PROJECT, "")
            if(oldSetting.isNotEmpty()) {
                val recentItems = this.settings.getSetting(Settings.KEY_RECENT_LIST, "")
                val items = recentItems.split(",")

                val newRecent = mutableListOf(oldSetting.trim())
                for(i in 0..3) {
                    if(items.size > i) {
                        if(items[i].trim() != oldSetting.trim()) {
                            newRecent.add(items[i].trim())
                        }
                    }
                }
                this.settings.saveSetting(Settings.KEY_RECENT_LIST, newRecent.joinToString(","))
            }

            // close data
            this.settings.saveSetting(Settings.KEY_LAST_PROJECT, this.path)
            this.isOpen = false
            this.path.value = ""
            this.files.clear()
        }
    }

    /**
     * Get Properties of unzipped file
     * @param file file-name in (*.rrm)
     * @return the properties of the file
     */
    fun getProperties(file: String): Properties? {

        // file exists in temporary folder
        if(this.files.contains(file)) {

            // open properties file
            val stream = FileInputStream(this.tmp + File.separatorChar + file)
            val properties = Properties()
            properties.load(stream)
            stream.close()
            return properties
        }
        return null
    }

    /**
     * Saves the properties in temporary file
     * @param file file-name in (*.rrm)
     * @param properties the new properties
     * @param comment the comment for saving
     */
    fun saveProperties(file: String, properties: Properties, comment: String = "") {

        // file exists in temporary folder
        if(this.files.contains(file)) {

            // write back properties
            val stream = FileOutputStream(this.tmp + File.separatorChar + file)
            properties.store(stream, comment)
            stream.close()
        }
    }

    /**
     * Create the properties in temporary file
     * @param file file-name in (*.rrm)
     * @param properties the new properties
     * @param comment the comment for saving
     */
    fun createProperties(file: String, properties: Properties, comment: String = "") {

        // file exists in temporary folder
        if(!this.files.contains(file)) {

            // write back properties
            val stream = FileOutputStream(this.tmp + File.separatorChar + file)
            properties.store(stream, comment)
            stream.close()
            this.files.add(file)
        }
    }

    /**
     * Closes the zip file
     */
    private fun closeZip() {
        val fos = FileOutputStream(this.path.value)
        val zos = ZipOutputStream(fos)
        val file = File(this.tmp)
        this.zipFile(file, file.name, zos)
        zos.close()
        fos.close()
    }

    /**
     * Closes the zip file and writes back the temporary folder
     * @param fileToZip folder or file
     * @param fileName current file name
     * @param zipOut output-stream of zip
     */
    @Throws(IOException::class)
    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {

        // no hidden file
        if (fileToZip.isHidden) {
            return
        }

        // if directory read files and execute this function
        if (fileToZip.isDirectory) {
            val children = fileToZip.listFiles()
            for (childFile in children!!) {
                zipFile(childFile, childFile.name, zipOut)
            }
            return
        }

        // write back files to zip
        val fis = FileInputStream(fileToZip)
        val zipEntry = ZipEntry(fileName)
        zipOut.putNextEntry(zipEntry)
        val bytes = ByteArray(1024)
        var length: Int
        while ((fis.read(bytes).also { length = it }) >= 0) {
            zipOut.write(bytes, 0, length)
        }
        fis.close()
    }

    /**
     * Create a temporary directory
     * @return the path
     */
    private fun createTempDirectory(): String {
        return Files.createTempDirectory(UUID.randomUUID().toString()).toFile().absolutePath
    }
}