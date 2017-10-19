
@Library('util')

def myUtils = new util()


node {
        checkout scm
        def startedByWhat = myUtils.jobStartedCause()
        if (startedByWhat == 'user') {
            echo 'Hello World from dev8'
        }
}
