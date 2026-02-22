pipeline {
    agent any
    
    tools {
        maven 'Maven3.9.12'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Compile') {
            steps {
                sh 'mvn clean compile -B -ntp'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test -B -ntp'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B -ntp'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}