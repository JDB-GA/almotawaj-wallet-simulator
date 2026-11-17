pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    environment {
        AWS_REGION = 'eu-north-1'
        COMPOSE_PROJECT_NAME = 'almotawaj'
        DOCKER_DEFAULT_PLATFORM = 'linux/amd64'
        FRONTEND_URL = 'https://wallet.almotawaj.com'
        FRONTEND_BUCKET = 'almotawaj-frontend-481374029689'
        CLOUDFRONT_ID = 'E1PQXMBPMZPI7D'
    }

    stages {
        stage('Backend: Build and Test') {
            when { changeset 'services/**' }
            parallel {
                stage('identity') { steps { dir('services/identity-service') { sh './mvnw -B verify' } } }
                stage('wallet') { steps { dir('services/wallet-service') { sh './mvnw -B verify' } } }
                stage('notification') { steps { dir('services/notification-service') { sh './mvnw -B verify' } } }
                stage('audit') { steps { dir('services/audit-service') { sh './mvnw -B verify' } } }
                stage('chatbot') { steps { dir('services/chatbot-service') { sh './mvnw -B verify' } } }
                stage('gateway') { steps { dir('services/api-gateway') { sh './mvnw -B verify' } } }
            }
        }

        stage('Backend: Build Images') {
            when { changeset 'services/**' }
            steps {
                sh 'touch .env'
                sh 'docker compose build'
            }
        }

        stage('Backend: Push to ECR') {
            when { changeset 'services/**' }
            steps {
                sh '''
                    ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
                    REGISTRY=$ACCOUNT.dkr.ecr.$AWS_REGION.amazonaws.com
                    aws ecr get-login-password | docker login --username AWS --password-stdin $REGISTRY

                    for SERVICE in identity-service wallet-service notification-service audit-service chatbot-service api-gateway; do
                        docker tag almotawaj-$SERVICE $REGISTRY/almotawaj/$SERVICE:$BUILD_NUMBER
                        docker tag almotawaj-$SERVICE $REGISTRY/almotawaj/$SERVICE:latest
                        docker push $REGISTRY/almotawaj/$SERVICE:$BUILD_NUMBER
                        docker push $REGISTRY/almotawaj/$SERVICE:latest
                    done
                '''
            }
        }

        stage('Backend: Deploy') {
            when {
                anyOf {
                    changeset 'services/**'
                    changeset 'deploy/**'
                    changeset 'Jenkinsfile'
                }
            }
            steps {
                sh '''
                    ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
                    BUCKET=elasticbeanstalk-$AWS_REGION-$ACCOUNT
                    VERSION=build-$BUILD_NUMBER-$(date +%Y%m%d%H%M%S)

                    cd deploy && jar -cfM ../$VERSION.zip docker-compose.yml && cd ..
                    aws s3 cp $VERSION.zip s3://$BUCKET/almotawaj/$VERSION.zip
                    aws elasticbeanstalk create-application-version --application-name almotawaj \
                        --version-label $VERSION --source-bundle S3Bucket=$BUCKET,S3Key=almotawaj/$VERSION.zip
                    aws elasticbeanstalk update-environment --environment-name almotawaj-prod --version-label $VERSION
                '''
            }
        }

        stage('Frontend: Build and Deploy') {
            when { changeset 'frontend/**' }
            steps {
                dir('frontend') {
                    sh '''
                        pnpm install --frozen-lockfile
                        VITE_API_URL=$FRONTEND_URL pnpm build
                        aws s3 sync dist s3://$FRONTEND_BUCKET --delete
                        aws cloudfront create-invalidation --distribution-id $CLOUDFRONT_ID --paths "/*"
                    '''
                }
            }
        }
    }
}
