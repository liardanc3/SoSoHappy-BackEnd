def services = ['auth']

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

                        sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config delete deployment config-deployment"
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
                        dir(${service}-service) {
                            sh "java --version"
                            sh "chmod +x gradlew"
                            sh "./gradlew clean"
                            sh "./gradlew build"
                            archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true

                            sh "docker build -t liardance/${service}-service:latest ./"
                            sh "docker push liardance/${service}-service:latest"

                            sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config delete deployment ${service}-deployment"
                            sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-${service}-service.yaml"
                        }
                    }
                }
            }
        }
    }
}
