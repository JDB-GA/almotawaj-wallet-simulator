pipeline {
    agent any

    stages {
        stage('Build and Test') {
            parallel {
                stage('identity') { steps { dir('services/identity-service') { sh './mvnw -B verify' } } }
                stage('wallet') { steps { dir('services/wallet-service') { sh './mvnw -B verify' } } }
                stage('notification') { steps { dir('services/notification-service') { sh './mvnw -B verify' } } }
                stage('audit') { steps { dir('services/audit-service') { sh './mvnw -B verify' } } }
                stage('gateway') { steps { dir('services/api-gateway') { sh './mvnw -B verify' } } }
            }
        }

        stage('Build Images') {
            steps {
                sh 'touch .env'
                sh 'docker compose build'
            }
        }
    }
}
