pipeline {
    agent any
    parameters{
        string(name:'tomcat_dev',defaultValue:'178.79.184.237',description: 'Staging Server')
        string(name:'tomcat_prod',defaultValue:'178.79.191.217',description: 'Product Server')
    }
    triggers{
        pollSCM('* * * * *') // Polling source control
    }

    stages{
        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
            post {
                sucess {
                    echo 'Now archiving ..'
                    archiveArtifacts artifacts:'**/target/*.war'
                }
            }

        }
        stage('Deployments'){

            parallel{
                stage ('Deploy to staging'){
                    steps{
                        sh "scp -i  **/target/*.war root@${params.tomcat_dev}:/opt/tomcat/webapps"
                    }    
                        
                }

                stage ('Deploy to productions'){
                    steps{
                        sh "scp -i  **/target/*.war root@${params.tomcat_prod}:/opt/tomcat/webapps"
                    }    
                        
                }


            }

        }
    }
}