def services = ['auth-service']

pipeline {
    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-credential')
    }

    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Deploy config-service') {
            steps {
                script {
                    dir('config-service') {
                        sh "java --version"
                        sh "chmod +x gradlew"
                        sh "./gradlew clean"
                        sh "./gradlew build"
                        archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true

                        sh "docker build -t liardance/config-service:latest ./"
                        sh "docker push liardance/config-service:latest"

                        sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-config-service.yaml"
                    }
                }
            }
        }

        stage('Waiting config pod running') {
            steps {
                sleep(time: 30, unit: 'SECONDS')
            }
        }

        stage('Build and Deploy Other Services') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                            sh "java --version"
                            sh "chmod +x gradlew"
                            sh "./gradlew clean"
                            sh "./gradlew build"
                            archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true

                            sh "docker build -t liardance/${service}:latest ./"
                            sh "docker push liardance/${service}:latest"

                            sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-${service}.yaml"
                        }
                    }
                }
            }
        }
    }
}
