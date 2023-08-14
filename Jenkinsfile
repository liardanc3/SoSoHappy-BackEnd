def services = ['config-service', 'dm-service']

pipeline {
    environment{
        DOCKERHUB_CREDENTIALS = credentials('docker-credential')
    }

    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build jar') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                            sh "java --version"
                            sh "chmod +x gradlew"
                            sh "./gradlew clean"
                            sh "./gradlew build"
                            archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true
                        }
                    }
                }
            }
        }

        stage('Docker Login'){
            steps{
                script{
                    sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin"
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                             sh "docker push liardance/${service}:latest"
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                            script {
                                sh "kubectl apply -f k8s-${service}.yaml"
                            }
                        }
                    }
                }
            }
        }
    }
}