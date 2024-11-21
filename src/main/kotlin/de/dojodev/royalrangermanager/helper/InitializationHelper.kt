package de.dojodev.royalrangermanager.helper

import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.FileInputStream
import java.util.Properties

class InitializationHelper {

    companion object {
        private const val config = "/config/db_config.xml"
        private const val propertyFile = "/config/config.properties"

        private var session: SqlSession? = null
        private var properties: String = ""

        fun initBatis(properties: String = "", force: Boolean = false) {
            this.properties = properties
           if(session == null || force) {
               val props = if(properties == "") {
                   val prop = Properties()
                   val stream = InitializationHelper::class.java.getResourceAsStream(propertyFile)
                   if(stream != null) {
                       prop.load(stream)
                       stream.close()
                   }
                   prop
               } else {
                   val prop = Properties()
                   val stream = FileInputStream(properties)
                   prop.load(stream)
                   stream.close()
                   prop
               }
               val stream = InitializationHelper::class.java.getResourceAsStream(config)
               if(stream != null) {
                   val factory = SqlSessionFactoryBuilder().build(stream, props)
                   session = factory.openSession()
                   stream.close()
               }
           }
        }

        fun getSession(): SqlSession? {
            if(session == null) {
                initBatis(this.properties)
            }
            return session
        }
    }
}