pipeline {
    agent any
    
    tools {
        maven 'Maven3.9.12'
        jdk 'JDK17'
    }
    
    environment {
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'proyecto_tienda_online_spring'
        SONAR_PROJECT_NAME = 'Tienda Online Spring'
        SONAR_TOKEN = credentials('sonarqube-token')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '========================================'
                echo 'OBTENIENDO CODIGO DESDE GITHUB'
                echo '========================================'
                checkout scm
            }
        }
        
        stage('Compile') {
            steps {
                script {
                    echo '========================================'
                    echo 'COMPILANDO PROYECTO'
                    echo '========================================'
                    
                    sh 'java -version'
                    sh 'javac -version'
                    sh 'mvn -version'
                    
                    sh 'mvn clean compile -B -ntp'
                }
            }
            post {
                success {
                    echo 'COMPILACION EXITOSA'
                }
                failure {
                    echo 'ERROR DE COMPILACION'
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    echo '========================================'
                    echo 'EJECUTANDO PRUEBAS'
                    echo '========================================'
                    
                    def hayPruebas = sh(
                        script: 'find src/test -name "*.java" 2>/dev/null | wc -l',
                        returnStdout: true
                    ).trim()
                    
                    if (hayPruebas && hayPruebas.toInteger() > 0) {
                        echo "Se encontraron ${hayPruebas} archivos de prueba"
                        
                        def resultado = sh(
                            script: 'mvn test -B -ntp',
                            returnStatus: true
                        )
                        
                        if (resultado == 0) {
                            echo 'PRUEBAS EXITOSAS'
                        } else {
                            echo 'ALGUNAS PRUEBAS FALLARON'
                            currentBuild.result = 'UNSTABLE'
                        }
                    } else {
                        echo 'NO SE ENCONTRARON PRUEBAS'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, 
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo '========================================'
                    echo 'ANALIZANDO CALIDAD CON SONARQUBE'
                    echo '========================================'
                    
                    def sonarStatus = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' ${SONAR_HOST_URL}",
                        returnStdout: true
                    ).trim()
                    
                    if (sonarStatus == '200') {
                        echo "SonarQube accesible en ${SONAR_HOST_URL}"
                        
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.token=${SONAR_TOKEN} \
                                -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                -Dsonar.projectName='${SONAR_PROJECT_NAME}' \
                                -Dsonar.projectVersion=1.0.${BUILD_NUMBER} \
                                -Dsonar.sources=src/main \
                                -Dsonar.tests=src/test \
                                -Dsonar.java.binaries=target/classes \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                -Dsonar.java.source=17 \
                                -Dsonar.java.target=17
                        """
                        
                        echo "Analisis completado"
                        echo "Ver resultados en: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                    } else {
                        echo "SonarQube no esta accesible (HTTP ${sonarStatus})"
                        echo "Verifica que SonarQube este corriendo en ${SONAR_HOST_URL}"
                    }
                }
            }
            post {
                failure {
                    echo 'ERROR EN ANALISIS DE SONARQUBE'
                    echo 'Verifica:'
                    echo '  - Que SonarQube este corriendo'
                    echo '  - Que el token sea correcto'
                    echo '  - La conectividad de red'
                }
            }
        }
        
        stage('Package') {
            steps {
                script {
                    echo '========================================'
                    echo 'EMPACANDO APLICACION'
                    echo '========================================'
                    
                    sh 'mvn package -DskipTests -B -ntp'
                    
                    def jarFile = sh(
                        script: 'ls target/*.jar | head -1',
                        returnStdout: true
                    ).trim()
                    
                    echo "JAR generado: ${jarFile}"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', 
                                   fingerprint: true,
                                   excludes: 'target/*-sources.jar'
                }
            }
        }
    }
    
    post {
        always {
            echo '========================================'
            echo 'LIMPIANDO WORKSPACE'
            echo '========================================'
            cleanWs()
        }
        success {
            echo '========================================'
            echo 'PIPELINE COMPLETADO CON EXITO'
            echo '========================================'
        }
        failure {
            echo '========================================'
            echo 'PIPELINE FALLO'
            echo '========================================'
        }
        unstable {
            echo '========================================'
            echo 'PIPELINE COMPLETADO CON INESTABILIDADES'
            echo '========================================'
        }
    }
}