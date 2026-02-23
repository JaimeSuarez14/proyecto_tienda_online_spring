pipeline {
    agent any
    
    tools {
        maven 'Maven3.9.12'
    }
    
    environment {
        // SonarQube configuration
        SONAR_HOST_URL = 'http://3.144.227.32:9000'  // URL de tu SonarQube
        SONAR_PROJECT_KEY = 'proyecto_tienda_online_spring'
        SONAR_PROJECT_NAME = 'Tienda Online Spring'
        SONAR_TOKEN = credentials('sonarqube-token')  // Lo crearás en Jenkins
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
                    
                    sh 'java -version'  // Mostrará Java 21
                    sh 'mvn -version'
                    
                    sh 'mvn clean compile -B -ntp'
                }
            }
            post {
                success {
                    echo 'COMPILACIÓN EXITOSA'
                }
                failure {
                    echo 'ERROR DE COMPILACIÓN'
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
                        script: 'find src/test -name "*.java" | wc -l',
                        returnStdout: true
                    ).trim()
                    
                    if (hayPruebas.toInteger() > 0) {
                        echo "Se encontraron ${hayPruebas} archivos de prueba"
                        
                        // Ejecutar pruebas y capturar resultado
                        def resultado = sh(
                            script: 'mvn test -B -ntp',
                            returnStatus: true
                        )
                        
                        if (resultado == 0) {
                            echo 'PRUEBAS EXITOSAS'
                        } else {
                            echo 'ALGUNAS PRUEBAS FALLARON'
                            // Marcar como inestable pero continuar
                            currentBuild.result = 'UNSTABLE'
                        }
                    } else {
                        echo 'NO SE ENCONTRARON PRUEBAS'
                        echo 'Considera agregar pruebas unitarias al proyecto'
                    }
                }
            }
            post {
                always {
                    // Publicar reportes si existen
                    junit allowEmptyResults: true, 
                          testResults: 'target/surefire-reports/*.xml'
                    
                    // Mostrar resumen
                    script {
                        if (fileExists('target/surefire-reports')) {
                            echo 'Reportes de pruebas publicados'
                        } else {
                            echo 'No hay reportes de pruebas para publicar'
                        }
                    }
                }
            }
        }
        
        // ===== NUEVO: ANÁLISIS DE SONARQUBE CON JAVA 17 =====
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo '========================================'
                    echo 'ANALIZANDO CALIDAD DE CÓDIGO CON SONARQUBE'
                    echo '========================================'
                    
                    // Usar Java 17 específicamente para SonarQube
                    withEnv(['JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64', 
                             'PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin']) {
                        
                        sh 'java -version'  // Debe mostrar Java 17
                        
                        // Ejecutar análisis de SonarQube
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.login=${SONAR_TOKEN} \
                                -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                                -Dsonar.projectName='${SONAR_PROJECT_NAME}' \
                                -Dsonar.projectVersion=1.0.${BUILD_NUMBER} \
                                -Dsonar.sources=src/main \
                                -Dsonar.tests=src/test \
                                -Dsonar.java.binaries=target/classes \
                                -Dsonar.java.source=21 \
                                -Dsonar.java.target=21 \
                                -Dsonar.java.jdkHome=/usr/lib/jvm/java-21-openjdk-amd64 \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
            post {
                success {
                    echo 'ANÁLISIS DE SONARQUBE COMPLETADO'
                    echo "Ver resultados en: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                }
                failure {
                    echo 'ERROR EN ANÁLISIS DE SONARQUBE'
                }
            }
        }
        
        // ===== NUEVO: QUALITY GATE (opcional) =====
        stage('Quality Gate') {
            steps {
                script {
                    echo '========================================'
                    echo 'VERIFICANDO QUALITY GATE'
                    echo '========================================'
                    
                    // Esperar resultado de SonarQube (requiere webhook configurado)
                    timeout(time: 5, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline abortado por Quality Gate: ${qg.status}"
                        }
                    }
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '========================================'
                echo 'EMPACANDO APLICACIÓN'
                echo '========================================'
                sh 'mvn package -DskipTests -B -ntp'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo '========================================'
            echo 'PIPELINE COMPLETADO CON ÉXITO'
            echo '========================================'
        }
        failure {
            echo '========================================'
            echo 'PIPELINE FALLÓ'
            echo '========================================'
        }
        unstable {
            echo '========================================'
            echo 'PIPELINE INESTABLE (PRUEBAS FALLARON)'
            echo '========================================'
        }
    }
}
