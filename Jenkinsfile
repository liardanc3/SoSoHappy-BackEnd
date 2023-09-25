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

        stage('Build and Deploy Other Services') {
            steps {
                script {
                    for (def serv in services) {
                        dir("${serv}-service") {
                            sh "java --version"
                            sh "chmod +x gradlew"
                            sh "./gradlew clean"
                            sh "./gradlew build"
                            archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true

                            sh "docker build -t liardance/${serv}-service:latest ./"
                            sh "docker push liardance/${serv}-service:latest"

                            sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-${serv}-service.yaml"
sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config rollout restart deployment ${serv}-deployment"
                        }
                    }
                }
            }
        }
    }
}
