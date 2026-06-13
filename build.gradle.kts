plugins {
    `java-library`
}

allprojects{
    group = "com.kalimero2.team"
    version = "3.0.0-SNAPSHOT"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(26))
}

subprojects{
    apply{
        plugin("java-library")
    }
    tasks{
        compileJava{
            options.encoding = "UTF-8"
        }
    }
}
