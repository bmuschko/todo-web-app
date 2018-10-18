pipeline {
    agent any

    triggers {
        pollSCM('*/5 * * * *')
    }

    stages {
        stage('Compile & Unit Tests') {
            steps {
                gradlew('clean', 'test')
            }
        }
        stage('Integration Tests') {
            steps {
                gradlew('integrationTest')
            }
            post {
                always {
                    junit '**/build/test-results/integrationTest/TEST-*.xml'
                }
            }
        }
        stage('Assembly') {
            steps {
                gradlew('assemble')
                stash includes: '**/build/libs/*.war', name: 'app'
            }
        }
        stage('Build & Push Image') {
            environment {
                DOCKER_USERNAME = "${env.DOCKER_USERNAME}"
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
                DOCKER_EMAIL = "${env.DOCKER_EMAIL}"
            }
            steps {
                gradlew('dockerPushImage')
            }
        }
        stage('Deploy to Production') {
            steps {
                timeout(time: 1, unit: 'DAYS') {
                    input 'Deploy to Production?'
                }
                sshagent(credentials: ['ee8346e0-a000-4496-88aa-49977fd97154']) {
                    sh "ssh -o StrictHostKeyChecking=no ${env.DOCKER_SWARM_MANAGER_USERNAME}@${env.DOCKER_SWARM_MANAGER_IP} docker service update --image bmuschko/todo-web-app:latest todo-web-app"
                }
            }
        }
    }
    post {
        failure {
            mail to: 'benjamin.muschko@gmail.com', subject: 'Build failed', body: 'Please fix!'
        }
    }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}