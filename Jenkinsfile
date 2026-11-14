pipeline {
    agent any

    environment {
        AWS_REGION = 'eu-north-1'
        COMPOSE_PROJECT_NAME = 'almotawaj'
        DOCKER_DEFAULT_PLATFORM = 'linux/amd64'
    }

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

        stage('Push to ECR') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'aws-jenkins',
                        usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    sh '''
                        ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
                        REGISTRY=$ACCOUNT.dkr.ecr.$AWS_REGION.amazonaws.com
                        aws ecr get-login-password | docker login --username AWS --password-stdin $REGISTRY

                        for SERVICE in identity-service wallet-service notification-service audit-service api-gateway; do
                            docker tag almotawaj-$SERVICE $REGISTRY/almotawaj/$SERVICE:$BUILD_NUMBER
                            docker tag almotawaj-$SERVICE $REGISTRY/almotawaj/$SERVICE:latest
                            docker push $REGISTRY/almotawaj/$SERVICE:$BUILD_NUMBER
                            docker push $REGISTRY/almotawaj/$SERVICE:latest
                        done
                    '''
                }
            }
        }
    }
}