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
        stage('Assemble') {
            steps {
                gradlew('assemble')
                stash includes: '**/build/libs/*.war', name: 'app'
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