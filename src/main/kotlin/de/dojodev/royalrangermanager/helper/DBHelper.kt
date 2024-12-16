package de.dojodev.royalrangermanager.helper

import de.dojodev.royalrangermanager.db.mapper.PropertyMapper
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.util.Properties

class DBHelper {

    companion object {
        private const val CONFIG = "/config/db_config.xml"
        private var session: SqlSession? = null

        fun initBatis() {
            val props = this.getProperties()
            val stream = DBHelper::class.java.getResourceAsStream(CONFIG)
            if(stream != null) {
                val factory = SqlSessionFactoryBuilder().build(stream, props)
                session = factory.openSession(true)
                stream.close()
                this.init()
            }
        }

        fun close() {
            if(this.session != null) {
                this.session?.commit()
                this.session?.close()
                this.session = null
            }
        }

        fun getSession(): SqlSession? {
            if(session == null) {
                initBatis()
            }
            return session
        }

        private fun init() {

            // if properties exists show version if not set version to 0
            val driver = this.getProperties()?.getProperty("driver") ?: ""
            var version = 0
            try {
                if(driver.isNotEmpty()) {
                    val mapper = session?.getMapper(PropertyMapper::class.java)
                    val items = mapper?.getProperty("version")
                    val item = items?.get(0)
                    version = item?.value?.toInt() ?: 0
                }
            } catch (_: Exception) {}

            val part = when(driver) {
                "org.sqlite.JDBC" -> "sqlite"
                "com.mysql.jdbc.Driver" -> "mysql"
                else -> return
            }

            // execute init
            if(version == 0) {
                val init = DBHelper::class.java.getResourceAsStream("/sql/init_$part.sql")
                this.runScript(init!!)
            }

            // execute update
            var stream: InputStream?
            var cv = version + 1
            do {
                stream = DBHelper::class.java.getResourceAsStream("/sql/${cv}_$part.sql")
                if(stream != null) {
                    this.runScript(stream)
                }
                cv++
            } while (stream != null)
        }

        private fun getProperties(): Properties? {
            val project = Project.get()
            return project.getProperties("config.properties")
        }

        private fun runScript(stream: InputStream) {
            val runner = ScriptRunner(session?.connection)
            val reader = InputStreamReader(stream)
            runner.setSendFullScript(false)
            runner.setErrorLogWriter(PrintWriter(System.out))
            runner.setStopOnError(true)
            runner.setDelimiter(";")
            runner.setAutoCommit(true)
            runner.runScript(reader)
        }
    }
}