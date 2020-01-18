package io.johnsonlee.booster.transform.serviceloader

import com.didiglobal.booster.kotlinx.file
import com.didiglobal.booster.kotlinx.redirect
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.AbstractTransformContext
import com.didiglobal.booster.transform.util.transform
import java.io.File
import kotlin.test.Test

class ServiceLoaderTransformerTest {

    @Test
    fun `test transform service loader`() {
        val transformer = ServiceLoaderTransformer()
        val context = object : AbstractTransformContext(javaClass.name, emptyList(), emptyList(), emptyList()) {}
        val output = File(System.getProperty("user.dir")).file("build", "transform", "TransformerService.class").touch()

        javaClass.getResourceAsStream("TransformerService.class").use { input ->
            input.transform {
                transformer.transform(context, it)
            }
        }.redirect(output)
    }

}