pipeline {

    agent { node { label 'build' } }
    stages {
        stage('checkout') {
            steps {
                checkout scm
            }
        }

        stage('check java') {
            steps {
                sh "java -version"
            }
        }

        stage('clean') {
            steps {
                sh "chmod +x mvnw"
                sh "./mvnw -ntp clean -P-webapp"
            }
        }
        stage('nohttp') {
            steps {
                sh "./mvnw -ntp checkstyle:check"
            }
        }

        stage('backend tests') {
            steps {
                 script {
                try {
                    sh "./mvnw -ntp verify -P-webapp"
                } catch(err) {
                    throw err
                } finally {
                    junit '**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml'
                }
                 }
            }
        }

        stage('packaging') {
            steps {
                sh "./mvnw -ntp verify -P-webapp -Pprod -DskipTests"
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('quality analysis') {
            steps {
                withSonarQubeEnv('Gatway-microservice') {
                    sh "./mvnw -ntp initialize sonar:sonar"
                }
            }
        }

        stage('publish docker') {
            steps {
                sh "./mvnw -ntp -Pprod verify jib:build -Djib.to.image=brahimafa/configapi"
            }
        }
        stage('K8S rollout') {
            steps {
                sh "kubectl rollout restart deployment/configapi -n demo"
            }
        }

    }
}

