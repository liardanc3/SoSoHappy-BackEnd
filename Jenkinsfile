def services = ['config-service', 'dm-service']

pipeline {
    agent any

    tools {
    	jdk 'Java17'
    }

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
                            sh "./gradlew build --stacktrace"
                            archiveArtifacts artifacts: "**/build/libs/*.jar", allowEmptyArchive: true
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    for (def service in services) {
                        dir(service) {
                            def dockerImage = docker.build "liardance/${service}:latest"
                            dockerImage.push()
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