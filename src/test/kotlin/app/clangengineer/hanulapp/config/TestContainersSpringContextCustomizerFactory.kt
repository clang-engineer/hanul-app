package app.clangengineer.hanulapp.config

import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory
import java.util.*

class TestContainersSpringContextCustomizerFactory : ContextCustomizerFactory {

    private val log = LoggerFactory.getLogger(TestContainersSpringContextCustomizerFactory::class.java)

    companion object {
        private var mongoDbBean: MongoDbTestContainer? = null
    }

    override fun createContextCustomizer(
        testClass: Class<*>,
        configAttributes: MutableList<ContextConfigurationAttributes>
    ): ContextCustomizer {
        return ContextCustomizer { context, _ ->
            val beanFactory = context.beanFactory
            var testValues = TestPropertyValues.empty()
            val mongoAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedMongo::class.java)
            if (null != mongoAnnotation) {
                log.debug("detected the EmbeddedMongo annotation on class {}", testClass.name)
                log.info("Warming up the mongo database")
                if (null == mongoDbBean) {
                    mongoDbBean = beanFactory.createBean(MongoDbTestContainer::class.java)
                    beanFactory.registerSingleton(MongoDbTestContainer::class.java.name, mongoDbBean)
                    // (beanFactory as (DefaultListableBeanFactory)).registerDisposableBean(MongoDbTestContainer::class.java.name, mongoDbBean)
                }
                mongoDbBean?.let {
                    testValues = testValues.and("spring.data.mongodb.uri=" + it.getMongoDBContainer().replicaSetUrl)
                }
            }

            testValues.applyTo(context)
        }
    }
}
