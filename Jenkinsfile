
@Library('util')

def startedByWhat = jobStartedByWhat()

node {
        checkout scm
        
        if (startedByWhat == 'user') {
            echo 'Hello World from dev8'
        }
}
