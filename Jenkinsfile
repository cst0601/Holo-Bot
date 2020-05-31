Jenkinsfile (Declarative Pipeline)
pipeline {
    agent { docker { image 'maven:3.5.3' } }
    stages {
        stage('build') {
            steps {
            	sh 'echo "Start building..."'
                sh 'mvn --version'
            }
        }
        stage('Test') {
        	steps {
        		echo 'start testing...'
        	}
        }
    }
    post {
    	always {
    		echo 'This will always run'
    	}
    }
}

