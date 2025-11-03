package pl.przemyslawpitus.luminark.infrastructure

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.InputStream

object YamlFileReader {
    fun <T> readYaml(inputStream: InputStream, toClass: Class<T>): T {
        val loaderOptions = LoaderOptions()

        // 2. Create the Constructor, specifying the target class and the options
        val constructor = Constructor(toClass, loaderOptions)

        // 3. Create the Yaml instance with the Constructor
        val yaml = Yaml(constructor)

        val result = yaml.load<T>(inputStream)

        return result
    }
}