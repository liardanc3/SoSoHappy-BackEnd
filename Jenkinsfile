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
        
        stage('Build Docker Images And Push to Registry') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                             sh "docker build -t liardance/${service}:latest ./"
                             sh "docker push liardance/${service}:latest"
                        }
                    }
                }
            }
        }

        stage('Permission'){
            steps{
                script{
                    sh "chmod +rx /root/.kube/config" 
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                            script {
                                sh "kubectl --kubeconfig=/var/lib/jenkins/workspace/config apply -f k8s-${service}.yaml"
                            }
                        }
                    }
                }
            }
        }
    }
}