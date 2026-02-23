pipeline {
    agent any
    
    tools {
        maven 'Maven3.9.12'
        //jdk 'JDK17'  // Especificamos JDK 17
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Obteniendo código desde GitHub...'
                checkout scm
            }
        }
        
        stage('Compile') {
            steps {
                script {
                    echo '========================================'
                    echo 'INICIANDO COMPILACIÓN AUTOMÁTICA'
                    echo '========================================'
                    
                    sh 'java -version'
                    sh 'mvn -version'
                    sh 'javac -version'
                    
                    sh 'mvn clean compile -B -ntp'
                }
            }
            post {
                success {
                    echo ' COMPILACIÓN EXITOSA'
                }
                failure {
                    echo ' ERROR DE COMPILACIÓN'
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    echo '========================================'
                    echo 'EJECUTANDO PRUEBAS AUTOMÁTICAS'
                    echo '========================================'
                    
                    // Verificar si existen pruebas
                    def hayPruebas = sh(
                        script: 'find src/test -name "*.java" 2>/dev/null | wc -l',
                        returnStdout: true
                    ).trim()
                    
                    if (hayPruebas && hayPruebas.toInteger() > 0) {
                        echo " Se encontraron ${hayPruebas} archivos de prueba"
                        
                        // Ejecutar pruebas y capturar resultado
                        def resultado = sh(
                            script: 'mvn test -B -ntp',
                            returnStatus: true
                        )
                        
                        if (resultado == 0) {
                            echo ' PRUEBAS EXITOSAS'
                        } else {
                            echo ' ALGUNAS PRUEBAS FALLARON'
                            currentBuild.result = 'UNSTABLE'
                        }
                    } else {
                        echo ' NO SE ENCONTRARON PRUEBAS'
                        echo 'Considera agregar pruebas unitarias al proyecto'
                    }
                }
            }
            post {
                always {
                    // Publicar reportes JUnit
                    junit allowEmptyResults: true, 
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                script {
                    echo '========================================'
                    echo 'EMPACANDO APLICACIÓN'
                    echo '========================================'
                    
                    sh 'mvn package -DskipTests -B -ntp'
                    
                    // Verificar que el JAR se creó
                    def jarFile = sh(
                        script: 'ls target/*.jar | head -1',
                        returnStdout: true
                    ).trim()
                    
                    echo "JAR generado: ${jarFile}"
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo '========================================'
                    echo 'ANÁLISIS CON SONARQUBE'
                    echo '========================================'
                    
                    // Verificar si hay configuración de SonarQube
                    if (fileExists('sonar-project.properties') || fileExists('pom.xml')) {
                        sh 'mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=admin'
                    } else {
                        echo ' No se encontró configuración para SonarQube'
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo ' Limpiando workspace...'
            cleanWs()
        }
        success {
            echo 'PIPELINE COMPLETADO CON ÉXITO '
        }
        failure {
            echo 'PIPELINE FALLÓ'
        }
        unstable {
            echo 'PIPELINE COMPLETADO CON INESTABILIDADES '
        }
    }
}