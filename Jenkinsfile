def services = ['config-service', 'auth-service']

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

        stage('Build and Deploy Services') {
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
