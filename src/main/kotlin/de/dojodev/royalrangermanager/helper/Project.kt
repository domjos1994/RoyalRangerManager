package de.dojodev.royalrangermanager.helper

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


class Project {
    private var path: String
    private var isOpen: Boolean = false
    private val settings = Settings()
    private lateinit var tmp: String
    private val files = mutableListOf<String>()

    init {
        this.path = this.settings.getSetting(Settings.KEY_LAST_PROJECT, "")
        if(this.path.isNotEmpty()) {
            this.open(path)
        }
    }

    companion object {
        private var project: Project? = null

        fun get(): Project {
            if(project != null) {
                return project!!
            } else {
                this.project = Project()
                return this.project!!
            }
        }
    }

    fun isOpen(): Boolean {
        return this.isOpen
    }

    fun getPath(): String {
        return this.path
    }

    fun open(path: String) {
        this.files.clear()
        this.path = path

        val buffer = ByteArray(1024)
        val file = File(path)
        if(file.exists() && file.isFile) {
            this.tmp = this.createTempDirectory()
            val stream = FileInputStream(file)
            val zipStream = ZipInputStream(stream)
            var entry = zipStream.nextEntry
            while(entry != null) {
                val name = this.tmp + File.separatorChar + entry.name
                val oStream = FileOutputStream(name)
                var len = zipStream.read(buffer)
                while( len > 0) {
                    oStream.write(buffer, 0, len)
                    len = zipStream.read(buffer)
                }
                oStream.close()
                this.files.add(entry.name)
                entry = zipStream.nextEntry
            }
            zipStream.close()
            stream.close()
            this.isOpen = true
        } else {
            this.isOpen = false
        }
    }

    fun getProperties(file: String): Properties? {
        if(this.files.contains(file)) {
            val stream = FileInputStream(this.tmp + File.separatorChar + file)
            val properties = Properties()
            properties.load(stream)
            stream.close()
            return properties
        }
        return null
    }

    fun saveProperties(file: String, properties: Properties) {
        if(this.files.contains(file)) {
            val stream = FileOutputStream(file)
            properties.store(stream, "")
            stream.close()
        }
    }

    fun create(path: String) {
        this.files.clear()
        this.path = path

        this.tmp = this.createTempDirectory()
        val inputStream = Project::class.java.getResourceAsStream("/config/config.properties")
        if(inputStream != null) {
            val p = this.tmp + File.separatorChar + "config.properties"
            Files.copy(inputStream, Path.of(File(p).toURI()))
            inputStream.close()
            this.files.add("config.properties")
            this.isOpen = true
        }
    }

    fun save() {
        if(this.isOpen) {
            this.closeZip()
        }
    }

    fun close() {
        if(this.isOpen) {
            this.closeZip()
            this.settings.getSetting(Settings.KEY_LAST_PROJECT, this.path)
            this.isOpen = false
            this.path = ""
            this.files.clear()
        }
    }

    private fun closeZip() {
        val fos = FileOutputStream(this.path)
        val zos = ZipOutputStream(fos)
        val file = File(this.tmp)
        this.zipFile(file, file.name, zos)
        zos.close()
        fos.close()
    }

    @Throws(IOException::class)
    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
        if (fileToZip.isHidden) {
            return
        }
        if (fileToZip.isDirectory) {
            val children = fileToZip.listFiles()
            for (childFile in children!!) {
                zipFile(childFile, childFile.name, zipOut)
            }
            return
        }
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

    private fun createTempDirectory(): String {
        return Files.createTempDirectory(UUID.randomUUID().toString()).toFile().absolutePath
    }
}